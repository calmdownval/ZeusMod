package com.sunkenbridge.val.ZeusMod;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {
	
	ZeusModPlugin plugin;
	
	public InventoryCloseListener(ZeusModPlugin plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
    
	@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    	
    	HumanEntity entity = event.getPlayer();
		if (entity instanceof Player) {
			
			Player player = (Player)entity;
			if (player.isOp()) {
		    	
	    		plugin.getPreset(player).update();
	    	}
		}
    }
}
