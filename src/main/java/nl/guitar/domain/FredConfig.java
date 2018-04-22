package nl.guitar.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FredConfig {
    public int note;
    public short address;
    public short port;
    public float free;
    public float push;

    @Override
    public String toString() {
        return "FredConfig{" +
                "address=" + address +
                ", port=" + port +
                ", free=" + free +
                ", push=" + push +
                '}';
    }
}
