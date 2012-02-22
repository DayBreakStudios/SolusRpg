package me.dbstudios.solusrpg;

import java.util.logging.Level;

import me.dbstudios.solusrpg.sys.RpgPlayer;
import me.dbstudios.solusrpg.util.MessageNotFoundException;
import me.dbstudios.solusrpg.util.Util;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SRPlayerListener implements Listener {
	private SolusRpg common;
	
	public SRPlayerListener(SolusRpg common) {
		this.common = common;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		common.addRpgPlayer(ev.getPlayer());
		
		final RpgPlayer player = common.getRpgPlayer(ev.getPlayer());
		
		common.getServer().getScheduler().scheduleAsyncDelayedTask(common, new Runnable() {
			public void run() {
				player.updateSpeed();
				player.updateHud();
			}
		}, 40L);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent ev) {
		common.removeRpgPlayer(ev.getPlayer());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent ev) {
		if (ev.getAction() == Action.PHYSICAL)
			return;

		Material type = Material.AIR;

		if (ev.getItem() != null)
			type = ev.getItem().getType();

		if (ev.getAction().name().startsWith("RIGHT_CLICK") && type != Material.BOW && type != Material.POTION)
			return;
		
		RpgPlayer player = common.getRpgPlayer(ev.getPlayer());
		String item;
		
		if (type != Material.AIR) {
			item = Util.getSpecialName(ev.getItem().getType().name(), ev.getItem().getData().getData());
		} else {
			item = "FISTS";
		}
		
		if (!player.isAllowed(item, "can-use")) {
			ev.setCancelled(true);
			
			try {
				player.sendMessage(player.getMessage("use-deny", item));
			} catch (MessageNotFoundException e) {
				common.log(Level.WARNING, "No message defined for use-deny.");
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent ev) {
		RpgPlayer player = common.getRpgPlayer(ev.getPlayer());		
		
		player.getPlayer().teleport(common.getRespawnPoint());
		player.setHealth(player.getMaxHealth());
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent ev) {
		common.getRpgPlayer(ev.getPlayer()).updateSpeed();
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent ev) {
		common.getRpgPlayer(ev.getPlayer()).updateSpeed();
	}
}