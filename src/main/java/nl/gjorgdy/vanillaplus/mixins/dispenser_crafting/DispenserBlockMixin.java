package nl.gjorgdy.vanillaplus.mixins.dispenser_crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Shadow @Final public static DirectionProperty FACING;

    @Inject(method = "dispense", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/entity/DispenserBlockEntity;chooseNonEmptySlot(Lnet/minecraft/util/math/random/Random;)I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void onDispense(ServerWorld world, BlockPos pos, CallbackInfo ci, BlockPointerImpl blockPointerImpl, DispenserBlockEntity dispenserBlockEntity) {
        Direction facing = world.getBlockState(pos).get(DispenserBlock.FACING);
        if (world.getBlockState(pos.offset(facing)).getBlock() == Blocks.CRAFTING_TABLE) {
            // Cancel default dispenser behavior
            ci.cancel();
            // Create inventory
            CraftingInventory craftingInventory = new CraftingInventory(getScreenHandler(), 3, 3);
            for (int i = 0; i < 9; i++) {
                // If item not a filler, add to crafting inventory
                ItemStack slot = dispenserBlockEntity.getStack(i);
                if (!slot.isOf(Items.BARRIER)) {
                    craftingInventory.setStack(i, slot);
                }
            }
            // Get recipe
            Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
            if (optional.isPresent()) {
                // Get result of recipe
                ItemStack result = optional.get().craft(craftingInventory, world.getRegistryManager());
                // Decrement all items by 1
                for (int i = 0; i < 9; i++) {
                    craftingInventory.getStack(i).decrement(1);
                }
                // Check if crafting table has available hopper under it
                BlockEntity blockUnderCT = world.getBlockEntity(pos.offset(facing).down());
                if (blockUnderCT instanceof HopperBlockEntity) {
                    // Drop item in Crafting Table
                    Block.dropStack(world, pos.offset(facing), result);
                } else {
                    // Drop item above Crafting Table
                    Block.dropStack(world, pos.offset(facing).up(), result);
                }
                // Execute dispenser dispense item sound
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 1);
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 1.2f);
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 0.8f);
            } else {
                // Execute dispenser fail sound
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS);
            }

        }
    }

    private static ScreenHandler getScreenHandler() {
        return new ScreenHandler(ScreenHandlerType.CRAFTING, 0) {
            @Override
            public ItemStack quickMove(PlayerEntity player, int slot) {
                return null;
            }

            @Override
            public boolean canUse(PlayerEntity player) {
                return false;
            }
        };
    }

}
