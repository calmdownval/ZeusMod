package fit.seems.mc.zeusmod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommandExecutor implements CommandExecutor
{
	ZeusModPlugin plugin;

	public ResetCommandExecutor(ZeusModPlugin plugin)
	{
		this.plugin = plugin;
		plugin.getCommand("zreset").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("only available to players");
			return false;
		}

		if (args.length != 0)
		{
			sender.sendMessage("unexpected extra arguments");
			return false;
		}

		PlayerPreset preset = plugin.getPreset((Player)sender);
		if (preset != null)
		{
			preset.reset();
		}

		return true;
	}
}
