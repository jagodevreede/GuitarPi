package nl.guitar;

import nl.guitar.controlers.Controller;
import nl.guitar.controlers.NoOpController;
import nl.guitar.data.ConfigRepository;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.RealGuitarPlayer;
import org.testng.annotations.*;

public class PlayerServiceTest {

    private PlayerService playerService;

    @BeforeClass
    public void setUp() {
        Controller controller = new NoOpController();
        GuitarPlayer guitarPlayer = new RealGuitarPlayer(controller, new ConfigRepository());
        playerService = new PlayerService(guitarPlayer);
    }

    @Test
    public void testLoad() {
        playerService.load("ice_winter_rock.xml");
        playerService.start();
    }

}
