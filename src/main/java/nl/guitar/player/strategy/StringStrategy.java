package nl.guitar.player.strategy;

import java.util.List;
import java.util.Optional;

public interface StringStrategy {
    Optional<Short> getBestString(List<Short> possibleStringNumber, int[] stringsTaken, byte noteValue);
}
