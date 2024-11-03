package com.chailotl.industrious.block;

import com.chailotl.industrious.Main;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RubberTapBlock extends HorizontalFacingBlock
{
	public static final MapCodec<RubberTapBlock> CODEC = createCodec(RubberTapBlock::new);
	public static final BooleanProperty DRIPPING = BooleanProperty.of("dripping");
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 1.0, 10.0, 11.0, 7.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 1.0, 0.0, 11.0, 7.0, 6.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 1.0, 5.0, 16.0, 7.0, 11.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 1.0, 5.0, 6.0, 7.0, 11.0);

	@Override
	protected MapCodec<RubberTapBlock> getCodec()
	{
		return CODEC;
	}

	public RubberTapBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(DRIPPING, false));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return switch (state.get(FACING))
		{
			case EAST -> WEST_SHAPE;
			case WEST -> EAST_SHAPE;
			case SOUTH -> NORTH_SHAPE;
			default -> SOUTH_SHAPE;
		};
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare(world, blockPos, direction);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		BlockState blockState = getDefaultState();
		WorldView worldView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();

		for (Direction direction : ctx.getPlacementDirections())
		{
			if (direction.getAxis().isHorizontal())
			{
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(FACING, direction2);
				if (blockState.canPlaceAt(worldView, blockPos))
				{
					return worldView.getBlockState(blockPos.offset(direction)).isIn(Main.STRIPPED_JUNGLE_LOGS) ? blockState.with(DRIPPING, true) : blockState;
				}
			}
		}

		return null;
	}

	@Override
	protected boolean hasRandomTicks(BlockState state)
	{
		return state.get(DRIPPING);
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (world.random.nextInt(3) == 0)
		{
			if (state.get(DRIPPING))
			{
				for (int i = 1; i <= 5; ++i)
				{
					BlockState blockState = world.getBlockState(pos.down(i));

					if (!blockState.isAir())
					{
						if (blockState.isOf(Main.WOODEN_BASIN))
						{
							WoodenBasinBlock.fill(blockState, world, pos.down(i));
						}
						break;
					}
				}
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, DRIPPING);
	}
}