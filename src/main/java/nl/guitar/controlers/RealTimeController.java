package nl.guitar.controlers;

import java.util.concurrent.locks.LockSupport;

abstract class RealTimeController implements Controller {
    private long startTime;
    public void start(long offsetTime) {
        startTime = System.currentTimeMillis() + offsetTime;
    }

    public void waitUntilTimestamp(long timeStamp) {
        final long nextTimeStampInTime = startTime + timeStamp;
        LockSupport.parkUntil(nextTimeStampInTime);
    }

    public void waitMilliseconds(long waitTimeMS) {
        LockSupport.parkNanos(waitTimeMS * 1000000);
    }

    public void sleepMilliseconds(long waitTimeMS) {
        LockSupport.parkNanos(waitTimeMS * 1000000);
    }
}
