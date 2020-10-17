package fit.seems.mc.zeusmod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class ZeusModCommandExecutor implements CommandExecutor {
	private final ZeusModPlugin plugin;

	public ZeusModCommandExecutor(ZeusModPlugin plugin) {
		this.plugin = plugin;

		PluginCommand command = plugin.getCommand("zeus");
		command.setTabCompleter(new ZeusModTabCompleter());
		command.setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				throw new RuntimeException("this command is only available to players");
			}

			if (args.length == 0) {
				throw new RuntimeException("missing first argument");
			}

			PlayerPreset preset = plugin.getPreset((Player)sender);
			if (preset == null) {
				throw new RuntimeException("you are not allowed to use this plugin");
			}

			switch (args[0]) {
				case "fly":
				case "run": {
					if (args.length == 1) {
						throw new RuntimeException("missing second argument");
					}

					float modifier = Float.parseFloat(args[1]) / 100F;
					if (args[0].equals("fly")) {
						preset.setFlySpeedModifier(modifier);
					}
					else {
						preset.setRunSpeedModifier(modifier);
					}

					ZeusModPlugin.sendFormattedMessage(sender, "speed preset updated");
					break;
				}

				case "reset":
					preset.reset();
					ZeusModPlugin.sendFormattedMessage(sender, "restored defaults");
					break;

				case "sunny": {
					boolean isSunny = preset.toggleSunny();
					ZeusModPlugin.sendFormattedMessage(sender, "weather toggled " + (isSunny ? "to always sunny" : "back to world"));
					break;
				}

				default:
					throw new RuntimeException("unknown sub-command");
			}
		}
		catch (RuntimeException ex) {
			ZeusModPlugin.sendFormattedMessage(sender, ex.getMessage());
			return false;
		}

		return true;
	}
}
