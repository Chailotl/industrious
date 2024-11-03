package com.chailotl.industrious.item;

import com.chailotl.industrious.entity.BallEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BallItem extends Item
{
	public BallItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getMainHandStack();

		world.playSound(
			null,
			user.getX(),
			user.getY(),
			user.getZ(),
			SoundEvents.ENTITY_SNOWBALL_THROW,
			SoundCategory.NEUTRAL,
			0.5F,
			0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
		);

		if (!world.isClient)
		{
			BallEntity ball = new BallEntity(world, user);
			ball.setStack(stack);
			ball.setVelocity(user, user.getPitch(), user.getYaw(), -20, 1f, 1f);
			world.spawnEntity(ball);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		stack.decrementUnlessCreative(1, user);
		return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
	}
}