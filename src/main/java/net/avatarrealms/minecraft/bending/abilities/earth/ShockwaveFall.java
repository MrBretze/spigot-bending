package net.avatarrealms.minecraft.bending.abilities.earth;

import net.avatarrealms.minecraft.bending.model.Abilities;
import net.avatarrealms.minecraft.bending.model.IAbility;
import net.avatarrealms.minecraft.bending.utils.BlockTools;
import net.avatarrealms.minecraft.bending.utils.EntityTools;
import org.bukkit.entity.Player;

public class ShockwaveFall implements IAbility {
	private static final double threshold = 10;

	private IAbility parent;

	public ShockwaveFall(Player player, IAbility parent) {
		this.parent = parent;
		
		if (!EntityTools.canBend(player, Abilities.Shockwave)
				|| EntityTools.getBendingAbility(player) != Abilities.Shockwave
				|| player.getFallDistance() < threshold
				|| !BlockTools.isEarthbendable(player,
						player.getLocation().add(0, -1, 0).getBlock())) {
			return;
		}
		
		new ShockwaveArea(player, this);
	}

	@Override
	public int getBaseExperience() {
		return 0;
	}

	@Override
	public IAbility getParent() {
		return parent;
	}

}