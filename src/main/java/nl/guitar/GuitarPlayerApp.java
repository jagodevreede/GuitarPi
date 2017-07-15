package nl.guitar;


import nl.guitar.player.GuitarPlayer;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import io.dropwizard.websockets.WebsocketBundle;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

public class GuitarPlayerApp extends Application<GuitarAppConfiguration> {
	private WebsocketBundle websocketBundle;

	public static void main(String[] args) throws Exception {
		new GuitarPlayerApp().run(args);
	}

	@Override
	public void initialize(Bootstrap<GuitarAppConfiguration> bootstrap) {
		websocketBundle = new WebsocketBundle(StatusWebsocket.class);
		bootstrap.addBundle(websocketBundle);

        bootstrap.addBundle(new AssetsBundle("/webapp", "/", "index.html"));
	}

	@Override
	public void run(GuitarAppConfiguration config, Environment environment) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Class<?> clazz = Class.forName(GuitarPlayer.class.getPackage().getName() + "." + config.getImplementation() + "GuitarPlayer");
		GuitarPlayer guitarPlayer = (GuitarPlayer) clazz.newInstance();

		PlayerService playerService = new PlayerService(guitarPlayer);

		environment.jersey().register(new MusicResource(playerService));

    	((DefaultServerFactory) config.getServerFactory()).setJerseyRootPath("/api/*");
    	
    	environment.healthChecks().register("Ping", new GuitarAppHealthCheck());
    	configureCors(environment);
    }
    
    private void configureCors(Environment environment) {
        Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
      }

}
