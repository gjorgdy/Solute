package nl.gjorgdy.solute.mixins.copper_golem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
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
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(MoveItemsTask.class)
public abstract class MoveItemsTaskMixin {

    @Shadow
    protected abstract Box getSearchBoundingBox(PathAwareEntity entity);

    @Shadow
    private static Set<GlobalPos> getVisitedPositions(PathAwareEntity entity) {
        return null;
    }

    @Shadow
    private static Set<GlobalPos> getUnreachablePositions(PathAwareEntity entity) {
        return null;
    }

    @Shadow
    protected abstract int getHorizontalRange(PathAwareEntity entity);

    @Shadow
    @Nullable
    protected abstract MoveItemsTask.Storage getStorageFor(PathAwareEntity entity, World world, BlockEntity blockEntity, Set<GlobalPos> visitedPositions, Set<GlobalPos> unreachablePositions, Box box);

    @Shadow
    @Nullable
    private MoveItemsTask.Storage targetStorage;

    @Inject(method = "findStorage", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"), cancellable = true)
    public void onFindStorage(ServerWorld world, PathAwareEntity entity, CallbackInfoReturnable<Optional<MoveItemsTask.Storage>> cir) {
        if (!Solute.CONFIG.copperGolemModule.enabled) return;
        if (entity instanceof CopperGolemEntity copperGolem) {
            cir.setReturnValue(findStorage(world, copperGolem));
            cir.cancel();
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
    private Optional<BlockEntity> findBlockEntity(ServerWorld world, Stream<ChunkPos> chunkPosStream, Predicate<BlockEntity> predicate, BlockPos pos) {
        return chunkPosStream
                .map(chunkPos -> world.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z))
                .filter(Objects::nonNull)
                .flatMap(worldChunk -> worldChunk.getBlockEntities().values().stream())
                .filter(predicate)
                .min(Comparator.comparingDouble(a -> a.getPos().getSquaredDistance(pos)));
    }

    @Unique
    private Optional<MoveItemsTask.Storage> findStorage(ServerWorld world, CopperGolemEntity entity) {
        Box box = this.getSearchBoundingBox(entity);
        Set<GlobalPos> set = getVisitedPositions(entity);
        Set<GlobalPos> set2 = getUnreachablePositions(entity);
        Stream<ChunkPos> chunkPosStream = ChunkPos.stream(new ChunkPos(entity.getBlockPos()), Math.floorDiv(this.getHorizontalRange(entity), 16) + 1);
        MoveItemsTask.Storage storage = null;

        if (ItemUtils.isMusicDisc(world, entity.getMainHandStack())) {
            Predicate<BlockEntity> predicate = blockEntity -> blockEntity instanceof JukeboxBlockEntity jbe && jbe.isEmpty();
            var jukebox = findBlockEntity(world, chunkPosStream, predicate, entity.getBlockPos());
            if (jukebox.isPresent() && jukebox.get() instanceof JukeboxBlockEntity jukeboxBlockEntity) {
                storage = new MoveItemsTask.Storage(jukeboxBlockEntity.getPos(), jukeboxBlockEntity, jukeboxBlockEntity, jukeboxBlockEntity.getCachedState());
            }
        } else {
            Predicate<BlockEntity> predicate = blockEntity -> blockEntity instanceof ChestBlockEntity;
            var chest = findBlockEntity(world, chunkPosStream, predicate, entity.getBlockPos());
            if (chest.isPresent() && chest.get() instanceof ChestBlockEntity chestBlockEntity) {
                MoveItemsTask.Storage storage2 = this.getStorageFor(entity, world, chestBlockEntity, set, set2, box);
                if (storage2 != null) {
                    storage = storage2;
                }
            }
        }

        return storage == null ? Optional.empty() : Optional.of(storage);
    }

}
