package nl.guitar;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import nl.guitar.data.ConfigRepository;
import nl.guitar.musicxml.MusicXmlParserListener;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.RealGuitarPlayer;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.tuning.DefaultTuning;
import nl.guitar.player.tuning.DropDTuning;
import nl.guitar.player.tuning.GuitarTuning;
import nu.xom.ParsingException;
import org.jfugue.integration.MusicXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.guitar.util.FileUtil.toSHA1;

public class PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static final String MUSIC_FOLDER = "music/";
    private GuitarPlayer guitarPlayer;
    private String fileContents;

    private GuitarTuning guitarTuning = new DefaultTuning();

    PlayerService(GuitarPlayer guitarPlayer) {
        this.guitarPlayer = guitarPlayer;
    }

    public List<String> getAvailableMusic() {
        List<String> result = new ArrayList<>();
        for (File file : new File(MUSIC_FOLDER).listFiles((f) -> f.getName().endsWith(".xml"))) {
            result.add(file.getName().substring(0, file.getName().length() - 4));
        }
        Collections.sort(result);
        return result;
    }

    public String getCurrentFileContents() {
        return fileContents;
    }

    public void load(String folder, String fileToPlay) {
        try {
            URL url = new File(folder + fileToPlay).toURI().toURL();
            fileContents = Resources.toString(url, Charsets.UTF_8);
            fileContents = fileContents.replaceAll("http://www.musicxml.org/dtds/partwise.dtd", "musicxml/partwise.dtd");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(String fileToPlay) {
        this.load(MUSIC_FOLDER, fileToPlay);
    }

    public void start() {
        startWithCache(true);
    }

    public void startWithCache(boolean useCache) {
        try {
            String hash = toSHA1(fileContents);
            File cacheFile = new File(MUSIC_FOLDER + "/" + hash + "-" + guitarTuning.getClass().getSimpleName() + ".cache");
            List<GuitarAction> result = readListFromFile(cacheFile);
            if (result == null || !useCache) {
                logger.info("Creating cache file {}", cacheFile.getName());
                MusicXmlParser parser = new MusicXmlParser();
                MusicXmlParserListener simpleParserListener = new MusicXmlParserListener(guitarPlayer, guitarTuning);
                parser.addParserListener(simpleParserListener);

                parser.parse(fileContents);
                parser.fireAfterParsingFinished();

                result = simpleParserListener.guitarActions();
                mapper.writeValue(cacheFile, result);
            }

            guitarPlayer.resetFreds();

            guitarPlayer.playActions(result);
            logger.info("Done playing");

            guitarPlayer.resetFreds();
        } catch (IOException | ParsingException | ParserConfigurationException e) {
            logger.error("Failed to load score", e);
            throw new RuntimeException(e);
        }
    }


    private List<GuitarAction> readListFromFile(File file) {
        if (file.exists()) {
            long startTime = System.currentTimeMillis();
            try (InputStream is = new FileInputStream(file)) {
                List<GuitarAction> result = mapper.readValue(is, new TypeReference<List<GuitarAction>>() {
                });
                logger.info("Reading cache file {} done in {}ms", System.currentTimeMillis() - startTime);
                return result;
            } catch (IOException ioe) {
                logger.error("Failed to load file", ioe);
                return null;
            }
        } else {
            return null;
        }
    }

    public void stop() {
        guitarPlayer.stop();
    }

    public void reset() {
        try {
            guitarPlayer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
