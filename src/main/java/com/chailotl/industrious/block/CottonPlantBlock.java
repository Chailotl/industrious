package com.chailotl.industrious.block;

import com.chailotl.industrious.Main;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CottonPlantBlock extends CropBlock implements Fertilizable
{
	public static final MapCodec<CottonPlantBlock> CODEC = createCodec(CottonPlantBlock::new);
	public static final int MAX_AGE = 4;
	public static final IntProperty AGE = Properties.AGE_4;
	private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(4, 0, 4, 12, 6, 12),
		Block.createCuboidShape(3, 0, 3, 13, 13, 13),
		Block.createCuboidShape(1, 0, 1, 15, 16, 15),
		Block.createCuboidShape(1, 0, 1, 15, 16, 15),
		Block.createCuboidShape(0, 0, 0, 16, 16, 16)
	};

	@Override
	public MapCodec<CottonPlantBlock> getCodec()
	{
		return CODEC;
	};

	public CottonPlantBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected IntProperty getAgeProperty()
	{
		return AGE;
	}

	@Override
	public int getMaxAge()
	{
		return MAX_AGE;
	}

	@Override
	protected ItemConvertible getSeedsItem()
	{
		return Main.COTTON_SEEDS;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (random.nextInt(3) != 0)
		{
			super.randomTick(state, world, pos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world)
	{
		return super.getGrowthAmount(world) / 2;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return AGE_TO_SHAPE[this.getAge(state)];
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}
}