package net.avatar.realms.spigot.bending.learning.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.avatar.realms.spigot.bending.abilities.Abilities;
import net.avatar.realms.spigot.bending.abilities.BendingPlayer;
import net.avatar.realms.spigot.bending.abilities.BendingType;
import net.avatar.realms.spigot.bending.controller.Settings;
import net.avatar.realms.spigot.bending.event.AbilityCooldownEvent;
import net.avatar.realms.spigot.bending.learning.BendingLearning;
import net.avatar.realms.spigot.bending.utils.EntityTools;
import net.avatar.realms.spigot.bending.utils.PluginTools;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class AirListener implements Listener {
	private BendingLearning plugin;
	
	private Map<UUID, Double> airScooterDistanceTraveled = new HashMap<UUID, Double>();
	private Map<UUID, Location> airScooterLastLocation = new HashMap<UUID, Location>();
	
	private Map<UUID, Integer> airSuctionJump = new HashMap<UUID, Integer>();
	private Map<UUID, Integer> airBurst = new HashMap<UUID, Integer>();
	
	private static ChatColor color =PluginTools.getColor(Settings.getColorString("Air"));

	public AirListener(BendingLearning plugin) {
		this.plugin = plugin;
		
	}
	
	@EventHandler
	public void unlockAirBubble(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player && event.getCause().equals(DamageCause.DROWNING)) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((Player) event.getEntity());
			if(bPlayer != null) {
				if(bPlayer.isBender(BendingType.Air) && !bPlayer.isBender(BendingType.ChiBlocker)) {
					if(plugin.addPermission(bPlayer.getPlayer(), Abilities.AirBubble)) {
						String message = "After running out of air, you realise that you could use your AirBending to grap air before sinking";
						bPlayer.getPlayer().sendMessage(color+message);
						message = "Congratulations, you have unlocked "+Abilities.AirBubble.name();
						bPlayer.getPlayer().sendMessage(color+message);
					} 
				}
			}
		}
	}
	
	@EventHandler
	public void unlockAirScooter(PlayerToggleSprintEvent event) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((Player) event.getPlayer());
		if(bPlayer != null) {
			if(bPlayer.isBender(BendingType.Air) && !bPlayer.isBender(BendingType.ChiBlocker)) {
				Player pl = bPlayer.getPlayer();
				if(!event.getPlayer().isSprinting()) {
					airScooterLastLocation.put(pl.getUniqueId(), pl.getLocation().clone());
				} else {
					if(airScooterLastLocation.containsKey(pl.getUniqueId())) {
						Location last = airScooterLastLocation.get(pl.getUniqueId());
						Location current = pl.getLocation();
						if(last.getWorld().getUID().equals(current.getWorld().getUID())) {
							double distance = last.distance(current);
							if(airScooterDistanceTraveled.containsKey(pl.getUniqueId())) {
								distance = airScooterDistanceTraveled.get(pl.getUniqueId()) + distance;
							}
							if(distance >= 1000) {
								if(plugin.addPermission(pl, Abilities.AirScooter)) {
									String message = "After spending that much energy running arround, you think you will be able to speed up by using your airbending";
									pl.sendMessage(color+message);
									message = "Congratulations, you have unlocked "+Abilities.AirScooter.name();
									pl.sendMessage(color+message);
								} 
								airScooterDistanceTraveled.remove(pl.getUniqueId());
							} else {
								airScooterDistanceTraveled.put(pl.getUniqueId(), distance);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void unlockAirSuction(PlayerVelocityEvent event) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((Player) event.getPlayer());
		if(bPlayer != null) {
			if(bPlayer.isBender(BendingType.Air) && !bPlayer.isBender(BendingType.ChiBlocker)) {
				Player player = bPlayer.getPlayer();
				Vector down = new Vector(0, 1, 0);
				float angleToDirectFall = event.getVelocity().angle(down);
				if(-Math.PI/2 < angleToDirectFall && angleToDirectFall < Math.PI/2) {
					//Player has probably jumped
					int jumped = 0;
					if(airSuctionJump.containsKey(player.getUniqueId())) {
						jumped = airSuctionJump.get(player.getUniqueId());
					}
					jumped = jumped + 1;
					
					if(jumped >= 50) {
						if(plugin.addPermission(player, Abilities.AirSuction)) {
							String message = "Without realising it, you band air more and more efficiently, and you are now able to catch people from far away.";
							player.sendMessage(color+message);
							message = "Congratulations, you have unlocked "+Abilities.AirSuction.name();
							player.sendMessage(color+message);
						} 
						airSuctionJump.remove(player.getUniqueId());
					} else {
						airSuctionJump.put(player.getUniqueId(), jumped);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void unlockAirBurst(AbilityCooldownEvent event) {
		BendingPlayer bPlayer = event.getBender();
		if(bPlayer != null) {
			if(bPlayer.isBender(BendingType.Air) && !bPlayer.isBender(BendingType.ChiBlocker) && event.getAbility().equals(Abilities.AirBlast)) {
				Player player = bPlayer.getPlayer();
				int blasted = 0;
				if(airBurst.containsKey(player.getUniqueId())) {
					blasted = airBurst.get(player.getUniqueId());
				}
				blasted = blasted + 1;
				if(blasted >= 100) {
					if(plugin.addPermission(player, Abilities.AirBurst)) {
						String message = "Your skill at airblast is improving, and you feel now able to burst it 3 in one";
						player.sendMessage(color+message);
						message = "Congratulations, you have unlocked "+Abilities.AirBurst.name();
						player.sendMessage(color+message);
					} 
					airBurst.remove(player.getUniqueId());
				} else {
					airBurst.put(player.getUniqueId(), blasted);
				}
			}
		}
	}
	
	@EventHandler
	public void unlockAirShield(AbilityCooldownEvent event) {
		BendingPlayer bPlayer = event.getBender();
		if(bPlayer != null) {
			if(bPlayer.isBender(BendingType.Air) && event.getAbility().equals(Abilities.AirShield)) {
				List<LivingEntity> entities = EntityTools.getLivingEntitiesAroundPoint(bPlayer.getPlayer().getLocation(), 10);
				
				for(Entity entity : entities) {
					if(entity instanceof Player) {
						Player p = (Player)entity;
						BendingPlayer trainee = BendingPlayer.getBendingPlayer(p);
						if(trainee.isBender(BendingType.Air) && !trainee.isBender(BendingType.ChiBlocker)) {
							if(p.hasLineOfSight(bPlayer.getPlayer())) {
								if(plugin.addPermission(p, Abilities.AirShield)) {
									String message = "After seeing "+bPlayer.getPlayer().getName()+" doing an air shield, you are able to copy it for yourself.";
									p.sendMessage(color+message);
									message = "Congratulations, you have unlocked "+Abilities.AirShield.name();
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