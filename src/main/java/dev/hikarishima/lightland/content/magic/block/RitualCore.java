package dev.hikarishima.lightland.content.magic.block;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.recipe.ritual.AbstractMagicCraftRecipe;
import com.hikarishima.lightland.magic.registry.MagicContainerRegistry;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.magic.registry.item.magic.MagicWand;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.base.block.mult.AnimateTickBlockMethod;
import com.lcy0x1.base.block.mult.OnClickBlockMethod;
import com.lcy0x1.base.block.mult.ScheduleTickBlockMethod;
import com.lcy0x1.base.block.type.BlockMethod;
import com.lcy0x1.base.block.type.TileEntitySupplier;
import com.lcy0x1.core.util.SerialClass;
import dev.hikarishima.lightland.content.magic.ritual.AbstractMagicRitualRecipe;
import dev.lcy0x1.base.BaseRecipe;
import dev.lcy0x1.base.TickableBlockEntity;
import dev.lcy0x1.block.impl.BlockEntityBlockMethodImpl;
import dev.lcy0x1.block.mult.AnimateTickBlockMethod;
import dev.lcy0x1.block.mult.OnClickBlockMethod;
import dev.lcy0x1.block.mult.ScheduleTickBlockMethod;
import dev.lcy0x1.block.one.BlockEntityBlockMethod;
import dev.lcy0x1.block.type.BlockMethod;
import dev.lcy0x1.block.type.TileEntitySupplier;
import dev.lcy0x1.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RitualCore {
    public static final BlockEntityBlockMethod<TE> TILE_ENTITY_SUPPLIER_BUILDER = new BlockEntityBlockMethodImpl<>(null,TE.class,TE::new);

    public static class Activate implements ScheduleTickBlockMethod, OnClickBlockMethod, AnimateTickBlockMethod {

        @Override
        public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TE) {
                ((TE) te).activate(null, null);
            }
        }

        @Override
        public InteractionResult onClick(BlockState bs, Level w, BlockPos pos, Player pl, InteractionHand h, BlockHitResult r) {
            if (w.isClientSide()) {
                return pl.getMainHandItem().getItem() instanceof MagicWand ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
            if (pl.getMainHandItem().getItem() instanceof MagicWand) {
                BlockEntity te = w.getBlockEntity(pos);
                if (te instanceof TE) {
                    MagicProduct<?, ?> magic = MagicItemRegistry.GILDED_WAND.get().getData(pl, pl.getMainHandItem());
                    ((TE) te).activate(pl, magic);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        @Override
        public void animateTick(BlockState state, Level world, BlockPos pos, Random r) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (!(tile instanceof TE)) return;
            TE te = (TE) tile;
            if (!te.isLocked()) return;
            List<RitualSide.TE> side = te.getSide();
            if (side.size() < 8) return;
            for (RitualSide.TE ste : side) {
                if (ste.isEmpty()) continue;
                BlockPos spos = ste.getBlockPos();
                world.addAlwaysVisibleParticle(ParticleTypes.ENCHANT,
                        pos.getX() + 0.5,
                        pos.getY() + 2.5,
                        pos.getZ() + 0.5,
                        spos.getX() - pos.getX() + r.nextFloat() - 0.5,
                        spos.getY() - pos.getY() - r.nextFloat() - 0.5,
                        spos.getZ() - pos.getZ() + r.nextFloat() - 0.5);
            }
        }
    }

    public static final BlockMethod CLICK = new RitualTE.RitualPlace();
    public static final BlockMethod ACTIVATE = new Activate();
    public static final int[][] POS = {{-2, -2}, {-3, 0}, {-2, 2}, {0, -3}, {0, 3}, {2, -2}, {3, 0}, {2, 2}};

    @SerialClass
    public static class TE extends RitualTE implements TickableBlockEntity {

        public AbstractMagicRitualRecipe<?> recipe = null;

        @SerialClass.SerialField
        public int remainingTime = 0;

        @SerialClass.SerialField
        public int lv = 0;

        public TE() {
            super(MagicContainerRegistry.TE_RITUAL_CORE.get());
        }

        public void activate(@Nullable Player player, @Nullable MagicProduct<?, ?> magic) {
            if (level == null || level.isClientSide()) {
                return;
            }
            List<RitualSide.TE> list = getSide();
            if (list.size() < 8) {
                return;
            }
            //TODO sideness
            Inv inv = new Inv(this, list);
            Optional<AbstractMagicRitualRecipe<?>> r = level.getRecipeManager().getRecipeFor(MagicRecipeRegistry.RT_CRAFT, inv, level);
            r.ifPresent(e -> {
                Map<MagicElement, Integer> map = new LinkedHashMap<>();
                if (e.getMagic() != null) {
                    if (magic == null || magic.getCost() <= 0 || !e.getMagic().equals(magic.recipe.id) || player == null) {
                        send(player, Translator.get(true, "chat.ritual.fail.wrong"));
                        return;
                    }
                    lv = e.getLevel(magic.getCost());
                    if (lv == 0) {
                        send(player, Translator.get(true, "chat.ritual.fail.zero"));
                        return;
                    }
                    if (magic.type == MagicRegistry.MPT_ENCH || magic.type == MagicRegistry.MPT_EFF) {
                        MagicElement[] elems = magic.recipe.getElements();
                        for (MagicElement elem : elems) {
                            map.put(elem, map.getOrDefault(elem, 0) + lv);
                        }
                        for (MagicElement elem : map.keySet()) {
                            int has = MagicHandler.get(player).magicHolder.getElement(elem);
                            int take = map.get(elem);
                            if (has < take) {
                                send(player, Translator.get(true, "chat.ritual.fail.element"));
                                return;
                            }
                        }
                    }
                }
                recipe = e;
                remainingTime = 200;
                setLocked(true, list);
                if (player != null) {
                    for (MagicElement elem : map.keySet()) {
                        MagicHandler.get(player).magicHolder.addElement(elem, -map.get(elem));
                    }
                    PacketHandler.toClient((ServerPlayerEntity) player, new ToClientMsg(ToClientMsg.Action.ALL, MagicHandler.get(player)));
                }
            });

        }

        private void send(@Nullable Player player, Component text) {
            if (player == null) return;
            Level world = player.level;
            if (world == null) return;
            MinecraftServer server = world.getServer();
            if (server == null)
                return;
            server.getPlayerList().broadcastMessage(text, ChatType.GAME_INFO, player.getUUID());
        }

        private List<RitualSide.TE> getSide() {
            assert level != null;
            List<RitualSide.TE> list = new ArrayList<>();
            for (int[] dire : POS) {
                BlockEntity te = level.getBlockEntity(getBlockPos().offset(dire[0], 0, dire[1]));
                if (te instanceof RitualSide.TE) {
                    list.add((RitualSide.TE) te);
                }
            }
            return list;
        }

        @Override
        public void tick() {
            if (remainingTime <= 0 || level == null || level.isClientSide())
                return;
            remainingTime--;
            if (remainingTime % 4 != 0) {
                return;
            }
            List<RitualSide.TE> list = getSide();
            if (list.size() == 8 && recipe == null) {
                Inv inv = new Inv(this, list);
                Optional<AbstractMagicRitualRecipe<?>> r = level.getRecipeManager().getRecipeFor(MagicRecipeRegistry.RT_CRAFT, inv, level);
                if (r.isPresent()) {
                    recipe = r.get();
                } else {
                    remainingTime = 0;
                    lv = 0;
                    setLocked(false, list);
                    setChanged();
                    return;
                }
            }
            if (list.size() < 8 || !match(list)) {
                recipe = null;
                remainingTime = 0;
                lv = 0;
                setLocked(false, list);
                setChanged();
                return;
            }
            if (remainingTime == 0) {
                Inv inv = new Inv(this, list);
                recipe.assemble(inv, lv);
                lv = 0;
                recipe = null;
                setLocked(false, list);
            }
            setChanged(); // mark the tile entity dirty every 4 ticks
        }

        private void setLocked(boolean bool, List<RitualSide.TE> list) {
            setLocked(bool);
            for (RitualSide.TE te : list) {
                te.setLocked(bool);
            }
        }

        private boolean match(List<RitualSide.TE> list) {
            if (level == null || recipe == null || list.size() < 8) {
                return false;
            }
            Inv inv = new Inv(this, list);
            return recipe.matches(inv, level);
        }

    }

    public static class Inv implements BaseRecipe.RecInv<AbstractMagicRitualRecipe<?>> {

        public final TE core;
        public final List<RitualSide.TE> sides;

        private Inv(TE core, List<RitualSide.TE> sides) {
            this.core = core;
            this.sides = sides;
        }

        private SyncedSingleItemTE getSlot(int slot) {
            return slot < 5 ? sides.get(slot) : slot == 5 ? core : sides.get(slot - 1);
        }

        @Override
        public int getContainerSize() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getItem(int slot) {
            return getSlot(slot).getItem(0);
        }

        @Override
        public ItemStack removeItem(int slot, int count) {
            return getSlot(slot).removeItem(0, count);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return getSlot(slot).removeItemNoUpdate(0);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            getSlot(slot).setItem(0, stack);
        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            core.clearContent();
            for (RitualSide.TE te : sides)
                te.clearContent();
        }
    }

}
