package dev.lcy0x1.base;

import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface BaseContainerListener extends ContainerListener {

	void notifyTile();

	@SuppressWarnings({"unsafe", "unchecked"})
	@Override
	default void containerChanged(Container cont) {
		notifyTile();
	}
}
