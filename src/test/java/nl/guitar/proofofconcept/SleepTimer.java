package nl.guitar.proofofconcept;

import nl.guitar.controlers.ConsoleController;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SleepTimer {
    private static ConsoleController c = new ConsoleController();
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 16; i++) {
            executor.execute(() -> {
                while (true) {

                }
            });
        }

        long[] samples = new long[100];
        int pauseInMillis = 100;

        for (int i = 0; i < samples.length; i++) {
            long firstTime = System.nanoTime();
            c.sleepMilliseconds(pauseInMillis);
            long timeForNano = System.nanoTime() - firstTime;
            samples[i] = timeForNano;
        }

        System.out.printf("Time for Sleep %.0f max %d\n", Arrays.stream(samples).average().getAsDouble(), Arrays.stream(samples).max().getAsLong());
    }
}
