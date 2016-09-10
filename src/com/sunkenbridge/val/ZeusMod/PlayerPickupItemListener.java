package com.sunkenbridge.val.ZeusMod;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {
	
	ZeusModPlugin plugin;
	
	public PlayerPickupItemListener(ZeusModPlugin plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
    
	@EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    	
    	Player player = event.getPlayer();
		if (player.isOp()) {
		
			try {
				
				if (event.getItem().getType().equals(ZeusModPlugin.controlItem)) {
					
					plugin.scheduleUpdate(plugin.getPreset(player));
				}
			}
			catch (Exception e) { }
	    }
    }
}