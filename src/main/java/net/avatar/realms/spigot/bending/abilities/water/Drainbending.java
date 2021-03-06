package net.avatar.realms.spigot.bending.abilities.water;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.avatar.realms.spigot.bending.abilities.BendingAbilities;
import net.avatar.realms.spigot.bending.controller.ConfigurationParameter;
import net.avatar.realms.spigot.bending.utils.EntityTools;
import net.avatar.realms.spigot.bending.utils.TempBlock;

public class Drainbending {

	@ConfigurationParameter("Cooldown")
	public static long COOLDOWN = 1500;

	public static boolean canDrainBend(Player player) {
		return EntityTools.canBend(player, BendingAbilities.Drainbending);
	}

	public static boolean canBeSource(Block block) {
		if (TempBlock.isTempBlock(block)) {
			return false;
		}
		return block.isEmpty();
	}
}
