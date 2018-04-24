package nl.guitar.player.tuning;

import nl.guitar.data.ConfigRepository;

public class DefaultTuning extends GuitarTuning {
    public DefaultTuning() {
        super(new int[] { 0, 0, 0, 0, 0, 0}, new ConfigRepository());
    }
}
