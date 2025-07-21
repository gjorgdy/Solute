package nl.gjorgdy.solute.mixins.path;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import nl.gjorgdy.solute.Solute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "RETURN"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.pathModule.enabled) return;
        // early return if module disabled
        if (cir.getReturnValue() == ActionResult.PASS && context.getSide() != Direction.DOWN) {
            World world = context.getWorld();
            BlockPos blockPos = context.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.DIRT_PATH)) {

                PlayerEntity playerEntity = context.getPlayer();
                BlockState dirtState = Blocks.DIRT.getDefaultState();
                Block.pushEntitiesUpBeforeBlockChange(blockState, dirtState, world, blockPos);
                world.setBlockState(blockPos, dirtState, 11);
                world.playSound(null, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 0.75F);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, dirtState));
                PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(world, context.getPlayer(), context.getBlockPos(), blockState, null);


                if (playerEntity != null) {
                    playerEntity.swingHand(context.getHand(), true);
                    context.getStack().damage(1, playerEntity, context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
            }
        }
    }

}
