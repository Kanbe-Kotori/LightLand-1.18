package dev.lcy0x1.block;

import dev.lcy0x1.block.one.BlockEntityBlockMethod;
import dev.lcy0x1.block.type.BlockMethod;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DelegateBlock extends Block {

    public static DelegateBlock newBaseBlock(DelegateBlockProperties p, BlockMethod... impl) {
        for (BlockMethod m : impl) {
            if (m instanceof BlockEntityBlockMethod<?>) {
                return new DelegateEntityBlockImpl(p, impl);
            }
        }
        return new DelegateBlockImpl(p, impl);
    }

    protected DelegateBlock(Properties props) {
        super(props);
    }

}