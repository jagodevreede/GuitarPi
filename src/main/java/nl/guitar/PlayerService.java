package nl.guitar;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import nl.guitar.data.ConfigRepository;
import nl.guitar.musicxml.MusicXmlParserListener;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.tuning.DefaultTuning;
import nl.guitar.player.tuning.GuitarTuning;
import nu.xom.ParsingException;
import org.jfugue.integration.MusicXmlParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerService {

    public static final String MUSIC_FOLDER = "music/";
    private GuitarPlayer guitarPlayer;
    private String fileContents;

    private GuitarTuning guitarTuning = new DefaultTuning();

    PlayerService(GuitarPlayer guitarPlayer) {
        this.guitarPlayer = guitarPlayer;
    }

    public List<String> getAvailableMusic() {
        List<String> result = new ArrayList<>();
        for (File file : new File("./music/").listFiles((f) -> f.getName().endsWith(".xml"))) {
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
        try {
            MusicXmlParser parser = new MusicXmlParser();
            MusicXmlParserListener simpleParserListener = new MusicXmlParserListener(guitarPlayer, guitarTuning);
            parser.addParserListener(simpleParserListener);

            parser.parse(fileContents);
            parser.fireAfterParsingFinished();
        } catch (IOException | ParsingException | ParserConfigurationException e) {
            throw new RuntimeException(e);
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
