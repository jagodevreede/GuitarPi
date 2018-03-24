package nl.guitar.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlectrumConfig {
    public String note;
    public short adressHeight;
    public short portHeight;
    public float free;
    public float soft;
    public float hard;
    public float stop;
    public short adressPlectrum;
    public short portPlectrum;
    public float up;
    public float down;
}
