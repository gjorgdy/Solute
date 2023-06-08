package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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

    /**
     * Moves the player in the elevator
     * @param player instance of player using teleporter
     * @param up boolean value detirming if elevator goes up or down
     */
    public static void moveVertical(PlayerEntity player, boolean up) {
        World world = player.getWorld();
        BlockPos blockPos = player.getBlockPos();
        // Get the block under the player
        blockPos = blockPos.add(0, -1, 0);
        Block block = world.getBlockState(blockPos).getBlock();
        // Check if block is an elevator block
        if (!isElevatorBlock(block)) {
            return;
        }
        int deltaY;
        if (up) {
            deltaY = 1;
        } else {
            deltaY = -1;
        }
        // Loop trough blocks
        for (int i = 0; i < elevatorRange; i++) {
            blockPos = blockPos.add(0, deltaY, 0);
            block = world.getBlockState(blockPos).getBlock();
            // When it encounters an extender block, it resets the range counter
            if (isExtenderBlock(block)) {
                i = -1;
            // When it encounters an elevator block
            } else if (isElevatorBlock(block)) {
                if (teleport((ServerPlayerEntity) player, blockPos)) {
                    return;
                } else {
                    i--;
                }
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
    private static boolean teleport(ServerPlayerEntity player, BlockPos blockPos) {
        World world = player.getWorld();
        Vec3d playerPos = player.getPos();
        BlockState bottomBlock = world.getBlockState(blockPos.add(0,1,0));
        BlockState topBlock = world.getBlockState(blockPos.add(0,2,0));
        if ( !bottomBlock.shouldSuffocate(world, blockPos) && !topBlock.shouldSuffocate(world, blockPos) ) {
            Vec3d velocity = player.getVelocity();
            player.teleport(
                    (playerPos.getX()),
                    (blockPos.getY() + 1.05),
                    (playerPos.getZ()),
                    true
            );
            player.setVelocity(velocity);
            player.velocityModified = true;
            return true;
        } else {
            return false;
        }
    }

}
