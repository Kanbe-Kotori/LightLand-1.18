package dev.xkmc.cuisine.content.veges;

import dev.xkmc.cuisine.init.data.CuisineTemplates;

public class BlockCorn extends BlockDoubleCrops {

	public BlockCorn(CuisineTemplates.Veges type, Properties props) {
		super(type, props);
	}

	/*
	private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

	public BlockCorn(String name) {
		super(name, ItemDefinition.of(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.CORN)));
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (isUpper(state)) {
			IBlockState state2 = source.getBlockState(pos.down());
			if (state2.getBlock() == this && !isUpper(state2)) {
				return CROPS_AABB[state2.getValue(getAgeProperty())];
			}
		}
		return FULL_BLOCK_AABB;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		// NO-OP
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		IBlockState target = state;
		if (isUpper(target)) {
			pos = pos.down();
			target = worldIn.getBlockState(pos);
			if (target.getBlock() != this || isUpper(target)) {
				return false;
			}
		}
		BlockPos upPos = pos.up();
		IBlockState upState = worldIn.getBlockState(upPos);
		return !worldIn.isOutsideBuildHeight(upPos) && (upState.getBlock() == this || upState.getBlock().isReplaceable(worldIn, upPos));
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getBlock() != this)
			return super.canBlockStay(worldIn, pos, state);

		if (isUpper(state)) {
			return worldIn.getBlockState(pos.down()).getBlock() == this;
		} else {
			return getAge(state, worldIn, pos) < 2 || super.canBlockStay(worldIn, pos, state);
		}
	}

	@SubscribeEvent
	public static void onCropsGrowPost(BlockEvent.CropGrowEvent event) {
		IBlockState state = event.getState();
		if (state.getBlock() instanceof BlockCorn && ((BlockCorn) state.getBlock()).getAge(state, event.getWorld(), event.getPos()) > 1) {
			event.getWorld().setBlockState(event.getPos().up(), ((BlockCorn) state.getBlock()).withAge(8));
		}
	}*/

}