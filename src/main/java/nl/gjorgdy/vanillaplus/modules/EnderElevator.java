package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.functions.BlockFunctions;

import java.util.List;

public class EnderElevator {

    static List<Block> elevatorBlocks = List.of(
            Blocks.PURPUR_BLOCK,
            Blocks.PURPUR_PILLAR,
            Blocks.PURPUR_SLAB,
            Blocks.PURPUR_STAIRS
    );
    static List<Block> extenderBlocks = List.of(
            Blocks.END_ROD
    );
    static int elevatorRange = 8;

    public static void onJump(PlayerEntity player) {
        if (player.getWorld().getRegistryKey() != World.END) {
            moveVertical(player, true);
        }
    }

    public static void onSneak(PlayerEntity player) {
        if (player.getWorld().getRegistryKey() != World.END) {
            moveVertical(player, false);
        }
    }

    /**
     * Moves the player in the elevator
     * @param player instance of player using teleporter
     * @param up boolean value detirming if elevator goes up or down
     */
    private static void moveVertical(PlayerEntity player, boolean up) {
        World world = player.getWorld();
        BlockPos playerBlockPos = player.getBlockPos();
        BlockPos playerBlockUnderPos = playerBlockPos.add(0, -1, 0);
        BlockPos elevatorBlockPos;
        // Check for elevator
        if (isElevatorBlock(world.getBlockState(playerBlockPos).getBlock())) {
            elevatorBlockPos = playerBlockPos;
        } else if (isElevatorBlock( world.getBlockState(playerBlockUnderPos).getBlock())) {
            elevatorBlockPos = playerBlockUnderPos;
        } else return;
        // Loop trough blocks
        int deltaY = up ? 1 : -1;
        for (int i = 0; i < elevatorRange; i++) {
            elevatorBlockPos = elevatorBlockPos.up(deltaY);
            Block _block = world.getBlockState(elevatorBlockPos).getBlock();
            // When it encounters an extender block, it resets the range counter
            if (isElevatorBlock(_block)) {
                if (safeTeleport(player, elevatorBlockPos))
                    return;
                else i--;
            } else if (isExtenderBlock(_block)) {
                i = -1;
            }
        }

    }

    private static boolean isElevatorBlock(Block block) {
        return elevatorBlocks.contains(block);
    }

    private static boolean isExtenderBlock(Block block) {
        return extenderBlocks.contains(block);
    }

    /**
     * Teleports the player to the BlockPos if location is valid
     * @param player instance of player to teleport
     * @param blockPos location to teleport player to
     * @return return if location is valid and player is teleported
     */
    private synchronized static boolean safeTeleport(PlayerEntity player, BlockPos blockPos) {
        World world = player.getWorld();
        Vec3d playerPos = player.getPos();
        BlockState floorBlock = world.getBlockState(blockPos);
        BlockState bottomBlock = world.getBlockState(blockPos.add(0,1,0));
        BlockState topBlock = world.getBlockState(blockPos.add(0,2,0));
        if ( !bottomBlock.shouldSuffocate(world, blockPos) && !topBlock.shouldSuffocate(world, blockPos) ) {
            Vec3d velocity = player.getVelocity();
            double dY = BlockFunctions.isBottomSlab(floorBlock) ? 0.5 : 1;
            player.requestTeleport(
                    (playerPos.getX()),
                    ((double) blockPos.getY() + dY + 0.05),
                    (playerPos.getZ())
            );
            player.setVelocity(velocity);
            player.velocityModified = true;
            world.sendEntityStatus(player, (byte)46);
            return true;
        } else {
            return false;
        }
    }

}
