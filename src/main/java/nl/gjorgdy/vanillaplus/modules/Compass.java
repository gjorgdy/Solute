package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Compass {

    private static final Text spacer = Text.of(" • ");

    private enum modes {
        slime
    }

    public static void use(PlayerEntity player) {
        Map<Vec2f, Boolean> scanned = scan(player, modes.slime);

        MutableText text = Text.empty();
        text.append(spacer);
        for (Map.Entry<Vec2f, Boolean> entry : scanned.entrySet()) {
            if (entry.getValue())
                text.append(
                        Text.of(vec2dir(entry.getKey()))
                ).append(
                        spacer
                );
        }

        player.sendMessage(text, true);

        player.getItemCooldownManager().set(Items.COMPASS, 160);
    }

    public static Map<Vec2f, Boolean> scan(PlayerEntity player, modes mode) {
        Map<Vec2f, Boolean> grid = new HashMap<>();
        World world = player.getWorld();
        ChunkPos chunkPos = player.getChunkPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;

        for (int dX = -1; dX < 2; dX++) {
            for (int dZ = -1; dZ <= 1; dZ++) {
                Chunk chunk = world.getChunk(chunkX + dX, chunkZ + dZ);
                grid.put(new Vec2f(dX, dZ), isChunk(world, chunk, mode));
            }
        }

        return grid;
    }

    public static boolean isChunk(World world, Chunk chunk, modes mode) {
        if (mode == modes.slime) {
            return ChunkRandom.getSlimeRandom(chunk.getPos().x, chunk.getPos().z, ((StructureWorldAccess) world).getSeed(), 987234911L).nextInt(10) == 0;
        }
        return false;
    }

    @Nullable
    public static String vec2dir(Vec2f vec) {
        if (vec.equals(new Vec2f(-1, -1))) {
            return "NW";
        } else if (vec.equals(new Vec2f(0, -1))) {
            return "N";
        } else if (vec.equals(new Vec2f(1, -1))) {
            return "NE";
        } else if (vec.equals(new Vec2f(-1, 0))) {
            return "W";
        } else if (vec.equals(new Vec2f(0, 0))) {
            return "C";
        } else if (vec.equals(new Vec2f(1, 0))) {
            return "E";
        } else if (vec.equals(new Vec2f(-1, 1))) {
            return "SW";
        } else if (vec.equals(new Vec2f(0, 1))) {
            return "S";
        } else if (vec.equals(new Vec2f(1, 1))) {
            return "SE";
        } return null;
    }

}
