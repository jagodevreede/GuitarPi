package nl.guitar;

import nl.guitar.controlers.Controller;
import nl.guitar.controlers.NoOpController;
import nl.guitar.data.ConfigRepository;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class PlayerServiceTest {

    private PlayerService playerService;
    private GuitarPlayer guitarPlayer;

    @BeforeClass
    public void setUp() {
        Controller controller = new NoOpController();
        guitarPlayer = new GuitarPlayer(controller, new ConfigRepository());
        playerService = new PlayerService(guitarPlayer);
    }

    @Test
    public void testTest() {
        playerService.load("test.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testLoad() {
        playerService.load("ice_winter_rock.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testKrytonicght() {
        playerService.load("3_doors_down-kryptonite_acoustic_2.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testbumblebee() {
        playerService.load("flight_of_the_bumblebee.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testPirates() {
        playerService.load("pirates_of_the_caribbean_full.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testSlowDown() {
        playerService.load("slow_down_brother.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testDust() {
        playerService.load("Dust-all.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testFredE() {
        playerService.load("Test-fred-e.xml");
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 5);
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testTwoNotesOneString() {
        playerService.load("src/test/resources/","two_notes_one_string.xml");
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 2);
        assertEquals(playableGuitarNotes.get(0).getStringNumber(), 0);
        assertEquals(playableGuitarNotes.get(1).getStringNumber(), 1);
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testTwoNotesSameTime() {
        playerService.load("src/test/resources/","two_notes_same_time.xml");
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 2);
        assertEquals(playableGuitarNotes.get(0).getStringNumber(), 0);
        assertEquals(playableGuitarNotes.get(1).getStringNumber(), 1);
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void test_chapman_tracy_fast_car_small() {
        playerService.load("chapman_tracy-fast_car_small.xml");
        playerService.start();
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 5);
        assertEquals(getErrors(actions), "", "There should be no errors");
    }


    private List<GuitarNote> getPlayableNotes(List<GuitarAction> actions) {
        return actions.stream().flatMap(a -> a.notesToPlay.stream()).filter(GuitarNote::isHit).collect(Collectors.toList());
    }

    private String getErrors(List<GuitarAction> actions) {
        return actions.stream().filter(n -> n.error != null).map(n -> n.error).collect(Collectors.joining(", "));
    }

}
