package fit.seems.mc.zeusmod;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.WeatherType;

public class PlayerPreset {
	private final Player
		player;

	private boolean
		active = false,
		originalAllowFlight;

	private float
		originalFlySpeed,
		originalRunSpeed,
		flySpeedModifier,
		runSpeedModifier;

	public PlayerPreset(Player player) {
		this.player = player;

		restoreDefaults();
		originalAllowFlight = player.getAllowFlight();
		originalFlySpeed = player.getFlySpeed();
		originalRunSpeed = player.getWalkSpeed();
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isActive() {
		return active;
	}

	public void activate() {
		if (!active) {
			active = true;
			update();
		}
	}

	public void deactivate() {
		if (active) {
			active = false;
			update();
		}
	}

	public void setFlySpeedModifier(float modifier) {
		modifier = clamp(modifier);
		if (flySpeedModifier != modifier) {
			flySpeedModifier = modifier;
			if (active) {
				update();
			}
		}
	}

	public void setRunSpeedModifier(float modifier) {
		modifier = clamp(modifier);
		if (runSpeedModifier != modifier) {
			runSpeedModifier = modifier;
			if (active) {
				update();
			}
		}
	}

	public void setGameModeFlightAllowed(boolean flightAllowedByGameMode) {
		originalAllowFlight = flightAllowedByGameMode;
		update();

		// player likely switched from creative to survival
		if (active && !flightAllowedByGameMode && player.hasPermission(ZeusModPlugin.P_FLY)) {
			boolean isMidAir = player
				.getWorld()
				.getBlockAt(player.getLocation())
				.getRelative(0, -1, 0)
				.isPassable();

			if (isMidAir) {
				player.setFlying(true);
			}
		}
	}

	public void reset() {
		restoreDefaults();
		update();
		player.resetPlayerWeather();
	}

	public boolean toggleSunny() {
		if (player.getPlayerWeather() == null) {
			player.setPlayerWeather(WeatherType.CLEAR);
			return true;
		}
		else {
			player.resetPlayerWeather();
			return false;
		}
	}

	private void update() {
		if (active) {
			if (player.hasPermission(ZeusModPlugin.P_FLY)) {
				player.setAllowFlight(true);
				player.setFlySpeed(originalFlySpeed + (1F - originalFlySpeed) * flySpeedModifier);
			}
			player.setWalkSpeed(originalRunSpeed + (1F - originalRunSpeed) * runSpeedModifier);
		}
		else {
			// prevents lethal fall damage for survivalists
			if (player.isFlying() && !originalAllowFlight) {
				Location location = player.getLocation();
				double height = location.getY() - player.getWorld().getHighestBlockYAt(location);
				double duration = height > 0
					? Math.max(50D - height, 0D) * 4D + height * 3D
					: 300D;

				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, (int)duration, 1, true));
			}

			player.setAllowFlight(originalAllowFlight);
			player.setFlySpeed(originalFlySpeed);
			player.setWalkSpeed(originalRunSpeed);
		}
	}

	private void restoreDefaults() {
		originalFlySpeed = 0.2F;
		originalRunSpeed = 0.2F;
		flySpeedModifier = 0.25F;
		runSpeedModifier = 0.25F;
	}

	private static float clamp(float modifier) {
		return Math.max(Math.min(modifier, 1F), 0F);
	}
}
