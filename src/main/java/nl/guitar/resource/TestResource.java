package nl.guitar.resource;

import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.PlectrumConfig;
import nl.guitar.domain.TestState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
