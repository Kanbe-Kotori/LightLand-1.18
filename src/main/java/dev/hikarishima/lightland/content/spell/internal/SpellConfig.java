package dev.hikarishima.lightland.content.spell.internal;

import dev.lcy0x1.util.SerialClass;

@SerialClass
public class SpellConfig {
/*
    @SerialClass.SerialField
    public int duration, mana_cost, spell_load;
    @SerialClass.SerialField
    public float factor = 1f;
    @SerialClass.SerialField public MagicScroll.ScrollType type;

    public static <C extends SpellConfig> C get(Spell<C, ?> spell, Level world, Player player) {
        C ans = ConfigRecipe.getObject(world, MagicRecipeRegistry.SPELL, spell.getID());
        if (ans == null)
            return null;
        IMagicRecipe<?> r = IMagicRecipe.getMap(world, MagicRegistry.MPT_SPELL).get(spell);
        if (r == null)
            return ans;
        MagicProduct<?, ?> p = LLPlayerData.get(player).magicHolder.getProduct(r);
        if (p == null || !p.usable())
            return ans;
        ans = makeCopy(ans);
        ans.mana_cost += p.getCost() * ans.factor;
        ans.spell_load += p.getCost() * ans.factor;
        return ans;
    }

    public static <C extends SpellConfig> C makeCopy(C config) {
        return Automator.fromTag(Automator.toTag(new CompoundTag(), config), config.getClass());
    }

    @SerialClass
    public static class SpellDisplay {

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public int duration, setup, close;

    }TODO*/

}
