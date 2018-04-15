package nl.guitar.controlers;

public class NoOpController extends NoWaitController  {
    public void setServoPulse(int boardNumber, short port, float v) {
        // no op
    }
}
