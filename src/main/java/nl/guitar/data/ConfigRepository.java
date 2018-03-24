package nl.guitar.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.guitar.domain.PlectrumConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigRepository {
    private static final String PLECTRUM_CONF = "plectrum.conf";
    private static final ObjectMapper om = new ObjectMapper();

    public List<PlectrumConfig> loadPlectrumConfig() throws IOException {
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
        return om.readValue(configFile, new TypeReference<List<PlectrumConfig>>(){});
    }

    private void addNoteToPlectrumConfig(List<PlectrumConfig> config, String note) {
        PlectrumConfig pc = new PlectrumConfig();
        pc.note = note;
        config.add(pc);
    }

    public void savePlectrumConfig(List<PlectrumConfig> config) throws IOException {
        File configFile = new File(PLECTRUM_CONF);
        om.writeValue(configFile, config);
    }
}
