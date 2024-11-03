package com.chailotl.industrious.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RubberBlock extends Block
{
	public static final MapCodec<RubberBlock> CODEC = createCodec(RubberBlock::new);

	@Override
	public MapCodec<RubberBlock> getCodec()
	{
		return CODEC;
	}

	public RubberBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance)
	{
		if (entity.bypassesLandingEffects())
		{
			super.onLandedUpon(world, state, pos, entity, fallDistance);
		}
		else
		{
			entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
		}
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity)
	{
		if (entity.bypassesLandingEffects())
		{
			super.onEntityLand(world, entity);
		}
		else
		{
			bounce(entity);
		}
	}

	private void bounce(Entity entity)
	{
		Vec3d vec3d = entity.getVelocity();
		if (vec3d.y < 0.0)
		{
			double d = entity instanceof LivingEntity ? 0.75 : 0.6;
			entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
		}
	}
}