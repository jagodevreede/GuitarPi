package nl.guitar;

import com.codahale.metrics.health.HealthCheck;

public class GuitarAppHealthCheck extends HealthCheck {
	
	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}

}
