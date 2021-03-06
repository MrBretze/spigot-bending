package net.avatar.realms.spigot.bending.abilities.chi;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.avatar.realms.spigot.bending.abilities.BendingAbilities;
import net.avatar.realms.spigot.bending.abilities.BendingActiveAbility;
import net.avatar.realms.spigot.bending.abilities.ABendingAbility;
import net.avatar.realms.spigot.bending.abilities.BendingElement;
import net.avatar.realms.spigot.bending.abilities.BendingPath;
import net.avatar.realms.spigot.bending.controller.ConfigurationParameter;
import net.avatar.realms.spigot.bending.utils.BlockTools;
import net.avatar.realms.spigot.bending.utils.Tools;

@ABendingAbility(name = "High Jump", bind = BendingAbilities.HighJump, element = BendingElement.ChiBlocker)
public class HighJump extends BendingActiveAbility {

	@ConfigurationParameter("Height")
	private static final int JUMP_HEIGHT = 7;

	@ConfigurationParameter("Cooldown")
	private static final long COOLDOWN = 3500;

	public HighJump(Player player) {
		super(player);
	}

	@Override
	public boolean swing() {
		if (makeJump()) {
			this.bender.cooldown(BendingAbilities.HighJump, COOLDOWN);
		}

		ComboPoints.addComboPoint(this.player, null);
		return true;
	}

	private boolean makeJump() {
		if (!BlockTools.isSolid(this.player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
			return false;
		}
		int height = JUMP_HEIGHT;

		if(this.bender.hasPath(BendingPath.Seeker)) {
			height *= 0.8;
		}

		if(this.bender.hasPath(BendingPath.Restless)) {
			height *= 1.2;
		}

		if (ComboPoints.getComboPointAmount(this.player) >= 2) {
			height++;
		}
		Vector vec = Tools.getVectorForPoints(this.player.getLocation(), this.player.getLocation().add(this.player.getVelocity()).add(0, height, 0));
		this.player.setVelocity(vec);
		return true;
	}

	@Override
	public Object getIdentifier() {
		return this.player;
	}

	@Override
	public void progress() {
		
	}

	@Override
	public void stop() {
		
	}
}
