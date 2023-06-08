package nl.gjorgdy.vanillaplus.interfaces;

public interface EnderPearlEntityInterface {

    default void setTimeToLive() {}
    default void setTimeToLive(int value) {}

    default int getTimeToLive(boolean decrement) {
        return 0;
    }

}
