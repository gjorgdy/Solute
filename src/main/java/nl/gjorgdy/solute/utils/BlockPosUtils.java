package nl.gjorgdy.solute.utils;

import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class BlockPosUtils {

    /**
     * Execute a consumer through all block positions directly next to a blocks position
     *
     * @param blockPos the block to loop around
     * @param consumer the function to be executed for every neighbour
     */
    public static void forDirectNeighbours(BlockPos blockPos, Consumer<BlockPos> consumer) {
        consumer.accept(blockPos.up());
        consumer.accept(blockPos.down());
        consumer.accept(blockPos.north());
        consumer.accept(blockPos.east());
        consumer.accept(blockPos.south());
        consumer.accept(blockPos.west());
    }

    /**
     * Execute a consumer through all block positions around a blocks position
     *
     * @param blockPos the block to loop around
     * @param consumer the function to be executed for every neighbour
     */
    public static void forNeighbours(BlockPos blockPos, Consumer<BlockPos> consumer) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    consumer.accept(blockPos.add(x, y, z));
                }
            }
        }
    }

}
