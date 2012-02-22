package me.dbstudios.solusrpg;

import java.util.logging.Level;

import me.dbstudios.solusrpg.sys.RpgPlayer;
import me.dbstudios.solusrpg.util.MessageNotFoundException;
import me.dbstudios.solusrpg.util.Util;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventorySlotType;

public class SRInventoryListener implements Listener {
	private SolusRpg common;
	
	public SRInventoryListener(SolusRpg common) {
		this.common = common;
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent ev) {
		RpgPlayer player = common.getRpgPlayer(ev.getPlayer());
		
		if (ev.getInventory() instanceof PlayerInventory) {
			PlayerInventory inv = (PlayerInventory)ev.getInventory();
			ItemStack[] armorContents = inv.getArmorContents();
			ItemStack[] newContents = new ItemStack[4];
			
			for (int i = 0; i <= 3; i++) {
				if (armorContents[i].getAmount() != 0) {
					String name = armorContents[i].getType().name();
					
					if (!player.isAllowed(name, "can-wear")) {
						try {
							player.sendMessage(player.getMessage("wear-deny",name));
						} catch (MessageNotFoundException e) {
							common.log(Level.WARNING, "No message defined for wear-deny.");
						}
						
						ItemStack tmp = armorContents[i];
						
						int freeSlot = inv.firstEmpty();
						
						if (freeSlot != -1) {
							inv.setItem(freeSlot, tmp);
						} else {
							player.getPlayer().getWorld().dropItemNaturally(player.getPlayer().getLocation(), tmp);
							
							try {
								player.sendMessage(player.getMessage("wear-deny-item-drop", tmp.getType().name()));
							} catch (MessageNotFoundException e) {
								common.log(Level.WARNING, "No message defined for wear-deny-item-drop.");
							}
						}
					} else {
						newContents[i] = armorContents[i];
					}
				}
			}
			
			inv.setArmorContents(newContents);
			
			player.updateSpeed();
		}
	}
	
	@EventHandler
	public void onInventoryCraft(InventoryCraftEvent ev) {
		RpgPlayer player = common.getRpgPlayer(ev.getPlayer());
		
		if (!player.isAllowed(Util.getSpecialName(ev.getResult().getType().name(), ev.getResult().getData().getData()), "can-craft")) {
			ev.setCancelled(true);
			
			try {
				player.sendMessage(player.getMessage("craft-deny", Util.getSpecialName(ev.getResult().getType().name(), ev.getResult().getData().getData())));
			} catch (MessageNotFoundException e) {
				common.log(Level.INFO, "No message defined for craft-deny.");
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent ev) {		
		if (ev.getSlotType() == InventorySlotType.SMELTING || ev.getSlotType() == InventorySlotType.FUEL || ev.getSlotType() == InventorySlotType.RESULT)
			return;
		
		if (ev.getItem() == null || ev.getItem().getType() == Material.COAL || ev.getItem().getType() == Material.WOOD || ev.getItem().getType() == Material.LOG)
			return;
		
		RpgPlayer player = common.getRpgPlayer(ev.getPlayer());
		BlockState state = ev.getPlayer().getTargetBlock(null, 4).getState();
		
		if (state instanceof Furnace && ev.getItem() != null) {
			String item = Util.getSpecialName(ev.getItem().getType().name(), ev.getItem().getData().getData());
			
			if (!player.isAllowed(item, "can-smelt")) {
				ev.setCancelled(true);
				
				try {
					player.sendMessage(player.getMessage("smelt-deny", item));
				} catch (MessageNotFoundException e) {
					common.log(Level.INFO, "No message defined for smelt-deny.");
				}
			}
		}
	}
}