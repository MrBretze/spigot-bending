package net.avatar.realms.spigot.bending.abilities.chi;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.avatar.realms.spigot.bending.abilities.BendingAbilities;
import net.avatar.realms.spigot.bending.abilities.ABendingAbility;
import net.avatar.realms.spigot.bending.abilities.BendingActiveAbility;
import net.avatar.realms.spigot.bending.abilities.BendingElement;
import net.avatar.realms.spigot.bending.controller.ConfigurationParameter;
import net.avatar.realms.spigot.bending.utils.EntityTools;

/**
 *
 * This ability hit the first entity in front of you powerfully driving to a
 * knockback You must be sneaking when clicking to activate this technique.
 *
 */
@ABendingAbility(name = "Direct Hit", bind = BendingAbilities.DirectHit, element = BendingElement.ChiBlocker)
public class DirectHit extends BendingActiveAbility {

	@ConfigurationParameter("Damage")
	public static long DAMAGE = 5;

	@ConfigurationParameter("Knockback")
	public static long KNOCKBACK = 2;

	@ConfigurationParameter("Range")
	public static long RANGE = 4;

	@ConfigurationParameter("Cooldown")
	public static long COOLDOWN = 1500;

	public DirectHit(Player player) {
		super(player);
	}

	@Override
	public boolean swing() {
		LivingEntity target = EntityTools.getTargettedEntity(this.player, RANGE);
		if(target == null) {
			remove();
			return false;
		}
		if(this.player.isSneaking()) {
			EntityTools.damageEntity(this.player, target, DAMAGE);
			target.setVelocity(this.player.getEyeLocation().getDirection().clone().normalize()
					.multiply((0.5 + this.player.getVelocity().length()) * KNOCKBACK));

			this.bender.cooldown(this, COOLDOWN * 2);
		}
		else {
			if (ComboPoints.getComboPointAmount(this.player) < 3) {
				ComboPoints.addComboPoint(this.player, target);
				this.bender.cooldown(this, COOLDOWN);
			}
		}
		return false;
	}

	@Override
	public void progress() {
		
	}

	@Override
	protected long getMaxMillis() {
		return 1;
	}

	@Override
	public Object getIdentifier() {
		return this.player;
	}

	@Override
	public boolean canBeInitialized() {
		if (!super.canBeInitialized()) {
			return false;
		}

		if (EntityTools.isWeapon(this.player.getItemInHand().getType())) {
			return false;
		}

		return true;
	}

	@Override
	public void stop() {
		
	}

}
