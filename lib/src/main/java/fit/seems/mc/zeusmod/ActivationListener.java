package fit.seems.mc.zeusmod;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ActivationListener implements Listener {
	private final ZeusModPlugin plugin;

	public ActivationListener(ZeusModPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		PlayerPreset preset = plugin.getPreset(event.getPlayer());
		if (preset == null) {
			return;
		}

		// game mode rules are only applied after this event is handled
		// defer the update to the next tick
		new BukkitRunnable() {
			@Override
			public void run() {
				boolean flightAllowedByGameMode = false;
				switch (event.getNewGameMode()) {
					case CREATIVE:
					case SPECTATOR:
						flightAllowedByGameMode = true;
						break;
				}

				preset.setGameModeFlightAllowed(flightAllowedByGameMode);
			}
		}.runTask(plugin);
	}

	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		handleEntity(event.getEntity());
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		handleEntity(event.getPlayer());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent event) {
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemDamage(PlayerItemDamageEvent event) {
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
		handlePlayerEvent(event);
	}

	private void handlePlayerEvent(PlayerEvent event) {
		plugin.deferVerify(event.getPlayer());
	}

	private void handleEntity(Entity entity) {
		if (entity instanceof Player) {
			plugin.deferVerify((Player)entity);
		}
	}
}
