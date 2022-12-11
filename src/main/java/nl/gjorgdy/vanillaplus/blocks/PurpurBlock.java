package nl.gjorgdy.vanillaplus.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PurpurBlock extends Block {

    public PurpurBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        Vec3d v = entity.getVelocity();
        entity.setVelocity(v.add(0, 10, 0));
        world.breakBlock(pos, false);
    }
}
