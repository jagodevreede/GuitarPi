package me.pedrazas.dropangular.om;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ping {
	
	@JsonProperty("id")
    private long id;
	

	public Ping(String remoteIp) {
		super();
		this.remoteIp = remoteIp;
		this.timeStamp = System.currentTimeMillis();
	}



	@JsonProperty("remote_ip")
	private String remoteIp;
	
	public Ping() {
		super();
		this.timeStamp = System.currentTimeMillis();
	}

	// This attribute has a name, a different json name
	// and a different column name.
	// Cannot get more diverse than this :)
	@JsonProperty("timestamp")
	private Long timeStamp;
	
	public String getTimestampAsString(){
		DateTime jodaTime = new DateTime(this.timeStamp,DateTimeZone.forTimeZone(TimeZone.getTimeZone("EU/London")));
        DateTimeFormatter parser = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
        return parser.print(jodaTime);
	}

	
}
