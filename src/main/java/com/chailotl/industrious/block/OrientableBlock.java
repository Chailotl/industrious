package com.chailotl.industrious.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.Orientation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class OrientableBlock extends Block
{
	public static final MapCodec<OrientableBlock> CODEC = createCodec(OrientableBlock::new);
	private static final EnumProperty<Orientation> ORIENTATION = Properties.ORIENTATION;

	@Override
	protected MapCodec<? extends OrientableBlock> getCodec()
	{
		return CODEC;
	}

	public OrientableBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(ORIENTATION, Orientation.NORTH_UP));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		Direction direction = ctx.getPlayerLookDirection().getOpposite();

		Direction direction2 = switch (direction) {
			case DOWN -> ctx.getHorizontalPlayerFacing().getOpposite();
			case UP -> ctx.getHorizontalPlayerFacing();
			case NORTH, SOUTH, WEST, EAST -> Direction.UP;
		};
		return getDefaultState().with(ORIENTATION, Orientation.byDirections(direction, direction2));
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation)
	{
		return state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(ORIENTATION);
	}
}