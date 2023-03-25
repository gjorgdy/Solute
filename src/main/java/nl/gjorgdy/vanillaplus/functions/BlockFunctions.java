package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFunctions {

    public static void updateBlock(World world, BlockPos pos, boolean save) {
        // Force saving sign
        if (save) {
            world.markDirty(pos);
        }
        // Force updates to players
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

}
