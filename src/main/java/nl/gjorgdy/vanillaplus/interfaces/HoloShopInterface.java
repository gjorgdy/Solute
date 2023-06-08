package nl.gjorgdy.vanillaplus.interfaces;

import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import nl.gjorgdy.vanillaplus.objects.HoloShop;

public interface HoloShopInterface {

    default boolean vp$createShop(Direction direction, Block block) {
        return false;
    }
    default boolean vp$removeShop() {
        return false;
    }
    default HoloShop vp$getShop() {
        return null;
    }

}
