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
public interface PlayerSneakCallback {
    Event<PlayerSneakCallback> EVENT = EventFactory.createArrayBacked(PlayerSneakCallback.class,
            (listeners) -> (player) -> {
                for (PlayerSneakCallback listener : listeners) {
                    ActionResult result = listener.interactJump(player);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult interactJump(PlayerEntity player);
}
