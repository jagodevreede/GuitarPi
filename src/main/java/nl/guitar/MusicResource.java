package nl.guitar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/music")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MusicResource {
	private static final Logger logger = LoggerFactory.getLogger(MusicResource.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private PlayerService playerService;

	public MusicResource(PlayerService playerService){
		this.playerService = playerService;
	}

	@GET
	@Path("list")
	public List<String> getAvailableMusic() {
		return playerService.getAvailableMusic();
	}

	@POST
    @Path("start")
	public Response start(@Context HttpServletRequest request) {
	    executorService.submit(playerService::start);
		return Response.ok().build();
	}

	@POST
    @Path("stop")
    public Response stop(@Context HttpServletRequest request) {
	    playerService.stop();
        executorService.shutdown();
        return Response.ok().build();
    }

	@GET
    @Path("load/{path}")
    @Produces(MediaType.APPLICATION_XML)
	public String start(@PathParam("path") String path) {
	    playerService.load(path + ".xml");
		return playerService.getCurrentFileContents();
	}

/*	@POST
	public Response createPing(@Context HttpServletRequest request) {
		try {
			return Response.status(Response.Status.ACCEPTED).entity(null).build();
		} catch (Exception e) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(null).build();
		}
	}*/

}
