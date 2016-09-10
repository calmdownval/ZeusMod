package com.sunkenbridge.val.ZeusMod;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerItemHeldListener implements Listener {

	ZeusModPlugin plugin;
	
	public PlayerItemHeldListener(ZeusModPlugin plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
	
		Player player = event.getPlayer();
		if (player.isOp()) {
    	
    		SpeedPreset preset = plugin.getPreset(player);
    		try {
        		
	    		if (player.getInventory().getItem(event.getNewSlot()).getType().equals(ZeusModPlugin.controlItem))
	    			preset.activate();
	    		
	    		else
	    			preset.deactivate();
    		}
    		catch (Exception e) { }
    	}
	}
}
