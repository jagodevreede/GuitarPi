package nl.guitar.controlers;

public interface Controller {
    void start();

    void setServoPulse(int boardNumber, short port, float v);

    void waitUntilTimestamp(long timeStamp);

    void waitMilliseconds(long waitTimeMS);
}
