package com.sunkenbridge.val.ZeusMod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZeusModCommandExecutor implements CommandExecutor {

	ZeusModPlugin plugin;
	
	public ZeusModCommandExecutor(ZeusModPlugin plugin) {
		
		this.plugin = plugin;
		plugin.getCommand("zspeed").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
		if (sender instanceof Player) {
		
			Player player = (Player)sender;
			if (player.isOp()) {
				
				SpeedPreset preset = plugin.getPreset(player);
				try {
					
					float walk, fly;
					if (args.length == 1) {
					
						walk = fly = parseFloat(args[0]);
					}
					else if (args.length == 2) {
					
						walk = parseFloat(args[0]);
						fly = parseFloat(args[1]);
					}
					else {
					
						plugin.sendMessage(player, "Wrong number of arguments.");
						return true;
					}
					
					preset.setZeusWalkSpeed(walk);
					plugin.sendMessage(player, "walk speed set to " + Math.round(walk * 100f) + "%");
					
					preset.setZeusFlySpeed(fly);
					plugin.sendMessage(player, "fly speed set to " + Math.round(fly * 100f) + "%");
				}
				catch (Exception e) {
					
					plugin.sendMessage(player, "Invalid numeric value.");
				}
			}
			else
				plugin.sendMessage(player, "You must be an OP to use this command.");
			
			return true;
		}
		
		return false;
	}
	
	float parseFloat(String s) {
		
		return Math.max(Math.min(Float.parseFloat(s) / 100f, 1.0f), 0.1f);
	}
}