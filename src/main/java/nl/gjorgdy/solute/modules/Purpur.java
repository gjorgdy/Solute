package nl.gjorgdy.solute.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import nl.gjorgdy.solute.interfaces.ServerPlayerEntityInterface;
import nl.gjorgdy.solute.utils.BlockUtils;

import java.util.List;

public class Purpur {

    static final List<Block> elevatorBlocks = List.of(
            Blocks.PURPUR_BLOCK,
            Blocks.PURPUR_PILLAR,
            Blocks.PURPUR_SLAB,
            Blocks.PURPUR_STAIRS
    );
    static final int range = 16;

    public static void up(Entity entity) {
        activate(entity, true);
    }

    public static void down(Entity entity) {
        activate(entity, false);
    }

    private static void activate(Entity entity, boolean up) {
        if (entity instanceof ServerPlayerEntityInterface player) {
            if (player.solute$isOnElevatorCooldown()) return;
            else player.solute$setElevatorCooldown();
        }

        if (!isPoweredElevatorBlock(entity.getEntityWorld(), entity.getBlockPos().down())) return;
        for (int i = 2; i < range; i++) {
            BlockPos _pos = entity.getBlockPos().add(0, up ? (i) : (-1 * i), 0);
            if (isPoweredElevatorBlock(entity.getEntityWorld(), _pos)) {
                safeTeleport(entity, _pos);
                return;
            }
        }
    }

    public static boolean isPoweredElevatorBlock(World world, BlockPos pos) {
        return BlockUtils.isRedstonePowered(world, pos) &&
            elevatorBlocks.contains(world.getBlockState(pos).getBlock());
    }

    /**
     * Teleports the player to the BlockPos if the location is valid
     *
     * @param entity   instance of player to teleport
     * @param blockPos location to teleport player to
     */
    private static void safeTeleport(Entity entity, BlockPos blockPos) {
        World world = entity.getEntityWorld();
        Vec3d playerPos = entity.getEntityPos();

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

            double x = blockPos.toCenterPos().x + Math.min(0.2, Math.max(-0.2, dx));
            double y = ((double) blockPos.getY() + dY + 0.15);
            double z = blockPos.toCenterPos().z + Math.min(0.2, Math.max(-0.2, dz));

            teleportEntity(entity, new Vec3d(x, y, z));
        }
    }

    private static synchronized void teleportEntity(Entity entity, Vec3d destination) {
        if (!(entity.getEntityWorld() instanceof ServerWorld serverWorld)) return;
        TeleportTarget teleportTarget = new TeleportTarget(
                serverWorld,
                destination,
                entity.getVelocity().multiply(0.85),
                entity.getYaw(),
                entity.getPitch(),
                Purpur::enderEffect
        );

        enderEffect(entity);
        entity.teleportTo(teleportTarget);
    }

    private static void enderEffect(Entity entity) {
        if (entity instanceof ServerPlayerEntity playerEntity) {
            playerEntity.getEntityWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS);
        }
        entity.getEntityWorld().sendEntityStatus(entity, (byte)46);
    }

}
