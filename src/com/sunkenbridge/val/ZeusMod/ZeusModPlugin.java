package com.sunkenbridge.val.ZeusMod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class ZeusModPlugin extends JavaPlugin {

	private static final Vector
		half = new Vector(0.5, 0, 0.5),
		up1 = new Vector(0, 1, 0),
		up5 = new Vector(0, 5, 0);
	
	@SuppressWarnings("serial")
	private static final ArrayList<Material> transparent = new ArrayList<Material>()
			{{
				add(Material.AIR);
				add(Material.WATER);
				add(Material.STATIONARY_WATER);
				add(Material.LAVA);
				add(Material.STATIONARY_LAVA);
			}};
	
	public static Material controlItem = Material.GOLD_BOOTS;
	
	HashMap<Player, SpeedPreset> speedPresets = new HashMap<Player, SpeedPreset>();
	
	public SpeedPreset getPreset(Player player) {
	
		if (speedPresets.containsKey(player))
			return speedPresets.get(player);
		
		else {
			
			SpeedPreset preset = new SpeedPreset(player);
			speedPresets.put(player, preset);
			
			return preset;
		}
	}
	
	public void scheduleUpdate(final SpeedPreset preset) {
		
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			 
			  public void run() {
				  
				  preset.update();
			  }
			  
		}, 20);
	}
	
	public void strikeLightning(Location target, int radius) {

		World world = target.getWorld();
		radius *= radius;
		
		// kill nerby entities
		Collection<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class);
		for (LivingEntity entity : entities) {
			
			// don't kill OPs
			if (entity instanceof Player)
				if (((Player)entity).isOp())
					continue;
			
			// kill it
			if (entity.getLocation().distanceSquared(target) < radius) {
				
				entity.teleport(entity.getLocation().add(up5));
				entity.setFallDistance(100);
				entity.setHealth(0);
			}
		}
		
		// destroy items & arrows
		Collection<Item> items = world.getEntitiesByClass(Item.class);
		for (Item item : items)
			if (item.getLocation().distanceSquared(target) < radius)
				item.remove();

		Collection<Projectile> projectiles = world.getEntitiesByClass(Projectile.class);
		for (Projectile projectile : projectiles)
			if (projectile.getLocation().distanceSquared(target) < radius)
				projectile.remove();
		
		// strike lightning
		world.strikeLightningEffect(target);
	}
	
	public void strikeLightning(Location target) {
		
		strikeLightning(target, 5);
	}
	
	public void teleportPlayer(Player player, Location dest) {
	
		World
			world = dest.getWorld();
		
		Location
			from = player.getLocation(),
			temp1 = dest.clone().subtract(up1),
			temp2 = dest.clone();
		
		Material
			test1 = world.getBlockAt(temp1).getType(),
			test2 = world.getBlockAt(temp2).getType();
		
		Vector
			step = dest.toVector().subtract(from.toVector()).normalize();
		
		int i = 0;
		while (
			(test1.isBlock() && !transparent.contains(test1)) ||
			(test2.isBlock() && !transparent.contains(test2))) {
			
			if (++i > 5) {
				
				temp1 = dest.subtract(step).subtract(step);
				break;
			}
			temp1.add(step);
			test1 = world.getBlockAt(temp1).getType();
			temp2.add(step);
			test2 = world.getBlockAt(temp2).getType();
		}
		
		temp1.add(half);
		temp1.setDirection(from.getDirection());
		
		player.teleport(temp1);
		player.setFallDistance(0);
	}
	
	public void sendMessage(Player player, String message) {
		
		player.sendMessage("§a[ZeusMod]: §e" + message);
	}
	
	@Override
	public void onEnable() {

    	// save a copy of the default config.yml if one is not there
        saveDefaultConfig();

        // load config
        FileConfiguration config = getConfig();
        controlItem = Material.getMaterial(config.getInt("control", 317));
        
        // subscribe events & commands
		new PlayerInteractListener(this);
		new PlayerItemHeldListener(this);
		new InventoryCloseListener(this);
		new PlayerDropItemListener(this);
		new PlayerPickupItemListener(this);
		
		new ZeusModCommandExecutor(this);
	}
}
