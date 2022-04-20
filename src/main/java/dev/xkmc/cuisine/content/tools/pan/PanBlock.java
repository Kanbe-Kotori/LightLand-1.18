package dev.xkmc.cuisine.content.tools.pan;

import dev.lcy0x1.block.impl.BlockEntityBlockMethodImpl;
import dev.lcy0x1.block.mult.CreateBlockStateBlockMethod;
import dev.lcy0x1.block.mult.DefaultStateBlockMethod;
import dev.lcy0x1.block.mult.OnClickBlockMethod;
import dev.lcy0x1.block.one.BlockEntityBlockMethod;
import dev.lcy0x1.block.one.LightBlockMethod;
import dev.xkmc.cuisine.content.tools.base.CuisineUtil;
import dev.xkmc.cuisine.init.data.CuisineTags;
import dev.xkmc.cuisine.init.registrate.CuisineBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Objects;

public class PanBlock implements CreateBlockStateBlockMethod, DefaultStateBlockMethod, OnClickBlockMethod, LightBlockMethod {

	public static final BlockEntityBlockMethod<PanBlockEntity> TE = new BlockEntityBlockMethodImpl<>(CuisineBlocks.TE_PAN, PanBlockEntity.class);

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.LIT, BlockStateProperties.SIGNAL_FIRE, BlockStateProperties.OPEN);
	}

	@Override
	public BlockState getDefaultState(BlockState state) {
		return state.setValue(BlockStateProperties.LIT, false).setValue(BlockStateProperties.SIGNAL_FIRE, false).setValue(BlockStateProperties.OPEN, false);
	}

	@Override
	public InteractionResult onClick(BlockState bs, Level w, BlockPos pos, Player pl, InteractionHand h, BlockHitResult r) {
		ItemStack stack = pl.getItemInHand(h);
		boolean open = bs.getValue(BlockStateProperties.OPEN);
		boolean lit = bs.getValue(BlockStateProperties.LIT);
		boolean cooking = bs.getValue(BlockStateProperties.SIGNAL_FIRE);
		PanBlockEntity te = Objects.requireNonNull((PanBlockEntity) w.getBlockEntity(pos));
		if (stack.getItem() instanceof FlintAndSteelItem) {
			if (!lit) {
				w.playSound(pl, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, w.getRandom().nextFloat() * 0.4F + 0.8F);
				BlockState state = bs.setValue(BlockStateProperties.LIT, true);
				stack.hurtAndBreak(1, pl, (player) -> player.broadcastBreakEvent(h));
				if (!w.isClientSide()) {
					if (!open) {
						if (te.startCooking()) {
							state = state.setValue(BlockStateProperties.SIGNAL_FIRE, true)
									.setValue(BlockStateProperties.OPEN, false);
						}
					}
					w.setBlockAndUpdate(pos, state);
				}
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.CONSUME;
			}
		}
		if (lit && pl.isShiftKeyDown()) {
			BlockState unlit = bs.setValue(BlockStateProperties.LIT, false)
					.setValue(BlockStateProperties.SIGNAL_FIRE, false);
			if (!w.isClientSide()) {
				te.stopCooking();
				w.setBlockAndUpdate(pos, unlit);
			}
			return InteractionResult.SUCCESS;
		}
		if (!lit && open && pl.isShiftKeyDown()) {
			if (!w.isClientSide())
				te.dumpInventory();
			return InteractionResult.SUCCESS;
		}
		if (pl.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		if (!open || stack.isEmpty() && te.result.isEmpty()) {
			if (!w.isClientSide()) {
				BlockState toggle_cap = bs.setValue(BlockStateProperties.OPEN, !open);
				if (!open && cooking) {
					toggle_cap = bs.setValue(BlockStateProperties.SIGNAL_FIRE, false);
					te.stopCooking();
				}
				if (open && lit) {
					if (te.startCooking())
						toggle_cap = bs.setValue(BlockStateProperties.SIGNAL_FIRE, true)
								.setValue(BlockStateProperties.OPEN, false);
				}
				w.setBlockAndUpdate(pos, toggle_cap);
			}
			return InteractionResult.SUCCESS;
		}
		if (!te.result.isEmpty()) {
			Ingredient container = CuisineUtil.getContainer(te.result);
			if (container.isEmpty() || container.test(stack)) {
				if (!w.isClientSide()) {
					if (!container.isEmpty())
						stack.shrink(1);
					pl.getInventory().placeItemBackInInventory(te.result.split(1));
					if (te.result.isEmpty()) {
						te.result = ItemStack.EMPTY;
					}
					te.notifyTile(null);
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.CONSUME;
		}
		if (!stack.isEmpty()) {
			LazyOptional<IFluidHandlerItem> opt = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
			if (opt.resolve().isPresent()) {
				IFluidHandlerItem item = opt.resolve().get();
				FluidStack fluidStack = item.getFluidInTank(0);
				if (!fluidStack.isEmpty() && CuisineTags.AllFluidTags.JAR_ACCEPT.matches(fluidStack.getFluid())) { //TODO tag
					return FluidUtil.interactWithFluidHandler(pl, h, w, pos, r.getDirection()) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
				}
			}
			if (stack.is(CuisineTags.AllItemTags.CAN_COOK.tag)) {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				ItemStack remain = te.inputInventory.addItem(copy);
				if (remain.isEmpty()) stack.shrink(1);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.FAIL;
		}

		return InteractionResult.FAIL;
	}

	@Override
	public int getLightValue(BlockState bs, BlockGetter w, BlockPos pos) {
		return bs.getValue(BlockStateProperties.LIT) ? 15 : 0;
	}
}