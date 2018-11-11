package nl.guitar.player;

import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.FredConfig;
import nl.guitar.domain.PlectrumConfig;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RealGuitarPlayer extends GuitarPlayer {

    private static final Logger logger = LoggerFactory.getLogger(RealGuitarPlayer.class);

    private List<PlectrumConfig> plectrumConfig;
    private List<List<FredConfig>> fredConfig;

    private boolean[] isStringUp = new boolean[] { true, true, true, true, true, true };
    private int[] fredPressed = new int[] { 0, 0, 0, 0, 0, 0 };
    private long[] fredCount = new long[] { 0, 0, 0, 0, 0, 0 };

    public RealGuitarPlayer(Controller controller, ConfigRepository configRepository) {
        super(controller, configRepository);
        try {
            close();
            controller.waitMilliseconds(1000);
        } catch (InterruptedException  e) {
            logger.error("Failed to load real guitar player", e);
            throw new RuntimeException(e);
        }
        logger.info("Guitar player ready!");
        this.resetFreds();
    }

    @Override
    void prepareStringPressFredAndMovePlectrumToHigh(GuitarNote gn) {
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
                logger.trace("Press fred " + fredNumber);
                FredConfig fc = fredConfig.get(stringNumber).get(fredNumber - 1);
                controller.setServoPulse(fc.address, fc.port, fc.push);
            }
        }
        PlectrumConfig stringConfig = plectrumConfig.get(stringNumber);
        controller.setServoPulse(stringConfig.adressHeight, stringConfig.portHeight, stringConfig.free);
    }

    @Override
    void prepareStringMovePlectrumToUp(GuitarNote gn) {
        if (gn.getStringNumber() == -1 || !gn.isHit()) {
            return;
        }
        int stringNumber = gn.getStringNumber();
        PlectrumConfig stringConfig = plectrumConfig.get(stringNumber);

        controller.setServoPulse(stringConfig.adressPlectrum, stringConfig.portPlectrum, stringConfig.up);
        isStringUp[stringNumber] = true;
    }

    @Override
    void prepareStringMovePlectrumToHitPosition(GuitarNote gn) {
        int stringNumber = gn.getStringNumber();
        PlectrumConfig stringConfig = plectrumConfig.get(stringNumber);

        float heightDistance = stringConfig.hard - stringConfig.soft;
        float height = stringConfig.soft;
        if (fredPressed[stringNumber] > 1) {
            height = stringConfig.soft + (heightDistance / fredCount[stringNumber] * (fredPressed[stringNumber] -1));
        }
        controller.setServoPulse(stringConfig.adressHeight, stringConfig.portHeight, height);
    }

    void playString(GuitarNote gn) {
        if (gn.getStringNumber() == -1 || !gn.isHit()) {
            return;
        }
        float toPos;
        PlectrumConfig stringConfig = plectrumConfig.get(gn.getStringNumber());
        if (isStringUp[gn.getStringNumber()]) {
            toPos = stringConfig.down;
            isStringUp[gn.getStringNumber()] = false;
        } else {
            toPos = stringConfig.up;
            isStringUp[gn.getStringNumber()] = true;
        }
        controller.setServoPulse(stringConfig.adressPlectrum, stringConfig.portPlectrum, toPos);
    }

    @Override
    public void playActions(List<GuitarAction> guitarActions) {
        reloadConfig();
        super.playActions(guitarActions);
    }

    @Override
    public void resetFreds() {
        logger.debug("Resetting freds");
        for (int i = 0; i < 6; i++) {
            resetFred(i);
            controller.waitMilliseconds(350);
        }
        controller.waitMilliseconds(100);
        logger.debug("Resetting freds ready");
    }

    private void resetFred(int stringNumber) {
        for (int i = 0; i < fredConfig.get(stringNumber).size(); i += 2) {
            FredConfig fc = fredConfig.get(stringNumber).get(i);
            if (fc.port > -1) {
                controller.setServoPulse(fc.address, fc.port, fc.free);
                logger.trace("reset fred servo {}", fc);
            }
        }
    }

    private void reloadConfig() {
        plectrumConfig = configRepository.loadPlectrumConfig();
        fredConfig = configRepository.loadFredConfig();
    }

    @Override
    public void close() throws InterruptedException {
        logger.debug("Reset servo positions");
        reloadConfig();
        for (int i = 0; i < 6; i++) {
            fredCount[i] = fredConfig.get(i).stream().filter(f -> f.port > -1).count();
            List<FredConfig> configs = fredConfig.get(i);
            for (int j = 0; j < configs.size(); j += 2) {
                FredConfig fredConfig = configs.get(j);
                if (fredConfig.port > -1) {
                    controller.setServoPulse(fredConfig.address, fredConfig.port, fredConfig.push);
                    Thread.sleep(GuitarPlayer.PREPARE_TIME);
                    controller.setServoPulse(fredConfig.address, fredConfig.port, fredConfig.free);
                }
            }
            PlectrumConfig config = plectrumConfig.get(i);
            controller.setServoPulse(config.adressHeight, config.portHeight, config.free);
            Thread.sleep(PREPARE_TIME);
            controller.setServoPulse(config.adressPlectrum, config.portPlectrum, config.up);
        }
    }
}
