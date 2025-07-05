package nl.gjorgdy.solute.listeners;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
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

import java.util.Random;
import java.util.function.Function;

public class UseBlockCallbackListener implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockState blockState = world.getBlockState(hitResult.getBlockPos());
        ItemStack itemStack = player.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND && player.getOffHandStack().getItem() instanceof BlockItem) return ActionResult.PASS;
        if (hand == Hand.OFF_HAND && player.getMainHandStack().getItem() instanceof BlockItem) return ActionResult.PASS;

        // use ladder
        if (itemStack.isOf(Items.LADDER) && blockState.isOf(Blocks.LADDER)) {
            if (Ladder.lower(player, itemStack, world, hitResult.getBlockPos())) {
                player.swingHand(hand, true);
            }
            return ActionResult.SUCCESS;
        }
        // rails on rails
        else if (ItemUtils.isRails(itemStack) && blockState.getBlock() instanceof AbstractRailBlock) {
            if (Rails.place((ServerPlayerEntity) player, itemStack, blockState, hitResult.getBlockPos())) {
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // shears on mossy block
        else if (player.getStackInHand(hand).isOf(Items.SHEARS) && BlockUtils.isMossy(blockState.getBlock())) {
            if (Bricks.shearMoss((ServerWorld) world, hitResult.getBlockPos(), blockState, player)) {
                if (new Random().nextInt(4) > 1) {
                    Block.dropStack(world, hitResult.getBlockPos().offset(hitResult.getSide()), Items.VINE.getDefaultStack());
                }
                world.playSound(null, hitResult.getBlockPos(), SoundEvents.ENTITY_BOGGED_SHEAR, SoundCategory.BLOCKS);
                player.getStackInHand(hand).damage(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // pickaxe on crackable block
        else if (ToolUtils.isPickaxe(player.getStackInHand(hand)) && BlockUtils.canCrack(blockState.getBlock())) {
            if (Bricks.usePickaxeOnStone((ServerWorld) world, hitResult.getBlockPos(), blockState)) {
                world.playSound(null, hitResult.getBlockPos(), SoundEvents.BLOCK_DEEPSLATE_BRICKS_BREAK, SoundCategory.BLOCKS);
                player.getStackInHand(hand).damage(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // clay ball on cracked block
        else if (player.getStackInHand(hand).getItem().equals(Items.CLAY_BALL) && BlockUtils.isCracked(blockState.getBlock())) {
            if (Bricks.useClayOnStone((ServerWorld) world, hitResult.getBlockPos(), blockState, player)) {
                world.playSound(null, hitResult.getBlockPos(), SoundEvents.BLOCK_DEEPSLATE_BRICKS_PLACE, SoundCategory.BLOCKS);
                player.getStackInHand(hand).decrementUnlessCreative(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // vines on block
        else if (!player.isSneaking() && player.getStackInHand(hand).isOf(Items.VINE) && BlockUtils.canBeMossy(blockState.getBlock())) {
            if (Bricks.placeVines((ServerWorld) world, hitResult.getBlockPos(), blockState, player)) {
                player.getStackInHand(hand).decrementUnlessCreative(1, player);
                player.swingHand(hand, true);
                return ActionResult.SUCCESS;
            }
        }
        // concrete powder on cauldron
        else if (!player.isSneaking() && ItemUtils.isConcretePowder(player.getStackInHand(hand).getItem()) && blockState.isOf(Blocks.WATER_CAULDRON)) {
            return cauldronWash(player, hand, world, hitResult.getBlockPos(), ItemUtils::hardenConcretePowder);
        }
        // concrete powder on cauldron
        else if (!player.isSneaking() && ItemUtils.canBecomeMud(player.getStackInHand(hand)) && blockState.isOf(Blocks.WATER_CAULDRON)) {
            return cauldronWash(player, hand, world, hitResult.getBlockPos(), stack -> Items.MUD.getDefaultStack());
        }

        return ActionResult.PASS;
    }

    private ActionResult cauldronWash(PlayerEntity player, Hand hand, World world, BlockPos blockPos, Function<ItemStack, ItemStack> itemStackConsumer) {
        // early return if module disabled
        if (!Solute.CONFIG.cauldronModule.enabled) return ActionResult.PASS;
        // early return if module disabled
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

}
