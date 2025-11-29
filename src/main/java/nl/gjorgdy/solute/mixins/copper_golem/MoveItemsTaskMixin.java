package nl.gjorgdy.solute.mixins.copper_golem;

import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Stream;

@Mixin(MoveItemsTask.class)
public abstract class MoveItemsTaskMixin {

    @Shadow
    protected abstract int getHorizontalRange(PathAwareEntity entity);

    @Shadow
    @Nullable
    private MoveItemsTask.Storage targetStorage;

    @Inject(method = "findStorage", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"), cancellable = true)
    public void onFindStorage(ServerWorld world, PathAwareEntity entity, CallbackInfoReturnable<Optional<MoveItemsTask.Storage>> cir) {
        if (!Solute.CONFIG.copperGolemModule.enabled) return;
        if (entity instanceof CopperGolemEntity copperGolem && ItemUtils.isMusicDisc(world, copperGolem.getMainHandStack())) {
            var optJukebox = this.findJukebox(world, copperGolem);
            if (optJukebox.isPresent()) {
                cir.setReturnValue(optJukebox);
                cir.cancel();
            }
        }
    }

    @Inject(method = "hasValidTargetStorage", at = @At("TAIL"), cancellable = true)
    public void hasValidStorage(World world, PathAwareEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!Solute.CONFIG.copperGolemModule.enabled) return;
        cir.setReturnValue(
            cir.getReturnValue() || (this.targetStorage != null && this.targetStorage.blockEntity() instanceof JukeboxBlockEntity)
        );
    }

    @Inject(method = "placeStack", at = @At(value = "HEAD"), cancellable = true)
    private void onSetStack(PathAwareEntity entity, Inventory inventory, CallbackInfo ci) {
        if (!Solute.CONFIG.copperGolemModule.enabled) return;
        if (inventory instanceof JukeboxBlockEntity jukeboxBlockEntity) {
            ItemStack itemStack = entity.getMainHandStack();
            if (jukeboxBlockEntity.getStack() == null) {
                ItemStack itemStack2 = jukeboxBlockEntity.getStack(0);
                if (itemStack2.isEmpty()) {
                    jukeboxBlockEntity.setStack(0, itemStack.split(1));
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private Optional<MoveItemsTask.Storage> findJukebox(ServerWorld world, CopperGolemEntity entity) {
        Stream<ChunkPos> chunkPosStream = ChunkPos.stream(new ChunkPos(entity.getBlockPos()), Math.floorDiv(this.getHorizontalRange(entity), 16) + 1);
        // Find the nearest empty jukebox
        var jukebox = chunkPosStream
                .map(chunkPos -> world.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z))
                .filter(Objects::nonNull)
                .flatMap(worldChunk -> worldChunk.getBlockEntities().values().stream())
                .filter(blockEntity -> blockEntity instanceof JukeboxBlockEntity jbe && jbe.isEmpty())
                .min(Comparator.comparingDouble(a -> a.getPos().getSquaredDistance(entity.getBlockPos())));
        // If found, return the storage
        if (jukebox.isPresent() && jukebox.get() instanceof JukeboxBlockEntity jukeboxBlockEntity) {
            return Optional.of(
                new MoveItemsTask.Storage(jukeboxBlockEntity.getPos(), jukeboxBlockEntity, jukeboxBlockEntity, jukeboxBlockEntity.getCachedState())
            );
        }
        // Otherwise return empty
        return Optional.empty();
    }

}