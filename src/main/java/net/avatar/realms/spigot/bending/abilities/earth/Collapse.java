package net.avatar.realms.spigot.bending.abilities.earth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import net.avatar.realms.spigot.bending.abilities.BendingAbilities;
import net.avatar.realms.spigot.bending.abilities.ABendingAbility;
import net.avatar.realms.spigot.bending.abilities.BendingAbilityState;
import net.avatar.realms.spigot.bending.abilities.BendingActiveAbility;
import net.avatar.realms.spigot.bending.abilities.BendingElement;
import net.avatar.realms.spigot.bending.controller.ConfigurationParameter;
import net.avatar.realms.spigot.bending.utils.BlockTools;
import net.avatar.realms.spigot.bending.utils.EntityTools;

@ABendingAbility(name = "Collapse", bind = BendingAbilities.Collapse, element = BendingElement.Earth)
public class Collapse extends BendingActiveAbility {
	@ConfigurationParameter("Range")
	public static int RANGE = 20;

	@ConfigurationParameter("Radius")
	private static double RADIUS = 7;

	@ConfigurationParameter("Depth")
	public static int DEPTH = 6;

	@ConfigurationParameter("Cooldown")
	public static long COOLDOWN = 250;

	@ConfigurationParameter("Speed")
	public static double SPEED = 8;

	private Map<Block, Block> blocks = new HashMap<Block, Block>();
	private Map<Block, Integer> baseblocks = new HashMap<Block, Integer>();
	private double radius = RADIUS;

	private List<CompactColumn> columns = new LinkedList<CompactColumn>();

	public Collapse(Player player) {
		super(player);

	}

	@Override
	public boolean swing() {
		if (getState() != BendingAbilityState.Start) {
			return false;
		}
		this.bender.cooldown(BendingAbilities.Collapse, COOLDOWN);
		this.columns.add(new CompactColumn(this.player));
		setState(BendingAbilityState.Progressing);
		
		return false;
	}

	@Override
	public boolean sneak() {
		if (getState() != BendingAbilityState.Start) {
			return false;
		}

		Block sblock = BlockTools.getEarthSourceBlock(this.player, BendingAbilities.Collapse, RANGE);
		Location location;
		if (sblock == null) {
			location = EntityTools.getTargetBlock(this.player, RANGE, BlockTools.getTransparentEarthbending()).getLocation();
		} else {
			location = sblock.getLocation();
		}
		for (Block block : BlockTools.getBlocksAroundPoint(location, this.radius)) {
			if (BlockTools.isEarthbendable(this.player, BendingAbilities.Collapse, block) && !this.blocks.containsKey(block) && block.getY() >= location.getBlockY()) {
				getAffectedBlocks(block);
			}
		}

		if (!this.baseblocks.isEmpty()) {
			this.bender.cooldown(BendingAbilities.Collapse, COOLDOWN);
		}

		for (Block block : this.baseblocks.keySet()) {
			this.columns.add(new CompactColumn(this.player, block.getLocation()));
		}
		setState(BendingAbilityState.Progressing);
		return false;
	}
	
	@Override
	public boolean canTick() {
		if(!super.canTick()) {
			return false;
		}
		if (getState() == BendingAbilityState.Progressing && this.columns.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public void progress() {
		LinkedList<CompactColumn> test = new LinkedList<CompactColumn>(this.columns);
		for (CompactColumn column : test) {
			if (!column.progress()) {
				this.columns.remove(column);
			}
		}
	}

	@Override
	public void stop() {
		for (CompactColumn column : this.columns) {
			column.remove();
		}
	}

	private void getAffectedBlocks(Block block) {
		Block baseblock = block;
		int tall = 0;
		List<Block> bendableblocks = new ArrayList<Block>();
		bendableblocks.add(block);
		for (int i = 1; i <= DEPTH; i++) {
			Block blocki = block.getRelative(BlockFace.DOWN, i);
			if (BlockTools.isEarthbendable(this.player, BendingAbilities.Collapse, blocki)) {
				baseblock = blocki;
				bendableblocks.add(blocki);
				tall++;
			} else {
				break;
			}
		}
		this.baseblocks.put(baseblock, tall);
		for (Block blocki : bendableblocks) {
			this.blocks.put(blocki, baseblock);
		}

	}

	@Override
	public Object getIdentifier() {
		return this.player;
	}
}
