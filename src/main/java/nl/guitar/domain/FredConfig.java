package nl.guitar.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FredConfig {
    public int note;
    public short adress;
    public short port;
    public float free;
    public float push;
}
