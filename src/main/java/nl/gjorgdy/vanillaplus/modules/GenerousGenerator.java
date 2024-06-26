package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class GenerousGenerator {

    static final List<RegistryKey<Biome>> MOSSY_BIOMES = List.of(
            BiomeKeys.JUNGLE,
            BiomeKeys.SPARSE_JUNGLE,
            BiomeKeys.BAMBOO_JUNGLE,
            BiomeKeys.TAIGA,
            BiomeKeys.SNOWY_TAIGA,
            BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA
    );
    static final List<RegistryKey<Biome>> MOUNTAIN_BIOMES = List.of(
            BiomeKeys.FROZEN_PEAKS,
            BiomeKeys.JAGGED_PEAKS,
            BiomeKeys.STONY_PEAKS,
            BiomeKeys.MEADOW,
            BiomeKeys.GROVE,
            BiomeKeys.SNOWY_SLOPES
    );
    static final List<RegistryKey<Biome>> BADLAND_BIOMES = List.of(
            BiomeKeys.BADLANDS,
            BiomeKeys.ERODED_BADLANDS,
            BiomeKeys.WOODED_BADLANDS
    );
    static final int CRYING_OBSIDIAN_CHANCE = 16;

    public static boolean fiftyFifty() {
        return Math.random() > 0.5;
    }

    public static BlockState replaceCobblestone(World world, BlockPos pos, BlockState block) {
        // if vanilla would generate obsidian
        if (block.getBlock() == Blocks.OBSIDIAN) {
            return generateObsidian();
            // if vanilla would generate cobblestone
        } else if (block.getBlock() == Blocks.COBBLESTONE) {
            Block under = world.getBlockState(pos.add(0, -1, 0)).getBlock();
            if (under == Blocks.COBBLESTONE) {
                return Blocks.COBBLESTONE.getDefaultState();
            } else {
                return environmentBasedCobble(world, pos).getDefaultState();
            }
        } else {
            return block;
        }
    }

    public static Block environmentBasedCobble(World world, BlockPos pos) {
        Optional<RegistryKey<Biome>> biomeKeyOpt = world.getBiome(pos).getKey();
        // Check if biome is valid
        if (biomeKeyOpt.isEmpty()) {
            throw new RuntimeException("Could not find biome for position " + pos);
        }
        // biome based generation
        RegistryKey<Biome> biomeKey = biomeKeyOpt.get();
        if (pos.getY() > 60 || (fiftyFifty() && pos.getY() > 50)) {
            return getCobblestoneForBiome(biomeKey);
        }
        // deep
        else if (pos.getY() < 0 || (fiftyFifty() && pos.getY() < 10)) {
            return Blocks.COBBLED_DEEPSLATE;
        }
        // end dimension
        else if (world.getRegistryKey().equals(World.END)) {
            return Blocks.END_STONE;
        // default
        } else {
            return Blocks.COBBLESTONE;
        }
    }

    public static Block getCobblestoneForBiome(RegistryKey<Biome> biomeKey) {
        if (biomeKey == BiomeKeys.DESERT) {
            return Blocks.SANDSTONE;
        } else if (BADLAND_BIOMES.contains(biomeKey)) {
            return Blocks.RED_SANDSTONE;
        } else if (MOSSY_BIOMES.contains(biomeKey)) {
            int r = new Random().nextInt(4);
            return r == 2 ? Blocks.MOSSY_COBBLESTONE : Blocks.COBBLESTONE;
        } else {
            return Blocks.COBBLESTONE;
        }
    }

    public static BlockState replaceStone(World world, BlockPos pos) {
        Block under = world.getBlockState(pos.add(0, -1, 0)).getBlock();
        if (under == Blocks.STONE) {
            return Blocks.STONE.getDefaultState();
        } else {
            return environmentBasedStone(world, pos).getDefaultState();
        }
    }

    public static Block environmentBasedStone(World world, BlockPos pos) {
        Optional<RegistryKey<Biome>> biomeKeyOpt = world.getBiome(pos).getKey();
        // Check if biome is valid
        if (biomeKeyOpt.isEmpty()) {
            return Blocks.STONE;
        }
        int y = pos.getY();
        // Get biome key
        RegistryKey<Biome> biomeKey = biomeKeyOpt.get();
        if (y > 60 || (fiftyFifty() && pos.getY() > 50)) {
            return getStoneForBiome(biomeKey);
        }
        // between
        else if (y > 50) {
            return fiftyFifty() ? getStoneForBiome(biomeKey) : getStoneForUnderground();
        }
        // underground
        else if (y < 50 && y >= 10) {
            return getStoneForUnderground();
        }
        // under between
        else if (y < 10 && y >= 0) {
            return fiftyFifty() ? Blocks.DEEPSLATE : getStoneForUnderground();
        }
        // deep
        else if (y < 0) {
            return Blocks.DEEPSLATE;
        }
        // end dimension
        else if (world.getRegistryKey().equals(World.END)) {
            return Blocks.END_STONE;
            // default
        } else {
            return Blocks.STONE;
        }
    }

    public static Block getStoneForBiome(RegistryKey<Biome> biomeKey) {
        if (biomeKey == BiomeKeys.DESERT) {
            return Blocks.SMOOTH_SANDSTONE;
        } else if (BADLAND_BIOMES.contains(biomeKey)) {
            return Blocks.SMOOTH_RED_SANDSTONE;
        } else if (MOSSY_BIOMES.contains(biomeKey)) {
            int r = new Random().nextInt(4);
            return r == 2 ? Blocks.MOSSY_COBBLESTONE : Blocks.COBBLESTONE;
        } else {
            return Blocks.COBBLESTONE;
        }
    }

    public static Block getStoneForUnderground() {
        return randomBlock(Map.of(
            Blocks.ANDESITE, 20,
            Blocks.DIORITE, 20,
            Blocks.GRANITE, 20,
            Blocks.STONE, 40
        ));
    }

    private static Block randomBlock(Map<Block, Integer> blocks) {
        int r = new Random().nextInt(100);
        int c = 0;
        for (Map.Entry<Block, Integer> entry : blocks.entrySet()) {
            c += entry.getValue();
            if (r < c) {
                return entry.getKey();
            }
        }
        return (Block) blocks.values().toArray()[blocks.size() - 1];
    }

    private static BlockState generateObsidian() {
        Random rand = new Random();
        if (rand.nextInt(CRYING_OBSIDIAN_CHANCE) < 1) {
            return Blocks.CRYING_OBSIDIAN.getDefaultState();
        } else {
            return Blocks.OBSIDIAN.getDefaultState();
        }
    }

}
