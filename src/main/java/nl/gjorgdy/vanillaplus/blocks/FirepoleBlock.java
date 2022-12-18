package nl.gjorgdy.vanillaplus.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.VanillaPlus;

public class FirepoleBlock extends PaneBlock {

    public FirepoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof PlayerEntity) {
            VanillaPlus.LOGGER.info("I got stepped on... ;)");
        }
    }


}
