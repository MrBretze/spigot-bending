package net.avatar.realms.spigot.bending.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.avatar.realms.spigot.bending.abilities.base.Ability;

// I continue to use an extern class to handle flying because there can be many reasons to be flying
// : WaterSpout and Tornado for example.
// If your were WaterSpouting when a tornado comes against you, there may be a conflict giving the
// fly to a player
public class FlyingPlayer {

	private static Map<UUID, FlyingPlayer> flyingPlayers = new HashMap<UUID, FlyingPlayer>();
	
	private Player player;
	private boolean couldFly;
	private boolean wasFlying;
	private Map<Ability, Long> causes;

	private FlyingPlayer (Player player) {
		
		this.player = player;
		this.couldFly = (player.getAllowFlight());
		this.wasFlying = player.isFlying();
		this.causes = new HashMap<Ability, Long>();
	}

	public void fly () {
		
		this.player.setAllowFlight(true);
		this.player.setFlying(true);
	}

	public void resetState () {
		
		this.player.setAllowFlight(this.couldFly);
		this.player.setFlying(this.wasFlying);
	}

	private boolean addCause (Ability cause, Long maxDuration) {
		
		if (this.causes == null) {
			return false;
		}
		
		this.causes.put(cause, maxDuration);
		return true;
	}

	public boolean hasCauses () {
		
		if (this.causes == null) {
			return false;
		}

		return !this.causes.isEmpty();
	}
	
	public boolean hasCause (Ability cause) {
		
		if (this.causes == null) {
			return false;
		}

		if (this.causes.isEmpty()) {
			return false;
		}
		
		return this.causes.containsKey(cause);
	}
	
	private void removeCause (Ability cause) {
		
		if (this.causes == null) {
			return;
		}
		
		if (this.causes.containsKey(cause)) {
			this.causes.remove(cause);
		}
	}

	public static FlyingPlayer addFlyingPlayer (Player player, Ability cause, Long maxDuration) {
		
		if ((player == null) || (cause == null)) {
			return null;
		}
		
		FlyingPlayer flying = null;
		if (flyingPlayers.containsKey(player.getUniqueId())) {
			flying = flyingPlayers.get(player.getUniqueId());
			if (flying == null) {
				flying = new FlyingPlayer(player);
			}
		}
		else {
			flying = new FlyingPlayer(player);
		}
		
		flying.addCause(cause, System.currentTimeMillis() + maxDuration);
		if (flying.hasCauses()) {
			flyingPlayers.put(player.getUniqueId(), flying);
			flying.fly();
		}
		return flying;
	}

	public static void removeFlyingPlayer (Player player, Ability cause) {
		
		if (!flyingPlayers.containsKey(player.getUniqueId())) {
			return;
		}
		
		FlyingPlayer flying = flyingPlayers.get(player.getUniqueId());
		if (flying == null) {
			flyingPlayers.remove(player.getUniqueId());
			return;
		}
		
		if (flying.hasCause(cause)) {
			flying.removeCause(cause);
			if (!flying.hasCauses()) {
				flying.resetState();
				flyingPlayers.remove(player.getUniqueId());
			}
		}
	}
	
	private boolean handle () {
		
		long now = System.currentTimeMillis();
		List<Ability> toRemove = new LinkedList<Ability>();
		for (Ability ab : this.causes.keySet()) {
			if (now > this.causes.get(ab)) {
				toRemove.add(ab);
			}
		}
		
		for (Ability ab : toRemove) {
			this.causes.remove(ab);
		}

		return hasCauses();
	}

	public static void handleAll () {
		
		List<UUID> toRemove = new LinkedList<UUID>();
		for (UUID id : flyingPlayers.keySet()) {
			if (!flyingPlayers.get(id).handle()) {
				toRemove.add(id);
			}
		}

		for (UUID id : toRemove) {
			flyingPlayers.get(id).resetState();
			flyingPlayers.remove(id);
		}
	}
}