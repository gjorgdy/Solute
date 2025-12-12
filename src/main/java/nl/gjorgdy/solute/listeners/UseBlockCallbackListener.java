package nl.gjorgdy.solute.listeners;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Bricks;
import nl.gjorgdy.solute.modules.Ladder;
import nl.gjorgdy.solute.modules.Rails;
import nl.gjorgdy.solute.utils.BlockUtils;
import nl.gjorgdy.solute.utils.ItemUtils;
import nl.gjorgdy.solute.utils.ToolUtils;
import org.jspecify.annotations.NonNull;

import java.util.Random;
import java.util.function.Function;

public class UseBlockCallbackListener implements UseBlockCallback {

    @Override
    public @NonNull ActionResult interact(PlayerEntity player, World world, @NonNull Hand hand, BlockHitResult hitResult) {
        BlockState blockState = world.getBlockState(hitResult.getBlockPos());
        ItemStack itemStack = player.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND && player.getOffHandStack().getItem() instanceof BlockItem) return ActionResult.PASS;
        if (hand == Hand.OFF_HAND && player.getMainHandStack().getItem() instanceof BlockItem) return ActionResult.PASS;

        ActionResult result = ActionResult.PASS;
        if (Solute.CONFIG.ladderModule.enabled) {
            result = interactLadder(player, world, hand, hitResult, itemStack, blockState);
        }
        if (Solute.CONFIG.railsModule.enabled && result == ActionResult.PASS) {
            result = interactRails(player, world, hand, hitResult, itemStack, blockState);
        }
        if (Solute.CONFIG.bricksModule.enabled && result == ActionResult.PASS) {
            result = interactBricks(player, world, hand, hitResult, itemStack, blockState);
        }
        if (Solute.CONFIG.cauldronModule.enabled && result == ActionResult.PASS) {
            result = interactCauldron(player, world, hand, hitResult, itemStack, blockState);
        }
        return result;
    }

    private @NonNull ActionResult interactLadder(PlayerEntity player, World world, @NonNull Hand hand, BlockHitResult hitResult, ItemStack itemStack, BlockState blockState) {
        if (itemStack.isOf(Items.LADDER) && blockState.isOf(Blocks.LADDER)) {
            if (world.isClient()) return clientSwingHand(player, hand, hitResult);
            if (Ladder.lower(player, itemStack, world, hitResult.getBlockPos())) {
                player.swingHand(hand, true);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private @NonNull ActionResult interactRails(PlayerEntity player, World world, @NonNull Hand hand, BlockHitResult hitResult, ItemStack itemStack, BlockState blockState) {
        if (ItemUtils.isRails(itemStack) && blockState.getBlock() instanceof AbstractRailBlock) {
            if (world.isClient()) return ActionResult.PASS;
            if (player instanceof ServerPlayerEntity serverPlayer && Rails.place(serverPlayer, itemStack, blockState, hitResult.getBlockPos())) {
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private @NonNull ActionResult interactBricks(PlayerEntity player, World world, @NonNull Hand hand, BlockHitResult hitResult, ItemStack itemStack, BlockState blockState) {
        // shears on mossy block
        if (itemStack.isOf(Items.SHEARS) && BlockUtils.isMossy(blockState.getBlock())) {
            if (!(world instanceof ServerWorld serverWorld)) return clientSwingHand(player, hand, hitResult);
            if (Bricks.shearMoss(serverWorld, hitResult.getBlockPos(), blockState, player)) {
                if (new Random().nextInt(4) > 1) {
                    Block.dropStack(world, hitResult.getBlockPos().offset(hitResult.getSide()), Items.VINE.getDefaultStack());
                }
                world.playSound(null, hitResult.getBlockPos(), SoundEvents.ENTITY_BOGGED_SHEAR, SoundCategory.BLOCKS);
                itemStack.damage(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // pickaxe on crackable block
        else if (ToolUtils.isPickaxe(itemStack) && BlockUtils.canCrack(blockState.getBlock())) {
            if (!(world instanceof ServerWorld serverWorld)) return clientSwingHand(player, hand, hitResult);
            if (Bricks.usePickaxeOnStone(serverWorld, hitResult.getBlockPos(), blockState)) {
                world.playSound(null, hitResult.getBlockPos(), SoundEvents.BLOCK_DEEPSLATE_BRICKS_BREAK, SoundCategory.BLOCKS);
                itemStack.damage(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // clay ball on cracked block
        else if (itemStack.getItem().equals(Items.CLAY_BALL) && BlockUtils.isCracked(blockState.getBlock())) {
            if (!(world instanceof ServerWorld serverWorld)) return clientSwingHand(player, hand, hitResult);
            if (Bricks.useClayOnStone(serverWorld, hitResult.getBlockPos(), blockState, player)) {
                world.playSound(null, hitResult.getBlockPos(), SoundEvents.BLOCK_DEEPSLATE_BRICKS_PLACE, SoundCategory.BLOCKS);
                itemStack.decrementUnlessCreative(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // vines on block
        else if (!player.isSneaking() && itemStack.isOf(Items.VINE) && BlockUtils.canBeMossy(blockState.getBlock())) {
            if (!(world instanceof ServerWorld serverWorld)) return clientSwingHand(player, hand, hitResult);
            if (Bricks.placeVines(serverWorld, hitResult.getBlockPos(), blockState, player)) {
                itemStack.decrementUnlessCreative(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private @NonNull ActionResult interactCauldron(PlayerEntity player, World world, @NonNull Hand hand, BlockHitResult hitResult, ItemStack itemStack, BlockState blockState) {
        if (!player.isSneaking() && ItemUtils.isConcretePowder(player.getStackInHand(hand).getItem()) && blockState.isOf(Blocks.WATER_CAULDRON)) {
            if (world.isClient()) return clientSwingHand(player, hand, hitResult);
            return cauldronWash(player, hand, world, hitResult.getBlockPos(), ItemUtils::hardenConcretePowder);
        }
        // concrete powder on cauldron
        else if (!player.isSneaking() && ItemUtils.canBecomeMud(player.getStackInHand(hand)) && blockState.isOf(Blocks.WATER_CAULDRON)) {
            if (world.isClient()) return clientSwingHand(player, hand, hitResult);
            return cauldronWash(player, hand, world, hitResult.getBlockPos(), stack -> Items.MUD.getDefaultStack());
        }
        return ActionResult.PASS;
    }

    private ActionResult cauldronWash(PlayerEntity player, Hand hand, World world, BlockPos blockPos, Function<ItemStack, ItemStack> itemStackConsumer) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(stackInHand)) return ActionResult.FAIL;
        ItemStack resultStack = itemStackConsumer.apply(stackInHand);
        world.playSound(null, blockPos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundCategory.BLOCKS);
        player.getItemCooldownManager().set(stackInHand, 8);
        stackInHand.decrementUnlessCreative(1, player);
        player.swingHand(hand, true);
        if (!player.giveItemStack(resultStack)) {
            Block.dropStack(world, blockPos, resultStack);
        }
        return ActionResult.SUCCESS;
    }

    /**
     * Fakes a block interaction on the client by swinging the hand and sending a packet to the server.
     * @param player the client player
     * @param hand the hand used
     * @param hitResult the block hit result
     * @return ActionResult.FAIL to prevent further processing
     */
    private ActionResult clientSwingHand(PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (player instanceof ClientPlayerEntity clientPlayer) {
//            player.swingHand(hand);
            var packet = new PlayerInteractBlockC2SPacket(hand, hitResult, 0);
            clientPlayer.networkHandler.sendPacket(packet);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

}
