package dev.xkmc.lightland.content.magic.item.oriental;

import dev.xkmc.lightland.content.magic.item.oriental.circle.AbstractCircleMagic;
import net.minecraft.world.item.Item;

public class ModeUpgrade extends Item {

    public final AbstractCircleMagic.CircleMode upgradeType;
    public ModeUpgrade(Properties props, AbstractCircleMagic.CircleMode mode) {
        super(props);
        this.upgradeType = mode;
    }
}
