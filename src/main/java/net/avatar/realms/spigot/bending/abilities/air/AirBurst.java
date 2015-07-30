package net.avatar.realms.spigot.bending.abilities.air;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.avatar.realms.spigot.bending.abilities.Abilities;
import net.avatar.realms.spigot.bending.abilities.BendingAbility;
import net.avatar.realms.spigot.bending.abilities.BendingPlayer;
import net.avatar.realms.spigot.bending.abilities.BendingType;
import net.avatar.realms.spigot.bending.abilities.IAbility;
import net.avatar.realms.spigot.bending.abilities.energy.AvatarState;
import net.avatar.realms.spigot.bending.controller.ConfigurationParameter;
import net.avatar.realms.spigot.bending.utils.EntityTools;
import net.avatar.realms.spigot.bending.utils.Tools;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * AirBurst is just an utility abilities, it does no damage or whatever, only providing a way to check if a player has charged
 * Classes AirSphereBurst, AirConeBurst, AirFallBurst consumes charge and remove it
 * 
 * @author Koudja
 *
 */

@BendingAbility(name="Air Burst", element=BendingType.Air)
public class AirBurst implements IAbility {
	private static Map<Player, AirBurst> instances = new HashMap<Player, AirBurst>();
	
	@ConfigurationParameter("Charge-Time")
	public static long DEFAULT_CHARGETIME = 1750;
	
	@ConfigurationParameter("Cooldown")
	public static long COOLDOWN = 2000;
	
	@ConfigurationParameter("Push-Factor")
	public static double PUSHFACTOR = 1.5;
	
	@ConfigurationParameter("Del-Theta")
	public static double DELTHETA = 10;
	
	@ConfigurationParameter("Del-Phi")
	public static double DELPHI = 10;

	private Player player;
	private long starttime;
	private long chargetime = DEFAULT_CHARGETIME;
	private boolean charged = false;
	
	private IAbility parent;

	public AirBurst(Player player, IAbility parent) {
		this.parent = parent;
		if (BendingPlayer.getBendingPlayer(player).isOnCooldown(
				Abilities.AirBurst))
			return;

		if (instances.containsKey(player))
			return;
		starttime = System.currentTimeMillis();
		if (AvatarState.isAvatarState(player)) {
			chargetime = 0;
		}
			
		this.player = player;
		instances.put(player, this);
	}

	private boolean progress() {
		if (!EntityTools.canBend(player, Abilities.AirBurst)
				|| EntityTools.getBendingAbility(player) != Abilities.AirBurst) {
			return false;
		}
		
		if (!player.isSneaking()) {
			return false;
		}
		
		if (System.currentTimeMillis() > starttime + chargetime && !charged) {
			charged = true;
		}

		if (charged) {
			Location location = player.getEyeLocation();
			// location = location.add(location.getDirection().normalize());
			location.getWorld().playEffect(
					location,
					Effect.SMOKE,
					Tools.getIntCardinalDirection(player.getEyeLocation()
							.getDirection()), 3);
		}
		return true;
	}
	
	public void remove() {
		instances.remove(player);
	}

	public static void progressAll() {
		List<AirBurst> toRemove = new LinkedList<AirBurst>();
		for (AirBurst burst : instances.values()) {
			boolean keep = burst.progress();
			if(!keep) {
				toRemove.add(burst);
			}
		}
		
		for(AirBurst burst : toRemove) {
			burst.remove();
		}
	}

	public static void removeAll() {
		instances.clear();
	}
	
	public static boolean isAirBursting(Player player) {
		return instances.containsKey(player);
	}
	
	public boolean isCharged() {
		return charged;
	}

	@Override
	public IAbility getParent() {
		return parent;
	}

	public static AirBurst getAirBurst(Player player) {
		return instances.get(player);
	}
}
