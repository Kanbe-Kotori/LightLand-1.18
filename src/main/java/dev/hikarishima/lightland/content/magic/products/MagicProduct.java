package dev.hikarishima.lightland.content.magic.products;

import dev.hikarishima.lightland.content.common.capability.LLPlayerData;
import dev.hikarishima.lightland.content.magic.products.info.ProductState;
import dev.hikarishima.lightland.content.magic.products.recipe.IMagicRecipe;
import dev.lcy0x1.magic.HexHandler;
import dev.lcy0x1.util.Automator;
import dev.lcy0x1.util.NBTObj;
import dev.lcy0x1.util.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class MagicProduct<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> extends IMagicProduct<I, P> {

    public static final int LOCKED = -2, UNLOCKED = -1;

    public final NBTObj tag;
    public final LLPlayerData player;
    public final IMagicRecipe<?> recipe;

    public MagicProduct(MagicProductType<I, P> type, LLPlayerData player, NBTObj tag, ResourceLocation rl, IMagicRecipe<?> r) {
        super(type, rl);
        this.tag = tag;
        this.player = player;
        this.recipe = r;
        if (tag != null) {
            if (!tag.tag.contains("_base")) {
                tag.getSub("_base").tag.putInt("cost", LOCKED);
            }
        }
    }

    protected final NBTObj getBase() {
        return tag.getSub("_base");
    }

    public final boolean unlocked() {
        return getBase().tag.getInt("cost") > -2;
    }

    public final int getCost() {
        return getBase().tag.getInt("cost");
    }

    public final void setUnlock() {
        if (!unlocked())
            getBase().tag.putInt("cost", -1);
    }

    public void updateBestSolution(HexHandler hex, HexData data, int cost) {
        int prev = getBase().tag.getInt("cost");
        tag.tag.remove("hex");
        Automator.toTag(tag.getSub("misc").tag, data);
        hex.write(tag.getSub("hex"));
        getBase().tag.putInt("cost", cost);
    }

    public HexHandler getSolution() {
        if (!tag.tag.contains("hex"))
            return new HexHandler(3);
        return new HexHandler(tag.getSub("hex"));
    }

    public final boolean usable() {
        return getBase().tag.getInt("cost") > UNLOCKED;
    }

    public ProductState getState() {
        switch (getBase().tag.getInt("cost")) {
            case LOCKED:
                return ProductState.LOCKED;
            case UNLOCKED:
                return ProductState.UNLOCKED;
            default:
                return ProductState.CRAFTED;
        }
    }

    public boolean visible() {
        return true;
    }

    public HexData getMiscData() {
        HexData data;
        if (tag.tag.contains("misc")) {
            data = Automator.fromTag(tag.getSub("misc").tag, HexData.class);
        } else {
            data = new HexData();
        }
        if (data.list.size() == 0)
            data.list.add(type.elem);
        else if (data.list.get(0) != type.elem)
            data.list.set(0, type.elem);
        return data;
    }

    public boolean matchList(List<MagicElement> elem) {
        return elem.equals(getMiscData().list);
    }

    public CodeState logged(LLPlayerData handler) {
        if (!usable())
            return null;
        List<MagicElement> list = getMiscData().list;
        if (list.size() < 4)
            return CodeState.SHORT;
        return handler.magicHolder.getTree(getMiscData().list) == recipe ? CodeState.FINE : CodeState.REPEAT;
    }

    @SerialClass
    public static class HexData {

        @SerialClass.SerialField
        public int[] order;

        @SerialClass.SerialField(generic = MagicElement.class)
        public ArrayList<MagicElement> list = new ArrayList<>();

    }

    public enum CodeState {
        SHORT, REPEAT, FINE
    }

}
