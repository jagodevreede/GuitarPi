package nl.guitar.player;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class GuitarPlayerTest {

    @Test
    public void test() {
        int n = 6;

        double high = 0.8;
        double low = 0.7;

        double ans = Math.pow(low/high, n);

        // an = a0 * b^n  // a0 = 0.8
        assertEquals(ans,0.978,0.001);
    }
}
