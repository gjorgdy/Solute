package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.VanillaPlus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Elevator {

    private static int RANGE = 8;

    /**
     * Moves the player in the elevator
     * @param player instance of player using teleporter
     * @param up boolean value detirming if elevator goes up or down
     */
    public static void moveVertical(PlayerEntity player, boolean up) {
        World world = player.getWorld();
        BlockPos blockPos = player.getBlockPos();
        // Get the block under the player
        Block block = world.getBlockState(blockPos.add(0,-1,0)).getBlock();
        // Check if block is an elevator block
        if (contains(block)) {
            int deltaY;
            if (up) {
                deltaY = 1;
            } else {
                blockPos = blockPos.add(0, -1, 0);
                deltaY = -1;
            }
            // Loop trough blocks
            for (int i = 0; i < VanillaPlus.CONFIG.elevatorRange(); i++) {
                blockPos = blockPos.add(0, deltaY, 0);
                block = world.getBlockState(blockPos).getBlock();
                // If it encounters an elevator block
                if (contains(block)) {
                    if (teleport(player, blockPos)) {
                        return;
                    } else {
                        i--;
                    }
                }
            }
        }
    }

    private static boolean contains(Block block) {
        String id = block.toString().split("\\{|\\}")[1];
        return VanillaPlus.CONFIG.elevatorBlocks().contains(id);
    }

    /**
     * Teleports the player to the BlockPos if location is valid
     * @param player instance of player to teleport
     * @param blockPos location to teleport player to
     * @return return if location is valid and player is teleported
     */
    private static boolean teleport(PlayerEntity player, BlockPos blockPos) {
        World world = player.getWorld();
        Vec3d playerPos = player.getPos();
        BlockState firstBlock = world.getBlockState(blockPos.add(0,1,0));
        BlockState secondBlock = world.getBlockState(blockPos.add(0,2,0));
        if ( !firstBlock.shouldSuffocate(world, blockPos) && !secondBlock.shouldSuffocate(world, blockPos) ) {
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
