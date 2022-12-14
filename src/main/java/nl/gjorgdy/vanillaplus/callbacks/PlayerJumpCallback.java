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
public interface PlayerJumpCallback {
    Event<PlayerJumpCallback> EVENT = EventFactory.createArrayBacked(PlayerJumpCallback.class,
            (listeners) -> (player) -> {
                for (PlayerJumpCallback listener : listeners) {
                    ActionResult result = listener.interactJump(player);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult interactJump(PlayerEntity player);
}
