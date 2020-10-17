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
import org.bukkit.scheduler.BukkitRunnable;

public final class ZeusModPlugin extends JavaPlugin {
	public static final String
		P_USE = "zeusmod.use",
		P_BLINK = "zeusmod.blink",
		P_FLY = "zeusmod.fly",
		P_STRIKE = "zeusmod.strike",
		P_STRIKE_PLAYER = "zeusmod.strike.player";

	private final HashMap<Player, PlayerPreset>
		presets = new HashMap<Player, PlayerPreset>();

	private final HashSet<PlayerPreset>
		active = new HashSet<PlayerPreset>();

	private Material
		controlItemType = Material.GOLDEN_SWORD;

	private double
		strikeRadius = 6D,
		strikeKnockBack = 2.5D;

	private boolean
		strikeLightning = true;

	private int
		minBlinkDistance = 10,
		maxBlinkDistance = 50;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();

		// read the configuration
		long pollInterval = config.getLong("pollInterval", 10L);
		ItemStack stack = config.getItemStack("control");
		if (stack != null) {
			controlItemType = stack.getType();
		}

		strikeRadius = config.getDouble("strikeRadius", strikeRadius);
		strikeKnockBack = config.getDouble("strikeKnockBack", strikeKnockBack);
		strikeLightning = config.getBoolean("strikeLightning", strikeLightning);
		minBlinkDistance = config.getInt("minBlinkDistance", minBlinkDistance);
		maxBlinkDistance = config.getInt("maxBlinkDistance", maxBlinkDistance);

		// bind event listeners and command executors
		new ActivationListener(this);
		new InteractionListener(this);
		new ZeusModCommandExecutor(this);

		// start the deactivation polling loop
		new BukkitRunnable() {
			@Override
			public void run() {
				for (PlayerPreset preset : active) {
					verify(preset);
				}
			}
		}.runTaskTimer(this, 0L, pollInterval);
	}

	@Override
	public void onDisable() {
		for (PlayerPreset preset : active) {
			preset.deactivate();
		}
		active.clear();
		presets.clear();
	}

	public Material getControlItemType() {
		return controlItemType;
	}

	public double getStrikeRadius() {
		return strikeRadius;
	}

	public double getStrikeKnockBack() {
		return strikeKnockBack;
	}

	public boolean getStrikeLightning() {
		return strikeLightning;
	}

	public int getMinBlinkDistance() {
		return minBlinkDistance;
	}

	public int getMaxBlinkDistance() {
		return maxBlinkDistance;
	}

	public PlayerPreset getPreset(Player player) {
		if (!player.hasPermission(P_USE)) {
			return null;
		}

		PlayerPreset preset = presets.get(player);
		if (preset == null) {
			preset = new PlayerPreset(player);
			presets.put(player, preset);
		}

		return preset;
	}

	public boolean verify(PlayerPreset preset) {
		ItemStack stack = preset.getPlayer().getInventory().getItemInMainHand();
		if (stack != null && stack.getType().equals(controlItemType)) {
			preset.activate();
			active.add(preset);
			return true;
		}
		else {
			preset.deactivate();
			active.remove(preset);
			return false;
		}
	}

	public boolean verify(Player player) {
		PlayerPreset preset = getPreset(player);
		if (preset != null) {
			return verify(preset);
		}
		return false;
	}

	public void deferVerify(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				verify(player);
			}
		}.runTask(this);
	}

	public static void sendFormattedMessage(CommandSender recipient, String message) {
		recipient.sendMessage("ZeusMod: " + message);
	}
}
