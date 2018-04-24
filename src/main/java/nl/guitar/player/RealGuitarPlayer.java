package nl.guitar.player;

import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.FredConfig;
import nl.guitar.domain.PlectrumConfig;
import nl.guitar.player.object.GuitarNote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RealGuitarPlayer extends GuitarPlayer {

    private static final Logger logger = LoggerFactory.getLogger(RealGuitarPlayer.class);

    private final List<PlectrumConfig> plectrumConfig;
    private final List<List<FredConfig>> fredConfig;

    private boolean[] isStringUp = new boolean[] { true, true, true, true, true, true };
    private int[] fredPressed = new int[] { 0, 0, 0, 0, 0, 0 };

    public RealGuitarPlayer(Controller controller, ConfigRepository configRepository) {
        super(controller, configRepository);
        logger.info("Starting real guitar player");
        plectrumConfig = configRepository.loadPlectrumConfig();
        fredConfig = configRepository.loadFredConfig();
        try {
            close();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException  e) {
            logger.error("Failed to load real guitar player", e);
            throw new RuntimeException(e);
        }
        logger.info("Real guitar player ready!");
    }

    @Override
    void prepareString(GuitarNote gn) {
        if (gn.getStringNumber() == -1 && gn.getNoteValue() > 0) {
            logger.info("Not a correct string for {}", gn);
            return;
        }
        int stringNumber = gn.getStringNumber();
        int fredNumber = gn.getFred();

        if (fredPressed[stringNumber] != fredNumber) {
            if (fredPressed[stringNumber] > 0) {
                resetFred(stringNumber);
            }
            fredPressed[stringNumber] = fredNumber;
            if (fredNumber > 0) {
                logger.debug("Press fred " + fredNumber);
                FredConfig fc = fredConfig.get(stringNumber).get(fredNumber - 1);
                controller.setServoPulse(fc.address, fc.port, fc.push);
            }
        }
    }

    void playString(GuitarNote gn) {
        if (gn.getStringNumber() == -1) {
            return;
        }
        if (gn.isHit()) {
            float toPos;
            PlectrumConfig stringConfig = plectrumConfig.get(gn.getStringNumber());
            if (isStringUp[gn.getStringNumber()]) {
                toPos = stringConfig.down;
                isStringUp[gn.getStringNumber()] = false;
            } else {
                toPos = stringConfig.up;
                isStringUp[gn.getStringNumber()] = true;
            }
            controller.setServoPulse(stringConfig.portPlectrum, stringConfig.adressPlectrum, toPos);
        }
    }

    @Override
    public void resetFreds() {
        logger.debug("Resetting freds");
        for (int i = 0; i < 6; i++) {
            resetFred(i);
        }
    }

    private void resetFred(int stringNumber) {
        for (int i = 0; i < fredConfig.get(stringNumber).size(); i += 2) {
            FredConfig fc = fredConfig.get(stringNumber).get(i);
            if (fc.port > -1) {
                controller.setServoPulse(fc.address, fc.port, fc.free);
                logger.debug("reset fred servo {}", fc);
            }
        }
    }

    @Override
    public void close() {
        logger.debug("Reset servo positions");
        for (int i = 0; i < 6; i++) {
            resetFred(i);
            PlectrumConfig config = plectrumConfig.get(i);
            controller.setServoPulse(config.adressPlectrum, config.portPlectrum, config.up);
            controller.setServoPulse(config.adressHeight, config.portHeight, config.soft);
        }
    }
}
