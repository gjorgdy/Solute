package nl.gjorgdy.solute.interfaces;

public interface EnderPearlEntityInterface {
    default void solute$setTimeToLive() {}
    default void solute$setTimeToLive(int value) {}
    default int solute$getTimeToLive(boolean decrement) {
        return 0;
    }
}
