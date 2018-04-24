package nl.guitar.player.tuning;

import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.FredConfig;

import java.util.List;

abstract public class GuitarTuning {
    private static final int[] stringStartNote = new int[] { 28, 33, 38, 43, 47, 52};
    private final int[] start = new int[] { 28, 33, 38, 43, 47, 52};
    private final int[] end = new int[] { 28, 33, 38, 43, 47, 52};


    GuitarTuning(int[] offset, ConfigRepository configRepository) {
        for (short i = 0; i < 6; i++) {
            start[i] = stringStartNote[i] + offset[i];
            end[i] = stringStartNote[i] + offset[i];
        }
        List<List<FredConfig>> config = configRepository.loadFredConfig();
        for (short i = 0; i < 6; i++) {
            List<FredConfig> stringConfig = config.get(i);
            for (short j = 0; j < stringConfig.size(); j++) {
                FredConfig fredConfig = stringConfig.get(j);
                if (fredConfig.port > -1) {
                    end[i] = stringStartNote[i] + j + 1 + offset[i];
                } else {
                    break;
                }
            }
        }
    }

    public int getStartNote(int stringIndex) {
        return start[stringIndex];
    }

    public int getEndNote(int stringIndex) {
        return end[stringIndex];
    }
}
