package com.sunkenbridge.val.ZeusMod;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
	
	ZeusModPlugin plugin;
	
	public PlayerDropItemListener(ZeusModPlugin plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
    
	@EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
    	
    	Player player = event.getPlayer();
		if (player.isOp()) {
		    
	    	plugin.getPreset(player).update();
	    }
    }
}
