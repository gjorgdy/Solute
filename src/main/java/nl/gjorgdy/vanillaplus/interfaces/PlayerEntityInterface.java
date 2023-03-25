package nl.gjorgdy.vanillaplus.interfaces;

public interface PlayerEntityInterface {

    default void vanillaPlus$resetCooldown() {}
    default boolean vanillaPlus$checkCooldown() {
        return false;
    }

}
