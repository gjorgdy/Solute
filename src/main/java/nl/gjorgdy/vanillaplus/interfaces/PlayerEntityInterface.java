package nl.gjorgdy.vanillaplus.interfaces;

public interface PlayerEntityInterface {

    default void resetCooldown() {}
    default boolean checkCooldown() {
        return false;
    }

}
