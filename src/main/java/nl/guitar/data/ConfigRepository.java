package nl.guitar.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.guitar.domain.FredConfig;
import nl.guitar.domain.PlectrumConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigRepository {
    private static final String PLECTRUM_CONF = "plectrum.conf";
    private static final String FRED_CONF = "fred.conf";
    private static final short HOW_MANY_FRED_ON_STRING = 16;
    private static final int[] stringStartNote = new int[] { 28, 33, 38, 43, 47, 52};
    private static final ObjectMapper om = new ObjectMapper();

    public List<PlectrumConfig> loadPlectrumConfig() {
        try {
            File configFile = new File(PLECTRUM_CONF);
            if (!configFile.exists()) {
                List<PlectrumConfig> config = new ArrayList<>();
                addNoteToPlectrumConfig(config, "E");
                addNoteToPlectrumConfig(config, "B");
                addNoteToPlectrumConfig(config, "G");
                addNoteToPlectrumConfig(config, "D");
                addNoteToPlectrumConfig(config, "A");
                addNoteToPlectrumConfig(config, "e");
                savePlectrumConfig(config);
            }
            return om.readValue(configFile, new TypeReference<List<PlectrumConfig>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addNoteToPlectrumConfig(List<PlectrumConfig> config, String note) {
        PlectrumConfig plectrumConfig = new PlectrumConfig();
        plectrumConfig.note = note;
        config.add(plectrumConfig);
    }

    public void savePlectrumConfig(List<PlectrumConfig> config) {
        try {
            File configFile = new File(PLECTRUM_CONF);
            om.writeValue(configFile, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FredConfig> loadFredConfig() {
        try {
            File configFile = new File(FRED_CONF);
            if (!configFile.exists()) {
                List<FredConfig> config = new ArrayList<>();
                for (int startNote : stringStartNote) {
                    for (int i = 0; i < HOW_MANY_FRED_ON_STRING; i++) {
                        addNoteToFredConfig(config, startNote + i);
                    }
                }
                saveFredConfig(config);
            }
            return om.readValue(configFile, new TypeReference<List<FredConfig>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFredConfig(List<FredConfig> config) {
        try {
            File configFile = new File(FRED_CONF);
            om.writeValue(configFile, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addNoteToFredConfig(List<FredConfig> config, int note) {
        FredConfig fredConfig = new FredConfig();
        fredConfig.note = note;
        fredConfig.push = 1.5f;
        if (note % 2 == 0) {
            fredConfig.free = 1.5f;
        }
        config.add(fredConfig);
    }
}
