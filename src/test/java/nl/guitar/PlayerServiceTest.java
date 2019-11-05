package nl.guitar;

import nl.guitar.controlers.Controller;
import nl.guitar.controlers.NoOpController;
import nl.guitar.data.ConfigRepository;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class PlayerServiceTest {

    private static final String DTD_FOLDER_TEST = "./musicxml";
    private static final String DEFAULT_MUSIC_FOLDER = "music/";
    private static final String TEST_MUSIC_FOLDER = "src/test/resources/";
    private PlayerService playerService;
    private GuitarPlayer guitarPlayer;

    @BeforeClass
    public void setUp() {
        Controller controller = new NoOpController();
        ConfigRepository configRepository = new ConfigRepository();
        guitarPlayer = new GuitarPlayer(controller, configRepository);
        playerService = new PlayerService(guitarPlayer, configRepository);
    }

    @AfterClass
    public void cleanUp() {
        playerService.clearCache();
    }

    @Test
    public void testTest() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "test.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testLoad() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "ice_winter_rock.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testKrytonicght() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "3_doors_down-kryptonite_acoustic_2.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testbumblebee() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "flight_of_the_bumblebee.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testPirates() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "pirates_of_the_caribbean_full.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testSlowDown() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "slow_down_brother.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testDust() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "Dust-all.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testFredE() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "Test-fred-e.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 5);
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testTwoNotesOneString() {
        playerService.load(TEST_MUSIC_FOLDER,"two_notes_one_string.xml", DTD_FOLDER_TEST);
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
        playerService.load(TEST_MUSIC_FOLDER,"two_notes_same_time.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 2);
        assertEquals(playableGuitarNotes.get(0).getStringNumber(), 0);
        assertEquals(playableGuitarNotes.get(1).getStringNumber(), 1);
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Ignore
    @Test
    public void test_chapman_tracy_fast_car_small() {
        playerService.load(DEFAULT_MUSIC_FOLDER, "chapman_tracy-fast_car_small.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 5);
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testAllAtOnce() {
        playerService.load(TEST_MUSIC_FOLDER,"All at once root.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(playableGuitarNotes.size(), 6);
        for (GuitarNote gn : playableGuitarNotes) {
            assertEquals(gn.getFred(), 0, "Should be on open string: " + gn.toString());
        }
        assertEquals(getErrors(actions), "", "There should be no errors");
    }

    @Test
    public void testWithRest() {
        playerService.load(TEST_MUSIC_FOLDER,"With rest.xml", DTD_FOLDER_TEST);
        playerService.startWithCache(false);
        List<GuitarAction> actions = guitarPlayer.getLastPlayedActions();
        assertEquals(actions.size(), 3);
        List<GuitarNote> playableGuitarNotes = getPlayableNotes(actions);
        assertEquals(getErrors(actions), "", "There should be no errors");
        assertEquals(playableGuitarNotes.size(), 3);
    }

    private List<GuitarNote> getPlayableNotes(List<GuitarAction> actions) {
        return actions.stream().flatMap(a -> a.notesToPlay.stream()).filter(GuitarNote::isHit).collect(Collectors.toList());
    }

    private String getErrors(List<GuitarAction> actions) {
        return actions.stream().filter(n -> n.error != null).map(n -> n.error).collect(Collectors.joining(", "));
    }

}
