package nl.gjorgdy.vanillaplus.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface FluidBlockCallback {
    Event<FluidBlockCallback> EVENT = EventFactory.createArrayBacked(FluidBlockCallback.class,
            (listeners) -> (world, blockPos) -> {
                for (FluidBlockCallback listener : listeners) {
                    ActionResult result = listener.interact(world, blockPos);
                }
                return ActionResult.SUCCESS;
            });
    ActionResult interact(World world, BlockPos blockPos);
}
