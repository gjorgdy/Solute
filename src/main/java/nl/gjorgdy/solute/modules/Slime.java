package nl.gjorgdy.solute.modules;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Slime {

    public static void use(ServerWorld world, PlayerEntity player, Hand hand) {
        if (isSlimeChunk(world, world.getChunk(player.getBlockPos()))) {
            world.spawnParticles(
                    ParticleTypes.ITEM_SLIME,
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    100,
                    0.5,
                    0.5,
                    0.5,
                    1
            );
            world.playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.BLOCK_SLIME_BLOCK_PLACE,
                    SoundCategory.BLOCKS,
                    2,
                    2
            );
        } else {
            world.playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
                    SoundCategory.BLOCKS,
                    1,
                    0.25f
            );
        }
        player.swingHand(hand, true);
        player.getItemCooldownManager().set(player.getStackInHand(hand), 40);
    }

    public static boolean isSlimeChunk(World world, Chunk chunk) {
        return ChunkRandom.getSlimeRandom(chunk.getPos().x, chunk.getPos().z, ((StructureWorldAccess) world).getSeed(), 987234911L).nextInt(10) == 0;
    }

}
