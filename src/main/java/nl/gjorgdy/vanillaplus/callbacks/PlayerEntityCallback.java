package nl.gjorgdy.vanillaplus.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * Called when a player jumps.
 * Upon return:
 * - SUCCESS cancels further processing and continues with normal jump
 * - PASS - not used
 * - FAIL - not used
 */
public interface PlayerEntityCallback {

    Event<PlayerEntityCallback> JUMP_EVENT = EventFactory.createArrayBacked(PlayerEntityCallback.class,
            (listeners) -> (player) -> {
                for (PlayerEntityCallback listener : listeners) {
                    ActionResult result = listener.interactJump(player);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult interactJump(PlayerEntity player);

    Event<PlayerEntityCallback> SNEAK_EVENT = EventFactory.createArrayBacked(PlayerEntityCallback.class,
            (listeners) -> (player) -> {
                for (PlayerEntityCallback listener : listeners) {
                    ActionResult result = listener.interactSneak(player);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult interactSneak(PlayerEntity player);

}
