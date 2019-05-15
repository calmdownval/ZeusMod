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

	private HashMap<Player, PlayerPreset> presets = new HashMap<Player, PlayerPreset>();

	private HashSet<PlayerPreset> active = new HashSet<PlayerPreset>();

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
		new HarmListener(this);

		// bind commands
		new SpeedCommandExecutor(this);
		new ResetCommandExecutor(this);

		// start the deactivation loop
		taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				for (PlayerPreset preset : active)
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

	public PlayerPreset getPreset(Player player)
	{
		if (!player.isOp())
		{
			return null;
		}

		PlayerPreset preset = presets.get(player);
		if (preset == null)
		{
			preset = new PlayerPreset(player);
			presets.put(player, preset);
		}

		return preset;
	}

	public void update(Player player, ItemStack stack)
	{
		PlayerPreset preset = getPreset(player);
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

	private void update(PlayerPreset preset, ItemStack stack)
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
