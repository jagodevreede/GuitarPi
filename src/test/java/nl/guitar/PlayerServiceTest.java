package nl.guitar;

import nl.guitar.controlers.Controller;
import nl.guitar.controlers.NoOpController;
import nl.guitar.data.ConfigRepository;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.RealGuitarPlayer;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import org.testng.annotations.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;

public class PlayerServiceTest {

    private PlayerService playerService;
    private GuitarPlayer guitarPlayer;

    @BeforeClass
    public void setUp() {
        Controller controller = new NoOpController();
        guitarPlayer = new RealGuitarPlayer(controller, new ConfigRepository());
        playerService = new PlayerService(guitarPlayer);
    }

    @Test
    public void testLoad() {
        playerService.load("ice_winter_rock.xml");
        playerService.start();
    }

    @Test
    public void testFredE() {
        playerService.load("Test-fred-e.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 5);
    }

    @Test
    public void testTwoNotesOneString() {
        playerService.load("src/test/resources/","two_notes_one_string.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 2);
        assertEquals(playableGuitarNotes.get(0).getStringNumber(), 0);
        assertEquals(playableGuitarNotes.get(1).getStringNumber(), 1);
    }

    private List<GuitarNote> getPlayableNotes(List<GuitarAction> actions) {
        return actions.stream().flatMap(a -> a.notesToPlay.stream()).filter(GuitarNote::isHit).collect(Collectors.toList());
    }

}
