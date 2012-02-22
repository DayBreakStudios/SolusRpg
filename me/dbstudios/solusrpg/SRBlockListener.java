package me.dbstudios.solusrpg;

import java.util.logging.Level;

import me.dbstudios.solusrpg.sys.RpgPlayer;
import me.dbstudios.solusrpg.util.Util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SRBlockListener implements Listener {
	private SolusRpg common;
	
	public SRBlockListener(SolusRpg common) {
		this.common = common;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent ev) {
		RpgPlayer player = common.getRpgPlayer(ev.getPlayer());
		String block = Util.getSpecialName(ev.getBlock().getType().name(), ev.getBlock().getData());
		
		if (!player.isAllowed(block, "can-break")) {
			ev.setCancelled(true);
			
			try {
				player.sendMessage(player.getMessage("break-deny", block));
			} catch (Exception e) {
				common.log(Level.WARNING, "No message defined for break-deny.");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent ev) {
		RpgPlayer player = common.getRpgPlayer(ev.getPlayer());
		String block = Util.getSpecialName(ev.getBlockPlaced().getType().name(), ev.getBlock().getData());
		
		if (!player.isAllowed(block, "can-place")) {
			ev.setCancelled(true);
			
			try {
				player.sendMessage(player.getMessage("place-deny", block));
			} catch (Exception e) {
				common.log(Level.WARNING, "No message defined for place-deny.");
			}
		}
	}
}
