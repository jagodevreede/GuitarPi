package nl.guitar.resource;

import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.PlectrumConfig;
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

@Path("/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource {
	private static final Logger logger = LoggerFactory.getLogger(ConfigResource.class);

	private final ConfigRepository repository;

	public ConfigResource(ConfigRepository repository) {
		this.repository = repository;
	}

    @GET
	@Path("plectrum")
	public List<PlectrumConfig> loadPlectrumConfig() throws IOException {
		return repository.loadPlectrumConfig();
	}

	@POST
	@Path("plectrum")
	public void savePlectrumConfig(List<PlectrumConfig> config) throws IOException {
		repository.savePlectrumConfig(config);
	}

}
