package nl.gjorgdy.vanillaplus.interfaces;

public interface EnderPearlEntityInterface {
    default void vanillaPlus$setTimeToLive() {}
    default void vanillaPlus$setTimeToLive(int value) {}
    default int vanillaPlus$getTimeToLive(boolean decrement) {
        return 0;
    }
}
