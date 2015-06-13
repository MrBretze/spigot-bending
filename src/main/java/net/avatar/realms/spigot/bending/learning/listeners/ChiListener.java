package net.avatar.realms.spigot.bending.learning.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.avatar.realms.spigot.bending.abilities.Abilities;
import net.avatar.realms.spigot.bending.abilities.BendingPlayer;
import net.avatar.realms.spigot.bending.abilities.BendingType;
import net.avatar.realms.spigot.bending.controller.ConfigManager;
import net.avatar.realms.spigot.bending.event.AbilityCooldownEvent;
import net.avatar.realms.spigot.bending.learning.BendingLearning;
import net.avatar.realms.spigot.bending.utils.EntityTools;
import net.avatar.realms.spigot.bending.utils.PluginTools;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class ChiListener implements Listener {
	private BendingLearning plugin;
	
	private Map<UUID, Double> sprintDistanceTraveledDash = new HashMap<UUID, Double>();
	private Map<UUID, Location> sprintLastLocationDash = new HashMap<UUID, Location>();
	private static final int distanceNeededDash = 1000;

	public ChiListener(BendingLearning plugin) {
		this.plugin = plugin;
		
	}
	
	@EventHandler
	public void unlockDash(PlayerToggleSprintEvent event) {
		Player pl = (Player) event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(pl);
		if(bPlayer != null) {
			if(bPlayer.isBender(BendingType.ChiBlocker)) {
				if(!pl.isSprinting()) {
					sprintLastLocationDash.put(pl.getUniqueId(), pl.getLocation().clone());
				} else {
					if(sprintLastLocationDash.containsKey(pl.getUniqueId())) {
						Location last = sprintLastLocationDash.get(pl.getUniqueId());
						Location current = pl.getLocation();
						if(last.getWorld().getUID().equals(current.getWorld().getUID())) {
							double distance = last.distance(current);
							if(sprintDistanceTraveledDash.containsKey(pl.getUniqueId())) {
								distance = sprintDistanceTraveledDash.get(pl.getUniqueId()) + distance;
							}
							if(distance >= distanceNeededDash) {
								if(plugin.addPermission(pl, Abilities.Dash)) {
									ChatColor color = PluginTools.getColor(ConfigManager.getColor("ChiBlocker"));
									String message = "Wooo !";
									pl.sendMessage(color+message);
									message = "Congratulations, you have unlocked "+Abilities.Dash.name();
									pl.sendMessage(color+message);
								}
								sprintDistanceTraveledDash.remove(pl.getUniqueId());
							} else {
								sprintDistanceTraveledDash.put(pl.getUniqueId(), distance);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler 
	public void unlockPoisonnedDart(PotionSplashEvent event) {
		
	}
	
	@EventHandler
	public void unlockPoisonnedDart(ProjectileLaunchEvent event) {
		
	}
	
	@EventHandler 
	public void unlockSmokeBomb(AbilityCooldownEvent event) {
		BendingPlayer bPlayer = event.getBender();
		if(bPlayer != null) {
			if(bPlayer.isBender(BendingType.ChiBlocker) && event.getAbility().equals(Abilities.SmokeBomb)) {
				List<LivingEntity> entities = EntityTools.getLivingEntitiesAroundPoint(bPlayer.getPlayer().getLocation(), 10);
				for(LivingEntity entity : entities) {
					if(entity instanceof Player) {
						Player p = (Player)entity;
						BendingPlayer trainee = BendingPlayer.getBendingPlayer(p);
						if(trainee.isBender(BendingType.ChiBlocker)) {
							if(p.hasLineOfSight(bPlayer.getPlayer())) {
								if(plugin.addPermission(p, Abilities.SmokeBomb)) {
									ChatColor color = PluginTools.getColor(ConfigManager.getColor("ChiBlocker"));
									String message = "By analizing and another smoke bomb from "+bPlayer.getPlayer().getName()+" you think you can reproduce it";
									p.sendMessage(color+message);
									message = "Congratulations, you have unlocked "+Abilities.SmokeBomb.name();
									p.sendMessage(color+message);
								}
							}
						}
					}
				}
			}
		}
	}
}
