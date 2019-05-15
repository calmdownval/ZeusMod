package fit.seems.mc.zeusmod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommandExecutor implements CommandExecutor
{
	ZeusModPlugin plugin;

	public SpeedCommandExecutor(ZeusModPlugin plugin)
	{
		this.plugin = plugin;
		plugin.getCommand("zfly").setExecutor(this);
		plugin.getCommand("zwalk").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("only available to players");
			return false;
		}

		if (args.length != 1)
		{
			sender.sendMessage("expected exactly one argument");
			return false;
		}

		PlayerPreset preset = plugin.getPreset((Player)sender);
		if (preset != null)
		{
			try
			{
				float speed = Math.max(Math.min(Float.parseFloat(args[0]) / 100f, 1.0f), 0.1f);
				if (command.getName().equals("zwalk"))
				{
					preset.setZeusWalkSpeed(speed);
					sender.sendMessage("set zeus-walk speed to " + Math.round(speed * 100f));
				}
				else
				{
					preset.setZeusFlySpeed(speed);
					sender.sendMessage("set zeus-fly speed to " + Math.round(speed * 100f));
				}
			}
			catch (Exception e)
			{
				sender.sendMessage("expected a number between 10 and 100");
				return false;
			}
		}

		return true;
	}
}
