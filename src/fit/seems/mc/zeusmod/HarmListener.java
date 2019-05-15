package fit.seems.mc.zeusmod;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.bukkit.Location;

public class HarmListener implements Listener
{
	ZeusModPlugin plugin;

	public HarmListener(ZeusModPlugin plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		Entity damagerEntity = event.getDamager();
		if (damagerEntity instanceof Player)
		{
			Player damager = (Player)damagerEntity;
			PlayerPreset preset = plugin.getPreset(damager);
			if (preset == null || !preset.isActive())
			{
				return;
			}

			Entity victimEntity = event.getEntity();

			// reset damage
			event.setDamage(0);

			// generate knockback
			Vector knockback = damager.getLocation().getDirection();

			knockback
				.setY(Math.max(knockback.getY(), 0.1))
				.normalize()
				.multiply(5);

			victimEntity.setVelocity(knockback);

			// manually insta-kill stuff
			if (victimEntity instanceof Damageable)
			{
				if (victimEntity instanceof Player)
				{
					// strike lightning effect whenever attacking players
					Location location = victimEntity.getLocation();
					location.getWorld().strikeLightningEffect(location);

					// give worthy players immunity
					Player victim = (Player)victimEntity;
					if (plugin.getPreset(victim) == null)
					{
						return;
					}
				}

				// kill
				((Damageable)victimEntity).setHealth(0);
			}
		}
	}
}
