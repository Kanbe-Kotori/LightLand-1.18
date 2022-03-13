package dev.hikarishima.lightland.content.magic.gui.craft;

import com.google.common.collect.Maps;
import dev.hikarishima.lightland.content.common.capability.LLPlayerData;
import dev.hikarishima.lightland.content.magic.item.MagicScroll;
import dev.hikarishima.lightland.content.magic.item.MagicWand;
import dev.hikarishima.lightland.content.magic.item.ManaStorage;
import dev.hikarishima.lightland.content.magic.products.MagicElement;
import dev.hikarishima.lightland.content.magic.products.MagicProduct;
import dev.hikarishima.lightland.content.magic.spell.internal.Spell;
import dev.hikarishima.lightland.content.magic.spell.internal.SpellConfig;
import dev.hikarishima.lightland.init.LightLand;
import dev.hikarishima.lightland.init.data.LangData;
import dev.hikarishima.lightland.init.special.MagicRegistry;
import dev.lcy0x1.menu.BaseContainerMenu;
import dev.lcy0x1.util.SpriteManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpellCraftContainer extends BaseContainerMenu<SpellCraftContainer> {

	public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "spell_craft");

	protected Error err = Error.NO_ITEM;
	protected Spell<?, ?> spell = null;
	protected Map<MagicElement, Integer> map = Maps.newLinkedHashMap();

	private SpellConfig config = null;
	private int consume = 0, total_cost = 0, available = 0, ench_count = 0, exceed = 0;
	private boolean changing = false;

	public SpellCraftContainer(MenuType<SpellCraftContainer> type, int wid, Inventory plInv) {
		super(type, wid, plInv, MANAGER, (menu) -> new BaseContainer<>(5, menu), false);
		addSlot("wand_slot", stack -> stack.getItem() instanceof MagicWand);
		addSlot("input_slot", stack -> stack.getItem() instanceof MagicScroll);
		addSlot("ench_slot", stack -> stack.getItem() instanceof ManaStorage);
		addSlot("output_slot", stack -> false);
		addSlot("gold_slot", stack -> false);
	}

	@Override
	public void slotsChanged(Container inv) {
		if (changing)
			return;
		err = check();
		super.slotsChanged(inv);
	}

	private Error check() {
		spell = null;
		config = null;
		consume = 0;
		total_cost = 0;
		available = 0;
		ench_count = 0;
		exceed = 0;
		map.clear();
		ItemStack wand = container.getItem(0);
		ItemStack input = container.getItem(1);
		ItemStack ench = container.getItem(2);
		ItemStack output = container.getItem(3);
		ItemStack gold = container.getItem(4);
		if (wand.isEmpty() || input.isEmpty() || !output.isEmpty()) {
			return Error.NO_ITEM;
		}
		if (!(wand.getItem() instanceof MagicWand)) {
			return Error.NO_SPELL;
		}
		MagicWand magicWand = (MagicWand) wand.getItem();
		MagicProduct<?, ?> prod = magicWand.getData(inventory.player, wand);
		if (prod == null || prod.type != MagicRegistry.MPT_SPELL.get()) {
			return Error.NO_SPELL;
		}
		if (!prod.usable()) {
			return Error.LOCKED;
		}
		for (MagicElement elem : prod.recipe.getElements()) {
			if (map.containsKey(elem))
				map.put(elem, map.get(elem) + 1);
			else map.put(elem, 1);
		}
		LLPlayerData handler = LLPlayerData.get(inventory.player);
		for (MagicElement elem : map.keySet()) {
			if (map.get(elem) > handler.magicHolder.getElement(elem))
				return Error.ELEM;
		}
		spell = (Spell<?, ?>) prod.item;
		config = spell.getConfig(inventory.player.level, inventory.player);
		int cost = config.mana_cost;
		MagicScroll.ScrollType type = config.type;
		if (!(input.getItem() instanceof MagicScroll magicScroll)) {
			return Error.WRONG_SCROLL;
		}
		if (magicScroll.type != type) {
			return Error.WRONG_SCROLL;
		}
		int count = input.getCount();
		total_cost = count * cost;
		if (ench.isEmpty() || !(ench.getItem() instanceof ManaStorage)) {
			return Error.NOT_ENOUGH_MANA;
		}
		int mana = ((ManaStorage) ench.getItem()).mana;
		consume = total_cost / mana + (total_cost % mana > 0 ? 1 : 0);
		ench_count = ench.getCount();
		available = ench.getCount() * mana;
		exceed = consume * mana - total_cost;
		if (ench.getCount() < consume) {
			return Error.NOT_ENOUGH_MANA;
		}
		if (!gold.isEmpty()) {
			if (gold.getItem() != ((ManaStorage) ench.getItem()).container)
				return Error.CLEAR_GOLD;
			if (64 - gold.getCount() < consume)
				return Error.CLEAR_GOLD;
		}
		return Error.PASS;
	}

	@Override
	public boolean clickMenuButton(Player pl, int btn) {
		if (err == Error.PASS) {
			changing = true;
			ItemStack input = container.getItem(1);
			container.setItem(1, ItemStack.EMPTY);
			MagicScroll.initItemStack(spell, input);
			container.setItem(3, input);
			ItemStack ench = container.getItem(2);
			ench.shrink(consume);
			ItemStack gold = container.getItem(4);
			if (!gold.isEmpty())
				gold.grow(consume);
			else container.setItem(4, new ItemStack(((ManaStorage) ench.getItem()).container, consume));
			LLPlayerData handler = LLPlayerData.get(inventory.player);
			for (MagicElement elem : map.keySet()) {
				handler.magicHolder.addElement(elem, -map.get(elem));
			}
			changing = false;
			slotsChanged(container);
			return true;
		}
		return super.clickMenuButton(pl, btn);
	}

	public enum Error implements LangData.LangEnum<Error> {
		NO_ITEM(0),
		NO_SPELL(0),
		LOCKED(0),
		WRONG_SCROLL(0),
		NOT_ENOUGH_MANA(3),
		CLEAR_GOLD(0),
		ELEM(0),
		PASS(1);

		final int count;

		Error(int count) {
			this.count = count;
		}

		public Component getDesc(SpellCraftContainer cont) {
			if (this == WRONG_SCROLL)
				return LangData.get(this).append(cont.config.type.toItem().getDescription());
			if (this == NOT_ENOUGH_MANA)
				return LangData.get(this, cont.consume - cont.ench_count, cont.total_cost, cont.available);
			if (this == PASS)
				return LangData.get(this, cont.exceed);
			return LangData.get(this);
		}

		@Override
		public int getCount() {
			return count;
		}

	}

}
