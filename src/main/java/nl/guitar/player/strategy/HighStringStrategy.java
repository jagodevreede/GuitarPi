package nl.guitar.player.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HighStringStrategy implements StringStrategy {

    @Override
    public Optional<Short> getBestString(List<Short> possibleStringNumber, int[] stringsTaken, byte noteValue) {
        return possibleStringNumber.stream().filter(s -> stringsTaken[s] == -1 || stringsTaken[s] == noteValue).sorted().max(Comparator.naturalOrder());
    }
}
