package nl.guitar.resource;

import jdk.nashorn.internal.objects.annotations.Getter;
import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.FredConfig;
import nl.guitar.domain.PlectrumConfig;
import nl.guitar.domain.TestState;
import nl.guitar.player.GuitarPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {
	private static final Logger logger = LoggerFactory.getLogger(TestResource.class);

    private boolean[] isStringUp = new boolean[] { true, true, true, true, true, true };
    private int[] fredPosition = new int[] { -1, -1, -1, -1, -1, -1 };

	private final ConfigRepository repository;
	private final Controller controller;

	public TestResource(ConfigRepository repository, Controller controller) {
		this.repository = repository;
		this.controller = controller;
	}

    @GET
    @Path("fred/reset_all")
    public void testFred() {
        List<List<FredConfig>> fredConfigs = repository.loadFredConfig();
        for (List<FredConfig> row: fredConfigs) {
            for (int i =0; i < row.size(); i+=2) {
                FredConfig config = row.get(i);
                if (config.port > -1) {
                    logger.debug("Reset fred config {} to free", config);
                    controller.setServoPulse(config.address, config.port, config.free);
                }
            }
        }
    }

    @GET
    @Path("hit")// ?string' + stringNumber
    public void hitFred(@QueryParam("string") int stringNumber) throws InterruptedException {
        List<PlectrumConfig> plectrumConfigs = repository.loadPlectrumConfig();
        PlectrumConfig stringConfig = plectrumConfigs.get(stringNumber);
        float heightDistance = stringConfig.hard - stringConfig.soft;
        long fredCount = repository.loadFredConfig().get(stringNumber).stream().filter(f -> f.port > -1).count();
        float height = stringConfig.soft;
        if (fredPosition[stringNumber] > 0) {
            height = stringConfig.soft + (heightDistance / fredCount * fredPosition[stringNumber]);
            logger.info("Hit @height: {}", height);
        }
        controller.setServoPulse(stringConfig.adressHeight, stringConfig.portHeight, stringConfig.free);
        Thread.sleep(75);
        controller.setServoPulse(stringConfig.adressPlectrum, stringConfig.portPlectrum, stringConfig.up);

        controller.setServoPulse(stringConfig.adressHeight, stringConfig.portHeight, height);
        Thread.sleep(75);

        controller.setServoPulse(stringConfig.adressPlectrum, stringConfig.portPlectrum, stringConfig.down);
    }

    @GET
    @Path("all")
    public void all() throws InterruptedException {
        List<PlectrumConfig> plectrumConfigs = repository.loadPlectrumConfig();

        for (int i =0; i < plectrumConfigs.size(); i++) {
            long fredCount = repository.loadFredConfig().get(i).stream().filter(f -> f.port > -1).count();
            for (int f = 0; f <= fredCount; f++) {
                testFred(i, f, "push");
                Thread.sleep(GuitarPlayer.PREPARE_TIME);
                hitFred(i);
                Thread.sleep(350);
            }
        }
    }

    @GET
    @Path("reset")// ?string' + stringNumber
    public void resetString(@QueryParam("string") int stringNumber) throws InterruptedException {
        List<List<FredConfig>> fredConfigs = repository.loadFredConfig();
        List<FredConfig> configs = fredConfigs.get(stringNumber);
        for (int i = 0; i < configs.size(); i += 2) {
            FredConfig config = configs.get(i);
            if (config.port > -1) {
                controller.setServoPulse(config.address, config.port, config.push);
                Thread.sleep(GuitarPlayer.PREPARE_TIME);
                controller.setServoPulse(config.address, config.port, config.free);
            }
        }
        fredPosition[stringNumber] = 0;
    }

	@GET
    @Path("fred")// ?string' + stringNumber + '&fred=' + fredNumber + '&pos=' + pos
    public void testFred(@QueryParam("string") int stringNumber, @QueryParam("fred") int fredNumber, @QueryParam("pos") String pos) {
        List<List<FredConfig>> fredConfigs = repository.loadFredConfig();
        if (fredPosition[stringNumber] != fredNumber && fredPosition[stringNumber] != -1) {
            FredConfig config = fredConfigs.get(stringNumber).get(fredPosition[stringNumber]);
            controller.setServoPulse(config.address, config.port, config.free);
        }
        FredConfig config = fredConfigs.get(stringNumber).get(fredNumber);
        final float pushValue;
        if ("push".equalsIgnoreCase(pos)) {
            fredPosition[stringNumber] = fredNumber;
            pushValue = config.push;
        } else {
            fredPosition[stringNumber] = -1;
            pushValue = config.free;
        }
        logger.debug("Testing fred {} on string {} to value {}", fredNumber, stringNumber, pushValue);
        controller.setServoPulse(config.address, config.port, pushValue);
    }

	@GET
    @Path("{repeat}")
    public void testAll(@PathParam("repeat") int repeat) throws IOException, InterruptedException {
	    final long sleepTime = 300; //min 70
        List<PlectrumConfig> plectrumConfigs = repository.loadPlectrumConfig();
        for (int i = 0; i < plectrumConfigs.size(); i++) {
            PlectrumConfig config = plectrumConfigs.get(i);
            float heightValue = config.hard;
            controller.setServoPulse(config.adressHeight, config.portHeight, heightValue);
        }
        for (int x = 0; x < repeat; x++) {
            final long startTime = System.currentTimeMillis();
            for (int i = 0; i < plectrumConfigs.size(); i++) {
                PlectrumConfig config = plectrumConfigs.get(i);
                float positionValue = config.up;
                if (x % 2 == 0) {
                    positionValue = config.down;
                }
                controller.setServoPulse(config.adressPlectrum, config.portPlectrum, positionValue);
            }
            System.out.println("bs    " + (System.currentTimeMillis() - startTime));
            Thread.sleep(sleepTime);
            System.out.println("total " + (System.currentTimeMillis() - startTime));
        }
    }

    @GET
    @Path("/over/{repeat}")
    public String testOnlyToDownAll(@PathParam("repeat") int repeat) throws IOException, InterruptedException {
        //final long sleepTime = 100 - 12; //min 70
        List<PlectrumConfig> plectrumConfigs = repository.loadPlectrumConfig();
        for (int x = 0; x < repeat; x++) {
            final long startTime = System.currentTimeMillis();
            for (int i = 0; i < plectrumConfigs.size(); i++) {
                PlectrumConfig config = plectrumConfigs.get(i);
                controller.setServoPulse(config.adressPlectrum, config.portPlectrum, config.down);
            }
            System.out.println("hit   " + (System.currentTimeMillis() - startTime));
            Thread.sleep(40);
            for (int i = 0; i < plectrumConfigs.size(); i++) {
                PlectrumConfig config = plectrumConfigs.get(i);
                controller.setServoPulse(config.adressHeight, config.portHeight, config.free);
                Thread.sleep(30);
                controller.setServoPulse(config.adressPlectrum, config.portPlectrum, config.up);
                Thread.sleep(30);
                controller.setServoPulse(config.adressHeight, config.portHeight, config.hard);
                Thread.sleep(30);
            }
            //System.out.println("over  " + (System.currentTimeMillis() - startTime));
            //Thread.sleep(40);
            System.out.println("total " + (System.currentTimeMillis() - startTime));
        }
        return "OK";
    }

	@POST
	public void testPlectrumConfig(List<TestState> testStates) throws IOException, InterruptedException {
        List<PlectrumConfig> plectrumConfigs = repository.loadPlectrumConfig();
        for (int i = 0; i < testStates.size(); i++) {
		    TestState ts = testStates.get(i);
            PlectrumConfig config = plectrumConfigs.get(i);
            float heightValue = 0;
            switch (ts.height) {
                case FREE:
                    heightValue = config.free;
                    break;
                case SOFT:
                    heightValue = config.soft;
                    break;
                case HARD:
                    heightValue = config.hard;
                    break;
                case STOP:
                    heightValue = config.stop;
                    break;
            }
			controller.setServoPulse(config.adressHeight, config.portHeight, heightValue);
            Thread.sleep(25);
            float positionValue = 0;
            switch (ts.position) {
                case UP:
                    positionValue = config.up;
                    break;
                case DOWN:
                    positionValue = config.down;
                    break;
            }
            controller.setServoPulse(config.adressPlectrum, config.portPlectrum, positionValue);
		}
	}

}
