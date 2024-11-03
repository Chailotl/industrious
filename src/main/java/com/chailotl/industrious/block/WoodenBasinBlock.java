package com.chailotl.industrious.block;

import com.chailotl.industrious.Main;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class WoodenBasinBlock extends Block
{
	public static final MapCodec<WoodenBasinBlock> CODEC = createCodec(WoodenBasinBlock::new);
	public static final DirectionProperty FACING = DirectionProperty.of("facing", facing -> facing != Direction.DOWN);
	public static final int NUM_LEVELS = 4;
	public static final int MIN_LEVEL = 0;
	public static final int MAX_LEVEL = 3;
	public static final IntProperty LEVEL = IntProperty.of("level", MIN_LEVEL, MAX_LEVEL);
	protected static final VoxelShape DEFAULT_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(1.0, 1.0, 1.0, 15.0, 2.0, 15.0),
		Block.createCuboidShape(1.0, 1.0, 1.0, 15.0, 8.0, 2.0),
		Block.createCuboidShape(1.0, 1.0, 1.0, 2.0, 8.0, 15.0),
		Block.createCuboidShape(1.0, 1.0, 14.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(14.0, 1.0, 1.0, 15.0, 8.0, 15.0)
	);
	protected static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
		DEFAULT_SHAPE.offset(0, 2.0/16.0, 0),
		Block.createCuboidShape(1.0, 2.0, 4.0, 3.0, 3.0, 16.0),
		Block.createCuboidShape(13.0, 2.0, 4.0, 15.0, 3.0, 16.0)
	);
	protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
		DEFAULT_SHAPE.offset(0, 2.0/16.0, 0),
		Block.createCuboidShape(1.0, 2.0, 0.0, 3.0, 3.0, 12.0),
		Block.createCuboidShape(13.0, 2.0, 0.0, 15.0, 3.0, 12.0)
	);
	protected static final VoxelShape WEST_SHAPE = VoxelShapes.union(
		DEFAULT_SHAPE.offset(0, 2.0/16.0, 0),
		Block.createCuboidShape(4.0, 2.0, 1.0, 16.0, 3.0, 3.0),
		Block.createCuboidShape(4.0, 2.0, 13.0, 16.0, 3.0, 15.0)
	);
	protected static final VoxelShape EAST_SHAPE = VoxelShapes.union(
		DEFAULT_SHAPE.offset(0, 2.0/16.0, 0),
		Block.createCuboidShape(0.0, 2.0, 1.0, 12.0, 3.0, 3.0),
		Block.createCuboidShape(0.0, 2.0, 13.0, 12.0, 3.0, 15.0)
	);

	@Override
	public MapCodec<WoodenBasinBlock> getCodec() {
		return CODEC;
	}

	public WoodenBasinBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState()
			.with(FACING, Direction.UP)
			.with(LEVEL, 0)
		);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return switch (state.get(FACING))
		{
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
			default -> DEFAULT_SHAPE;
		};
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isSideSolidFullSquare(world, blockPos, direction);
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
					return blockState;
				}
			}
			else
			{
				blockState = blockState.with(FACING, Direction.UP);
				if (blockState.canPlaceAt(worldView, blockPos))
				{
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (state.get(LEVEL) == 3)
		{
			emptyFullBasin(player, state, world, pos);
			return ActionResult.success(world.isClient);
		}
		else
		{
			return ActionResult.PASS;
		}
	}

	public static BlockState fill(BlockState state, World world, BlockPos pos)
	{
		int i = state.get(LEVEL);
		if (i < MAX_LEVEL)
		{
			state = state.with(LEVEL, i + 1);
			world.setBlockState(pos, state, Block.NOTIFY_ALL);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(null, state));
			return state;
		}
		else
		{
			return state;
		}
	}

	public static BlockState emptyFullBasin(Entity user, BlockState state, World world, BlockPos pos)
	{
		if (!world.isClient)
		{
			Vec3d vec3d = Vec3d.add(pos, 0.5, 1.01, 0.5).addRandom(world.random, 0.7F);
			ItemEntity itemEntity = new ItemEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), new ItemStack(Main.RAW_RUBBER));
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}

		BlockState blockState = state.with(LEVEL, MIN_LEVEL);
		world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
		world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
		return blockState;
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state)
	{
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos)
	{
		return state.get(LEVEL);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING).add(LEVEL);
	}
}