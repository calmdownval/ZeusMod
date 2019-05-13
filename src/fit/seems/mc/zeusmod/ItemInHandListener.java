package fit.seems.mc.zeusmod;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ItemInHandListener implements Listener
{
	ZeusModPlugin plugin;

	public ItemInHandListener(ZeusModPlugin plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	private void handlePlayerEvent(PlayerEvent event)
	{
		plugin.scheduleUpdate(event.getPlayer());
	}

	private void handleEntity(Entity entity)
	{
		if (entity instanceof Player)
		{
			plugin.scheduleUpdate((Player)entity);
		}
	}


	// ASSORTED EVENTS:

	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event)
	{
		handleEntity(event.getEntity());
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		handleEntity(event.getPlayer());
	}


	// PLAYER EVENTS:

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent event)
	{
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event)
	{
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemDamage(PlayerItemDamageEvent event)
	{
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event)
	{
		handlePlayerEvent(event);
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event)
	{
		Player player = event.getPlayer();
		plugin.update(player, player.getInventory().getItem(event.getNewSlot()));
	}
}
