package nl.guitar.player.strategy;

import nl.guitar.player.tuning.GuitarTuning;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComplexStringStrategy implements StringStrategy {

    private final GuitarTuning guitarTuning;

    public ComplexStringStrategy(GuitarTuning guitarTuning) {
        this.guitarTuning = guitarTuning;
    }

    @Override
    public Optional<Short> getBestString(List<Short> possibleStringNumber, int[] stringsTaken, byte noteValue) {
        List<Short> possibleStrings = possibleStringNumber.stream().filter(s -> stringsTaken[s] == -1 || stringsTaken[s] == noteValue).sorted().collect(Collectors.toList());
        Short best = null;
        int bestDistance = Integer.MIN_VALUE;
        for (Short s : possibleStrings) {
            int distance = guitarTuning.getStartNote(s.intValue()) - noteValue;
            if (distance > bestDistance) {
                bestDistance = distance;
                best = s;
            }
        }
        return Optional.ofNullable(best);
    }
}
