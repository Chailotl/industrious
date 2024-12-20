package com.chailotl.industrious.item;

import com.chailotl.industrious.entity.BallEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BallItem extends Item
{
	public BallItem(Settings settings)
	{
		super(settings);

		DispenserBlock.registerBehavior(this, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack)
			{
				World world = pointer.world();
				Direction direction = pointer.state().get(DispenserBlock.FACING);
				Position position = DispenserBlock.getOutputLocation(pointer, 0.7, new Vec3d(0.0, 0.1, 0.0));
				BallEntity ballEntity = new BallEntity(world, position.getX(), position.getY(), position.getZ());
				ballEntity.setStack(stack);
				ballEntity.setVelocity(
					direction.getOffsetX(),
					direction.getOffsetY(),
					direction.getOffsetZ(),
					1f,
					1f
				);

				world.spawnEntity(ballEntity);
				stack.decrement(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer)
			{
				pointer.world().syncWorldEvent(1002, pointer.pos(), 0);
			}
		});
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
			BallEntity ballEntity = new BallEntity(world, user);
			ballEntity.setStack(stack);
			ballEntity.setVelocity(user, user.getPitch(), user.getYaw(), -20, 1f, 1f);
			world.spawnEntity(ballEntity);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		stack.decrementUnlessCreative(1, user);
		return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		PlayerEntity user = context.getPlayer();

		if (user.isSneaking())
		{
			World world = context.getWorld();
			ItemStack stack = context.getStack();
			BlockPos blockPos = context.getBlockPos();
			Direction direction = context.getSide();
			BlockState blockState = world.getBlockState(blockPos);

			if (!blockState.getCollisionShape(world, blockPos).isEmpty())
			{
				blockPos = blockPos.offset(direction);
			}

			world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.BLOCK_WOOL_PLACE,
				SoundCategory.NEUTRAL,
				0.5F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
			);

			if (!world.isClient)
			{
				BallEntity ballEntity = new BallEntity(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
				ballEntity.setStack(stack);
				world.spawnEntity(ballEntity);
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			stack.decrementUnlessCreative(1, user);
			world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
			return ActionResult.CONSUME;
		}

		return ActionResult.PASS;
	}
}