package nl.gjorgdy.solute.utils;

import net.minecraft.block.Block;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import nl.gjorgdy.solute.interfaces.ConcretePowderBlockInterface;

public class ItemUtils {

    public static boolean isMusicDisc(ServerWorld world, ItemStack stack) {
        return JukeboxSong.getSongEntryFromStack(world.getRegistryManager(), stack).isPresent();
    }

    public static ItemStack hardenConcretePowder(final ItemStack stack) {
        if (Block.getBlockFromItem(stack.getItem()) instanceof ConcretePowderBlockInterface concretePowderBlockInterface) {
            return concretePowderBlockInterface.solute$getHardenedStateDefaultItemStack();
        }
        return stack;
    }

    public static boolean canBecomeMud(final ItemStack stack) {
        return stack.isOf(Items.DIRT) || stack.isOf(Items.COARSE_DIRT)  || stack.isOf(Items.ROOTED_DIRT);
    }

    public static boolean isConcretePowder(Item item) {
        return BlockUtils.isConcretePowder(Block.getBlockFromItem(item));
    }

    public static boolean isRails(ItemStack item) {
        return item.isIn(ItemTags.RAILS);
    }

    public static boolean place(ItemStack item, PlayerEntity player, BlockPos pos) {
        return place(item, player, pos, null);
    }

    public static boolean place(ItemStack item, PlayerEntity player, BlockPos pos, SoundEvent soundEvent) {
        if (item.getItem() instanceof BlockItem blockItem) {
            var result = blockItem.place(new ItemPlacementContext(
                player,
                Hand.MAIN_HAND,
                item,
                BlockHitResult.createMissed(Vec3d.ZERO, Direction.DOWN, pos)
            ));
            boolean placed = result == ActionResult.SUCCESS;
            if (placed && soundEvent != null) {
                PlayerUtils.playDirectSound((ServerPlayerEntity) player, soundEvent, SoundCategory.BLOCKS);
            }
            return placed;
        } else return false;
    }

}
