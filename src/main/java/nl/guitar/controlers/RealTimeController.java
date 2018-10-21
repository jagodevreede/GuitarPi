package nl.guitar.controlers;

import java.util.concurrent.TimeUnit;

abstract class RealTimeController implements Controller {
    private long startTime;
    public void start(long offsetTime) {
        startTime = System.currentTimeMillis() + offsetTime;
    }

    public void waitUntilTimestamp(long timeStamp) {
        final long nextTimeStampInTime = startTime + timeStamp;
        while (nextTimeStampInTime - System.currentTimeMillis() > 250) {
            sleepMilliseconds(100);
        }
        while (nextTimeStampInTime - System.currentTimeMillis() > 0) {
            // No op
        }
    }

    public void waitMilliseconds(long waitTimeMS) {
        final long nextTimeStampInTime = System.currentTimeMillis() + waitTimeMS;
        while (nextTimeStampInTime - System.currentTimeMillis() > 250) {
            sleepMilliseconds(100);
        }
        while (nextTimeStampInTime - System.currentTimeMillis() > 0) {
            // No op
        }
    }

    private void sleepMilliseconds(long waitTimeMS) {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTimeMS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
