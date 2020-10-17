package fit.seems.mc.zeusmod;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.HashSet;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.World;

public class InteractionListener implements Listener {
	private final ZeusModPlugin plugin;

	private static final int MIN_PASSABLE_BETWEEN = 2;
	private static final HashSet<Material> TRANSPARENT = new HashSet<Material>();
	static {
		TRANSPARENT.add(Material.AIR);
		TRANSPARENT.add(Material.WATER);
		TRANSPARENT.add(Material.GRASS);
		TRANSPARENT.add(Material.LAVA);
		TRANSPARENT.add(Material.FIRE);
	}

	public InteractionListener(ZeusModPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event) {
		Entity victim = event.getEntity();
		if (victim instanceof Player && plugin.verify((Player)victim)) {
			event.setDamage(0D);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity victim = event.getEntity();
		if (victim instanceof Player && plugin.verify((Player)victim)) {
			event.setDamage(0D);
		}

		Entity damager = event.getDamager();
		if (damager instanceof Player) {
			Player player = (Player)damager;
			if (plugin.verify(player) && player.hasPermission(ZeusModPlugin.P_STRIKE)) {
				strike(player, player.getLocation(), Collections.singleton(victim));
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!plugin.verify(player)) {
			return;
		}

		switch (event.getAction()) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				if (player.hasPermission(ZeusModPlugin.P_STRIKE)) {
					strikeArea(player);
				}
				break;

			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				if (player.hasPermission(ZeusModPlugin.P_BLINK)) {
					blink(player);
				}
				break;
		}
	}

	private boolean blink(Player actor) {
		int minDistance = plugin.getMinBlinkDistance();
		int maxDistance = plugin.getMaxBlinkDistance();
		int passableLength = MIN_PASSABLE_BETWEEN;
		int targetOffset = -1;
		boolean foundObstacle = false;
		Location oldLocation = actor.getEyeLocation();
		Vector position = oldLocation.toVector();
		Vector direction = oldLocation.getDirection();
		World world = actor.getWorld();

		for (int i = 0; i < maxDistance; ++i) {
			position.add(direction);
			Block block = world.getBlockAt(
				position.getBlockX(),
				position.getBlockY(),
				position.getBlockZ());
		
			if (block.isPassable() && block.getRelative(0, -1, 0).isPassable()) {
				int currentMinDistance = foundObstacle ? MIN_PASSABLE_BETWEEN : minDistance;

				// make sure we've moved enough distance from a wall
				if (++passableLength > currentMinDistance) {
					targetOffset = i;

					// we only want to pass one wall at a time
					if (foundObstacle) {
						break;
					}
				}
			}
			else {
				foundObstacle = true;
				passableLength = 0;
			}
		}

		// measure distance to the new location
		Location newLocation = targetOffset == -1
			? oldLocation
			: oldLocation
				.clone()
				.add(direction
					.clone()
					.multiply(targetOffset));

		double distance = oldLocation.clone().subtract(newLocation).length();
		if (targetOffset == -1 || (distance < minDistance && !foundObstacle)) {
			// spawn particle in a place visible to the player
			Location particleLocation = actor
				.getEyeLocation()
				.add(direction
					.clone()
					.multiply(Math.max(targetOffset - 1D, 0.5D)));

			actor.spawnParticle(Particle.BARRIER, particleLocation, 1);
			actor.playSound(oldLocation, Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.MASTER, 1F, 1F);
			return false;
		}

		actor.teleport(newLocation, TeleportCause.PLUGIN);
		actor.playSound(newLocation, Sound.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.MASTER, 1F, 1F);
		return true;
	}

	private void strikeArea(Player actor) {
		if (!actor.hasPermission(ZeusModPlugin.P_STRIKE)) {
			return;
		}

		Block targetBlock = actor.getTargetBlock(TRANSPARENT, 69); // haha funny number
		if (TRANSPARENT.contains(targetBlock.getType())) {
			return;
		}

		Location target = targetBlock.getLocation();
		double radius = plugin.getStrikeRadius();
		BoundingBox box = new BoundingBox(
			target.getX() - radius,
			target.getY() - radius,
			target.getZ() - radius,
			target.getX() + radius,
			target.getY() + radius,
			target.getZ() + radius);

		strike(actor, target, actor.getWorld().getNearbyEntities(box));
	}

	private void strike(Player actor, Location impactLocation, Iterable<Entity> targets) {
		boolean canStrikePlayers = actor.hasPermission(ZeusModPlugin.P_STRIKE_PLAYER);

		// we are actually matching a box, not sphere
		// the max distance from epicenter is therefore d = r * sqrt(2)
		double maxDistance = plugin.getStrikeRadius() * 1.41421356D;
		double maxMagnitude = plugin.getStrikeKnockBack();

		// move the epicenter down a little to make entities gain vertical or
		// horizontal momentum proportional to the distance from the impact
		Vector epicenter = impactLocation.toVector();
		epicenter.setY(epicenter.getY() - 1.5D);

		for (Entity target : targets) {

				// avoid hurting ourselves
			if (  target == actor ||
				// skip non-damageable entities
				!(target instanceof Damageable) ||
				// skip players when not enabled or when they're also in ZeusMode
				 (target instanceof Player && (!canStrikePlayers || plugin.verify((Player)target)))) {
				continue;
			}

			// prepare the knock back vector
			Vector knockBack = target.getLocation().toVector();

			// solve the 2D distance
			double x = knockBack.getX() - epicenter.getX();
			double z = knockBack.getZ() - epicenter.getZ();

			// scale the knock back magnitude by distance from epicenter
			double magnitude = maxMagnitude * (1D - Math.sqrt(x * x + z * z) / maxDistance);

			// finish the knock back vector
			knockBack
				.subtract(epicenter)
				.normalize()
				.multiply(magnitude);

			// apply knock back and damage
			((Damageable)target).setHealth(0D);
			target.setVelocity(knockBack);
		}

		if (plugin.getStrikeLightning()) {
			impactLocation.getWorld().strikeLightningEffect(impactLocation);
		}
	}
}
