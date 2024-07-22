package nl.gjorgdy.solute.modules;

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

public class Cobblestone {

    static final List<RegistryKey<Biome>> MOSSY_BIOMES = List.of(
            BiomeKeys.JUNGLE,
            BiomeKeys.SPARSE_JUNGLE,
            BiomeKeys.BAMBOO_JUNGLE,
            BiomeKeys.TAIGA,
            BiomeKeys.SNOWY_TAIGA,
            BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA
    );
    static final List<RegistryKey<Biome>> BADLAND_BIOMES = List.of(
            BiomeKeys.BADLANDS,
            BiomeKeys.ERODED_BADLANDS,
            BiomeKeys.WOODED_BADLANDS
    );
    static final int CRYING_OBSIDIAN_POSSIBLE = 16;

    public static boolean fiftyFifty(World world) {
        return chance(world, 1, 2);
    }

    public static boolean chance(World world, int favourable, int possible) {
        return random(world, possible) > favourable;
    }

    public static int random(World world, int possible) {
        return world.random.nextInt(possible + 1);
    }

    public static BlockState replaceCobblestone(World world, BlockPos pos, BlockState block) {
        // if vanilla would generate obsidian
        if (block.getBlock() == Blocks.OBSIDIAN) {
            return generateObsidian(world);
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
        if (pos.getY() > 60 || (fiftyFifty(world) && pos.getY() > 50)) {
            return getCobblestoneForBiome(world, biomeKey);
        }
        // deep
        else if (pos.getY() < 0 || (fiftyFifty(world) && pos.getY() < 10)) {
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

    public static Block getCobblestoneForBiome(World world, RegistryKey<Biome> biomeKey) {
        if (biomeKey == BiomeKeys.DESERT) {
            return Blocks.SANDSTONE;
        } else if (BADLAND_BIOMES.contains(biomeKey)) {
            return Blocks.RED_SANDSTONE;
        } else if (MOSSY_BIOMES.contains(biomeKey)) {
            return chance(world, 1, 3) ? Blocks.MOSSY_COBBLESTONE : Blocks.COBBLESTONE;
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
        if (y > 60 || (fiftyFifty(world) && pos.getY() > 50)) {
            return getStoneForBiome(world, biomeKey);
        }
        // between
        else if (y > 50) {
            return fiftyFifty(world) ? getStoneForBiome(world, biomeKey) : getStoneForUnderground(world);
        }
        // underground
        else if (y < 50 && y >= 10) {
            return getStoneForUnderground(world);
        }
        // under between
        else if (y < 10 && y >= 0) {
            return fiftyFifty(world) ? Blocks.DEEPSLATE : getStoneForUnderground(world);
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

    public static Block getStoneForBiome(World world, RegistryKey<Biome> biomeKey) {
        if (biomeKey == BiomeKeys.DESERT) {
            return Blocks.SMOOTH_SANDSTONE;
        } else if (BADLAND_BIOMES.contains(biomeKey)) {
            return Blocks.SMOOTH_RED_SANDSTONE;
        } else if (MOSSY_BIOMES.contains(biomeKey)) {
            return chance(world, 1, 3) ? Blocks.MOSSY_COBBLESTONE : Blocks.COBBLESTONE;
        } else {
            return Blocks.COBBLESTONE;
        }
    }

    public static Block getStoneForUnderground(World world) {
        return randomBlock(world, Map.of(
            Blocks.ANDESITE, 2,
            Blocks.DIORITE, 2,
            Blocks.GRANITE, 2,
            Blocks.STONE, 4
        ));
    }

    private static Block randomBlock(World world, Map<Block, Integer> blocks) {
        int r = random(world, 10);
        int c = 0;
        for (Map.Entry<Block, Integer> entry : blocks.entrySet()) {
            c += entry.getValue();
            if (r < c) {
                return entry.getKey();
            }
        }
        return (Block) blocks.values().toArray()[blocks.size() - 1];
    }

    private static BlockState generateObsidian(World world) {
        if (chance(world, 1, CRYING_OBSIDIAN_POSSIBLE)) {
            return Blocks.CRYING_OBSIDIAN.getDefaultState();
        } else {
            return Blocks.OBSIDIAN.getDefaultState();
        }
    }

}
