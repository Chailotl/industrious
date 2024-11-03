package com.chailotl.industrious.item;

import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BoxingGloveItem extends Item
{
	public BoxingGloveItem(Settings settings)
	{
		super(settings);
	}

	public static AttributeModifiersComponent createAttributeModifiers()
	{
		return AttributeModifiersComponent.builder()
			.add(
				EntityAttributes.GENERIC_ATTACK_KNOCKBACK,
				new EntityAttributeModifier(
					Identifier.ofVanilla("base_attack_knockback"), 2, EntityAttributeModifier.Operation.ADD_VALUE
				),
				AttributeModifierSlot.MAINHAND
			)
			.build();
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return !miner.isCreative();
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		return true;
	}
}