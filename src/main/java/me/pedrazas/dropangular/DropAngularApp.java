package me.pedrazas.dropangular;


import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

public class DropAngularApp extends Application<DropAngularConfiguration> {
	public static void main(String[] args) throws Exception {
		new DropAngularApp().run(args);
	}

	@Override
	public void initialize(Bootstrap<DropAngularConfiguration> bootstrap) {
        // with this, we're adding the webapp folder (located in our resources)
        // as root of our webapp.
        bootstrap.addBundle(new AssetsBundle("/webapp", "/", "index.html"));
	}

	@Override
	public void run(DropAngularConfiguration config, Environment environment) throws ClassNotFoundException {
		environment.jersey().register(new PingResource());
		// If you're using a version previous to v.0.8.x
		// you need this line to avoid the error:
		// Multiple servlets map to path: /*:
//    	environment.jersey().setUrlPattern("/api/*");
		// there's an option to configure your rootpath in your configuration.yml file
    	((DefaultServerFactory) config.getServerFactory()).setJerseyRootPath("/api/*");
    	
    	environment.healthChecks().register("Ping", new PingHealthCheck());
	    	// CORS
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
