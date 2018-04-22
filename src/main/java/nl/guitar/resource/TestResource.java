package nl.guitar.resource;

import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.FredConfig;
import nl.guitar.domain.PlectrumConfig;
import nl.guitar.domain.TestState;
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

	private final ConfigRepository repository;
	private final Controller controller;

	public TestResource(ConfigRepository repository, Controller controller) {
		this.repository = repository;
		this.controller = controller;
	}

	@GET
    @Path("fred")// ?string' + stringNumber + '&fred=' + fredNumber + '&pos=' + pos
    public void testFred(@QueryParam("string") int stringNumber, @QueryParam("fred") int fredNumber, @QueryParam("pos") String pos) {
        List<List<FredConfig>> fredConfigs = repository.loadFredConfig();
        FredConfig config = fredConfigs.get(stringNumber).get(fredNumber);
        final float pushValue;
        if ("push".equalsIgnoreCase(pos)) {
            pushValue = config.push;
        } else {
            pushValue = config.free;
        }
        logger.debug("Testing fred {} on string {} to value {}", fredNumber, stringNumber, pushValue);
        controller.setServoPulse(config.address, config.port, pushValue);
    }

	@GET
    @Path("{repeat}")
    public void testAll(@PathParam("repeat") int repeat) throws IOException, InterruptedException {
	    final long sleepTime = 100 - 12; //min 70
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
	public void savePlectrumConfig(List<TestState> testStates) throws IOException, InterruptedException {
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
