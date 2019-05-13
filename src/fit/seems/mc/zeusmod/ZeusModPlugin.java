package fit.seems.mc.zeusmod;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZeusModPlugin extends JavaPlugin
{
	private Material controlItem = Material.GOLDEN_SWORD;

	private HashMap<Player, SpeedPreset> speedPresets = new HashMap<Player, SpeedPreset>();

	private HashSet<SpeedPreset> active = new HashSet<SpeedPreset>();

	private int taskId = -1;


	@Override
	public void onEnable()
	{
		FileConfiguration config = getConfig();

		// get the control item type
		ItemStack stack = config.getItemStack("control", new ItemStack(controlItem));
		if (stack != null)
		{
			controlItem = stack.getType();
		}

		// subscribe events
		new ItemInHandListener(this);

		// bind commands
		new SpeedCommandExecutor(this);
		new ResetCommandExecutor(this);

		// start the deactivation loop
		taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				for (SpeedPreset preset : active)
				{
					update(preset, null);
				}
			}
		}, 0L, 20L);
	}

	@Override
	public void onDisable()
	{
		// clear events
		HandlerList.unregisterAll(this);

		// clear commands
		clearCommand("zfly");
		clearCommand("zwalk");
		clearCommand("zreset");

		// clear tasks
		getServer().getScheduler().cancelTask(taskId);
	}

	public SpeedPreset getPreset(Player player)
	{
		if (!player.isOp())
		{
			return null;
		}

		SpeedPreset preset = speedPresets.get(player);
		if (preset == null)
		{
			preset = new SpeedPreset(player);
			speedPresets.put(player, preset);
		}

		return preset;
	}

	public void update(Player player, ItemStack stack)
	{
		SpeedPreset preset = getPreset(player);
		if (preset != null)
		{
			update(preset, stack);
		}
	}

	public void scheduleUpdate(Player player)
	{
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				update(player, null);
			}
		}, 1L);
	}

	private void update(SpeedPreset preset, ItemStack stack)
	{
		if (stack == null)
		{
			stack = preset.getPlayer().getInventory().getItemInMainHand();
		}

		if (stack != null && stack.getType().equals(controlItem))
		{
			preset.activate();
			active.add(preset);
		}
		else
		{
			preset.deactivate();
			active.remove(preset);
		}
	}

	private void clearCommand(String name)
	{
		PluginCommand command = getCommand(name);
		if (command.getExecutor() == this)
		{
			command.setExecutor(null);
		}
	}
}
