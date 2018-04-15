package nl.guitar.controlers;

import java.util.concurrent.TimeUnit;

abstract class RealTimeController implements Controller {
    private long startTime;
    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void waitUntilTimestamp(long timeStamp) {
        final long nextTimeStampInTime = startTime + timeStamp;
        while (nextTimeStampInTime - System.currentTimeMillis() > 250) {
            waitMilliseconds(100);
        }
        while (nextTimeStampInTime - System.currentTimeMillis() > 0) {
            // No op
        }
    }

    public void waitMilliseconds(long waitTimeMS) {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTimeMS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
