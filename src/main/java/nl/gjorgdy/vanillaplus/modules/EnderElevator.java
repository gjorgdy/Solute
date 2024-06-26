package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.utils.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class EnderElevator {

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

    public void activate() {
        List<Entity> entities = getEntities();
        if (entities.isEmpty()) return;
        BlockPos destination = getDestinationPosition();
        if (destination == null) return;
        entities.forEach(entity -> safeTeleport(entity, destination));
    }

    private List<Entity> getEntities() {
        return world.getOtherEntities(null, new Box(
            pos.toCenterPos().add(-0.5, 0, -0.5),
            pos.toCenterPos().add(0.5, 2, 0.5)
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

        BlockState[] blockStates = new BlockState[]{
            world.getBlockState(blockPos),
            world.getBlockState(blockPos.up(1)),
            world.getBlockState(blockPos.up(2))
        };

        if ( !blockStates[1].shouldSuffocate(world, blockPos) && !blockStates[1].shouldSuffocate(world, blockPos) ) {

            double dx = playerPos.getX() - blockPos.toCenterPos().x;
            double dY = BlockUtils.isBottomSlab(blockStates[0]) ? 0.5 : 1;
            dY = BlockUtils.isBottomSlab(blockStates[1]) ? 1.5 : dY;
            double dz = playerPos.getZ() - blockPos.toCenterPos().z;

            TeleportTarget teleportTarget = new TeleportTarget(
                (ServerWorld) world,
                new Vec3d(
                    blockPos.toCenterPos().x + Math.min(0.2, Math.max(-0.2, dx)),
                    ((double) blockPos.getY() + dY + 0.15),
                    blockPos.toCenterPos().z + Math.min(0.2, Math.max(-0.2, dz))
                ),
                player.getVelocity(),
                player.getYaw(),
                player.getPitch(),
                EnderElevator::enderTeleport
            );

            player.teleportTo(teleportTarget);
        }
    }

    private static void enderTeleport(Entity entity) {
        if (entity instanceof PlayerEntity playerEntity) {
            entity.getWorld().playSound(playerEntity, entity.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS);
        }
        entity.getWorld().sendEntityStatus(entity, (byte)46);
    }

}
