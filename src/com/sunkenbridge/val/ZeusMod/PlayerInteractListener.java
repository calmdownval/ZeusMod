package com.sunkenbridge.val.ZeusMod;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
	
	private static final HashSet<Byte> transparent = new HashSet<Byte>(Arrays.asList(new Byte[] {
			
			0, // air
			8, 9, // water
			10, 11 //lava
	}));
	
	private ZeusModPlugin plugin;
	
	public PlayerInteractListener(ZeusModPlugin plugin) {
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
    
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	
    	Player player = event.getPlayer();
    	if (player.isOp()) {
    	
    		try {
    		
	    		if (event.getItem().getType().equals(ZeusModPlugin.controlItem)) {
	    		
	        		Action ac = event.getAction();
	        		if (ac == Action.LEFT_CLICK_BLOCK || ac == Action.LEFT_CLICK_AIR) {
	    			
	        			Location target = player.getTargetBlock(transparent, 500).getLocation();
	    				plugin.strikeLightning(target);
		        		
		        		event.setCancelled(true);
	    			}
	    			else if (ac == Action.RIGHT_CLICK_BLOCK || ac == Action.RIGHT_CLICK_AIR) {
	    			
	    				Location target = player.getTargetBlock(transparent, 30).getLocation();
	    				if (target != null) {

	    					plugin.teleportPlayer(player, target);
	    				}
	    				else {
	    				
	    					plugin.sendMessage(player, "No block in view (or too far).");
	    				}
	    			}
	    		}
    		}
    		catch (Exception e) { }
    	}
    }
}