package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.utils.BlockUtils;

import java.util.List;

public class EnderElevator extends Thread {

    static final List<Block> elevatorBlocks = List.of(
            Blocks.PURPUR_BLOCK,
            Blocks.PURPUR_PILLAR,
            Blocks.PURPUR_SLAB,
            Blocks.PURPUR_STAIRS
    );
    static final int range = 16;

    private final World world;
    private final BlockPos pos;

    public EnderElevator(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Override
    public void run() {
        List<Entity> entities = getEntities();
        if (entities.isEmpty()) return;
        BlockPos destination = getDestinationPosition();
        if (destination == null) return;
        entities.forEach(entity -> safeTeleport(entity, destination));
    }

    private List<Entity> getEntities() {
        return world.getOtherEntities(null, new Box(
            pos.north().east().toCenterPos(),
            pos.west().south().up(2).toCenterPos()
        ));
    }

    private BlockPos getDestinationPosition() {
        for (int i = 1; i < range; i++) {
            BlockPos posAbove = pos.up(i);
            BlockPos posBelow = pos.down(i);
            if (isElevatorBlock(posAbove)) {
                return posAbove;
            } else if (isElevatorBlock(posBelow)) {
                return posBelow;
            }
        }
        return null;
    }

    private boolean isElevatorBlock(BlockPos pos) {
        return isElevatorBlock(world, pos);
    }

    public static boolean isElevatorBlock(World world, BlockPos pos) {
        return elevatorBlocks.contains(world.getBlockState(pos).getBlock());
    }

    public static boolean isElevatorBlock(Block block) {
        return elevatorBlocks.contains(block);
    }

    /**
     * Teleports the player to the BlockPos if location is valid
     *
     * @param player   instance of player to teleport
     * @param blockPos location to teleport player to
     */
    private void safeTeleport(Entity player, BlockPos blockPos) {
        World world = player.getWorld();
        Vec3d playerPos = player.getPos();
        BlockState floorBlock = world.getBlockState(blockPos);
        BlockState bottomBlock = world.getBlockState(blockPos.add(0,1,0));
        BlockState topBlock = world.getBlockState(blockPos.add(0,2,0));
        if ( !bottomBlock.shouldSuffocate(world, blockPos) && !topBlock.shouldSuffocate(world, blockPos) ) {
            Vec3d velocity = player.getVelocity();
            double dY = BlockUtils.isBottomSlab(floorBlock) ? 0.5 : 1;
            dY = BlockUtils.isBottomSlab(bottomBlock) ? 1.5 : dY;
            player.requestTeleport(
                (playerPos.getX()),
                ((double) blockPos.getY() + dY + 0.15),
                (playerPos.getZ())
            );
            player.setVelocity(velocity);
            player.velocityModified = true;
            world.sendEntityStatus(player, (byte)46);
        }
    }

}
