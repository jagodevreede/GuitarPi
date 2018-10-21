package nl.guitar.controlers;

public interface Controller {
    void start(long offsetTime);

    void setServoPulse(int boardNumber, short port, float v);

    void waitUntilTimestamp(long timeStamp);

    void waitMilliseconds(long waitTimeMS);
}
