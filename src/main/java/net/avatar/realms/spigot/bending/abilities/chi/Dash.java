package net.avatar.realms.spigot.bending.abilities.chi;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.avatar.realms.spigot.bending.abilities.AbilityManager;
import net.avatar.realms.spigot.bending.abilities.BendingAbilities;
import net.avatar.realms.spigot.bending.abilities.BendingAbility;
import net.avatar.realms.spigot.bending.abilities.ABendingAbility;
import net.avatar.realms.spigot.bending.abilities.BendingAbilityState;
import net.avatar.realms.spigot.bending.abilities.BendingActiveAbility;
import net.avatar.realms.spigot.bending.abilities.BendingElement;
import net.avatar.realms.spigot.bending.controller.ConfigurationParameter;

@ABendingAbility(name = "Dash", bind = BendingAbilities.Dash, element = BendingElement.ChiBlocker)
public class Dash extends BendingActiveAbility {

	@ConfigurationParameter("Length")
	private static double LENGTH = 1.95;

	@ConfigurationParameter("Height")
	private static double HEIGHT = 0.71;

	@ConfigurationParameter("Cooldown")
	private static long COOLDOWN = 4000;

	private Vector direction;

	public Dash(Player player) {
		super(player);
	}

	@Override
	public boolean sneak() {
		if (getState() == BendingAbilityState.Start) {
			setState(BendingAbilityState.Preparing);
		}

		return false;
	}

	public static boolean isDashing(Player player) {
		Map<Object, BendingAbility> instances = AbilityManager.getManager().getInstances(BendingAbilities.Dash);
		if ((instances == null) || instances.isEmpty()) {
			return false;
		}
		return instances.containsKey(player);
	}

	public static Dash getDash(Player pl) {
		Map<Object, BendingAbility> instances = AbilityManager.getManager().getInstances(BendingAbilities.Dash);
		return (Dash) instances.get(pl);
	}

	@Override
	public void progress() {
		if (getState() == BendingAbilityState.Progressing) {
			dash();
		}
	}

	public void dash() {
		Vector dir = new Vector(this.direction.getX() * LENGTH, HEIGHT, this.direction.getZ() * LENGTH);
		this.player.setVelocity(dir);
		remove();
	}

	// This should be called in OnMoveEvent to set the direction dash the same
	// as the player
	public void setDirection(Vector d) {
		if (getState() != BendingAbilityState.Preparing) {
			return;
		}
		if (Double.isNaN(d.getX()) || Double.isNaN(d.getY()) || Double.isNaN(d.getZ()) || (((d.getX() < 0.005) && (d.getX() > -0.005)) && ((d.getZ() < 0.005) && (d.getZ() > -0.005)))) {
			this.direction = this.player.getLocation().getDirection().clone().normalize();
		} else {
			this.direction = d.normalize();
		}
		setState(BendingAbilityState.Progressing);
	}

	@Override
	public void stop() {
		long cd = COOLDOWN;
		if ((ComboPoints.getComboPointAmount(this.player) < 1)) {
			cd *= 1.5;
		}
		this.bender.cooldown(BendingAbilities.Dash, cd);
		ComboPoints.addComboPoint(this.player, null);
	}

	@Override
	public Object getIdentifier() {
		return this.player;
	}

}
