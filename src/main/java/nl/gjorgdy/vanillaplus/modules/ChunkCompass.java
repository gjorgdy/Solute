package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChunkCompass {

    private static final Text BULLET = Text.of(" • ");

    private static final List<Vec2f> DELTAS = List.of(
            new Vec2f(0, 0),
            new Vec2f(0, -1),
            new Vec2f(1, -1),
            new Vec2f(1, 0),
            new Vec2f(1, 1),
            new Vec2f(0, 1),
            new Vec2f(-1, 1),
            new Vec2f(-1, 0),
            new Vec2f(-1, -1)
    );

    private enum modes {
        slime
    }

    public static void use(PlayerEntity player) {
        List<Vec2f> scanned = scan(player, modes.slime);
        if (scanned.size() == 0) {
            MutableText text = Text.empty().append(Text.of("No Slime Chunks"));
            player.sendMessage(text.setStyle(Style.EMPTY.withColor(16724530)), true);
            return;
        }
        MutableText text = Text.empty();
        text.append(Text.of("Slime Chunks"));
        scanned.forEach(vec -> {
                text.append(BULLET)
                        .append(
                                Text.of(vec2dir(vec))
                        );
            }
        );
        player.sendMessage(text.setStyle(Style.EMPTY.withColor(5308240)), true);
        player.getItemCooldownManager().set(Items.COMPASS, 80);
    }

    public static List<Vec2f> scan(PlayerEntity player, modes mode) {
        List<Vec2f> chunks = new ArrayList<>();
        World world = player.getWorld();
        ChunkPos chunkPos = player.getChunkPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;

        DELTAS.forEach(vec -> {
            Chunk chunk = world.getChunk( chunkX + (int) vec.x, chunkZ + (int) vec.y);
            if (isChunk(world, chunk, mode))
                chunks.add(vec);
        });

        return chunks;
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
