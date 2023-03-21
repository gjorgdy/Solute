package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import nl.gjorgdy.vanillaplus.VanillaPlus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class CustomGenerator {

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

    public static BlockState replaceCobblestone(World world, BlockPos pos, BlockState block) {
        Block under = world.getBlockState(pos.add(0, -1, 0)).getBlock();
        if (block.getBlock() == Blocks.OBSIDIAN) {
            return generateObsidian();
        } else if (block.getBlock() == Blocks.COBBLESTONE) {
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
            return Blocks.COBBLESTONE;
        }
        RegistryKey<Biome> biomeKey = biomeKeyOpt.get();
        if (pos.getY() < -56) {
            return randomBlock(Map.of(
                    Blocks.NETHERRACK, 50,
                    Blocks.NETHER_QUARTZ_ORE, 10,
                    Blocks.BLACKSTONE, 40));
        }
        // If under y=0, generate cobbled deepslate
        else if (pos.getY() < 0 | biomeKey.equals(BiomeKeys.DEEP_DARK)) {
            return Blocks.COBBLED_DEEPSLATE;
        }
        // Generate mossy cobblestone in jungle biomes
        else if (MOSSY_BIOMES.contains(biomeKey)) {
            return randomBlock(Map.of(
                    Blocks.COBBLESTONE, 75,
                    Blocks.MOSSY_COBBLESTONE, 25));
        }
        // Generate endstone in the end dimension
        else if (world.getRegistryKey().equals(World.END)) {
            return Blocks.END_STONE;
        // Generate cobble any other time
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
        RegistryKey<Biome> biomeKey = biomeKeyOpt.get();
        if (pos.getY() < -56) {
            return randomBlock(Map.of(
                    Blocks.SOUL_SOIL, 50,
                    Blocks.BASALT, 45,
                    Blocks.SMOOTH_QUARTZ, 5)
            );
        } else
        // If under y=0, generate cobbled deepslate
        if (pos.getY() < 0 | biomeKey == BiomeKeys.DEEP_DARK) {
            return Blocks.DEEPSLATE;
        } else
        // Generate sandstone in the desert
        if (biomeKey == BiomeKeys.DESERT) {
            return Blocks.SANDSTONE;
        } else
        // Generate red sandstone in badlands biomes
        if (BADLAND_BIOMES.contains(biomeKey)) {
            return Blocks.RED_SANDSTONE;
        } else
        // Generate andesite, diorite, or granite on mountain peaks
        if (MOUNTAIN_BIOMES.contains(biomeKey)) {
            return randomBlock(Map.of(
                    Blocks.ANDESITE, 25,
                    Blocks.GRANITE, 25,
                    Blocks.DIORITE, 25,
                    Blocks.STONE, 25)
            );
        } else
        // Generate endstone in the end dimension
        if (world.getRegistryKey() == World.END) {
            return Blocks.END_STONE;
        } else {
            return Blocks.STONE;
        }
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
        return (Block) blocks.values().toArray()[blocks.size()-1];
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
