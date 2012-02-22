package me.dbstudios.solusrpg;

import java.util.Hashtable;

import me.dbstudios.solusrpg.sys.RpgPlayer;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class SREntityListener implements Listener {
	private Hashtable<RpgPlayer, Integer> lastIgniteTicks = new Hashtable<RpgPlayer, Integer>();
	private SolusRpg common;
	
	public SREntityListener(SolusRpg common) {
		this.common = common;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		int damage = e.getDamage();
		
		if (e.getEntity() instanceof Player && ((Player)e.getEntity()).getGameMode() == GameMode.CREATIVE) {
			e.setCancelled(true);
			return;
		} else if (e.getEntity() instanceof Player && e.getCause() == DamageCause.FIRE) {
			RpgPlayer player = common.getRpgPlayer((Player)e.getEntity());
			Integer lastIgnite = lastIgniteTicks.get(player);
			
			if (lastIgnite != null && lastIgnite >= 15) {
				damage = player.getModifiedDamageReceived(e.getDamage());
				lastIgniteTicks.remove(player);
			} else if (lastIgnite != null && lastIgnite < 15) {
				damage = 0;
				lastIgniteTicks.put(player, lastIgnite + 1);
			} else if (lastIgnite == null) {
				lastIgniteTicks.put(player, 0);
				damage = player.getModifiedDamageReceived(e.getDamage());
			}
			
			player.damage(damage, true);
			e.setCancelled(true);
			
			return;
		}
		
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent)e;
			Entity ent = ev.getEntity();
			Entity attacker = ev.getDamager();
			
			if (ent instanceof Player) {
				RpgPlayer player = common.getRpgPlayer((Player)ent);
				
				if (attacker instanceof Player) {
					RpgPlayer attackerPlayer = common.getRpgPlayer((Player)attacker);
					
					if (!attackerPlayer.isAllowed(attackerPlayer.getPlayer().getItemInHand().getType().name(), "can-use")) {
						e.setCancelled(true);
						return;
					}
					
					damage = player.getModifiedDamageReceived(attackerPlayer.getModifiedDamageDealt(ev.getDamage()));
				} else {
					damage = player.getModifiedDamageReceived(ev.getDamage());
				}
				
				player.damage(damage, false);
				
				e.setCancelled(true);
				
				player.updateHud();
			} else if (ent instanceof LivingEntity && attacker instanceof Player) {
				RpgPlayer player = common.getRpgPlayer((Player)attacker);
				
				if (!player.isAllowed(player.getPlayer().getItemInHand().getType().name(), "can-use")) {
					e.setCancelled(true);
					return;
				}
				
				LivingEntity lEnt = (LivingEntity)ent;
				damage = player.getModifiedDamageDealt(ev.getDamage());
				
				lEnt.damage(damage);
			}			
		} else {
			Entity ent = e.getEntity();
			
			if (ent instanceof Player) {
				RpgPlayer player = common.getRpgPlayer((Player)ent);
				
				player.damage(player.getModifiedDamageReceived(e.getDamage()), false);
				e.setCancelled(true);				
				player.updateHud();
			}
		}
		
		e.setDamage(damage);
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent ev) {
		if (ev.getEntity() instanceof Player) {
			common.getRpgPlayer((Player)ev.getEntity()).regenerate(ev.getAmount(), true);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent ev) {
		if (ev.getEntity() instanceof Player) {
			common.getRpgPlayer((Player)ev.getEntity()).hideHud();
		}
	}
}