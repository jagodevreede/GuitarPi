package nl.guitar;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.websockets.WebsocketBundle;
import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.resource.ConfigResource;
import nl.guitar.resource.MusicResource;
import nl.guitar.resource.TestResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

public class GuitarPlayerApp extends Application<GuitarAppConfiguration> {
	private WebsocketBundle websocketBundle;

	public static void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			LoggerFactory.getLogger(t.getClass()).error("Uncaught Exception in thread '" + t.getName() + "'", e);
		});
		new GuitarPlayerApp().run(args);
	}

	@Override
	public void initialize(Bootstrap<GuitarAppConfiguration> bootstrap) {
		websocketBundle = new WebsocketBundle(StatusWebsocket.class);
		bootstrap.addBundle(websocketBundle);

        bootstrap.addBundle(new AssetsBundle("/webapp", "/play", "index.html", "play"));
        bootstrap.addBundle(new AssetsBundle("/ui/assets", "/assets", "index.html", "assets"));
        bootstrap.addBundle(new AssetsBundle("/ui", "/ui", "index.html", "ui"));
	}

	@Override
	public void run(GuitarAppConfiguration config, Environment environment) throws Exception {
		ConfigRepository configRepository = new ConfigRepository();

		Controller controller = loadControllerFromConfig(config);

		PlayerService playerService = getPlayerService(controller);

		environment.jersey().register(new MusicResource(playerService));
		environment.jersey().register(new ConfigResource(configRepository));
		environment.jersey().register(new TestResource(configRepository, controller));

    	((DefaultServerFactory) config.getServerFactory()).setJerseyRootPath("/api/*");
    	
    	environment.healthChecks().register("Ping", new GuitarAppHealthCheck());
    	configureCors(environment);
    }

	private PlayerService getPlayerService(Controller controller) {
		GuitarPlayer guitarPlayer = new GuitarPlayer(controller, new ConfigRepository());

		return new PlayerService(guitarPlayer);
	}

	private Controller loadControllerFromConfig(GuitarAppConfiguration config) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<?> controllerClazz = Class.forName(Controller.class.getPackage().getName() + "." + config.getController());
		Constructor<?> controllerConstructor = controllerClazz.getConstructor();
		return (Controller) controllerConstructor.newInstance();
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
