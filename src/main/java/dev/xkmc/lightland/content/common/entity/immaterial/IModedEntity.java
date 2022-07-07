package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.content.magic.item.oriental.circle.AbstractCircleMagic;

public interface IModedEntity {

    void powerful();

    void efficient();

    void speedup();

    default void setMode(AbstractCircleMagic.CircleMode mode) {
        switch (mode) {
            case POWERFUL -> powerful();
            case SPEEDUP -> speedup();
            case EFFICIENT -> efficient();
        }
    }
}
