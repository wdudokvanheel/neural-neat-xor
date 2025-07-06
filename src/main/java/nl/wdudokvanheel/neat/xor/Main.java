package nl.wdudokvanheel.neat.xor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static final int RUNS = 10000;
    public static final int MAX_GENERATIONS = 300;

    public static void main(String[] args) {
        ExecutorService exec = Executors.newFixedThreadPool(10);
        CompletionService<Integer> svc = new ExecutorCompletionService<>(exec);

        for (int i = 0; i < RUNS; i++) {
            svc.submit(() -> new XorTest().run());
        }

        List<Integer> successfulGenerations = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < RUNS; i++) {
            try {
                int g = svc.take().get();
                if (g >= MAX_GENERATIONS) {
                    System.out.printf("Run %d failed (hit %d generations)%n", i + 1, g);
                } else {
                    successCount++;
                    successfulGenerations.add(g);
                    System.out.printf("Run %d solved in %d generations%n", i + 1, g);
                }
            } catch (InterruptedException | ExecutionException e) {
                exec.shutdownNow();
                throw new RuntimeException(e);
            }
        }

        exec.shutdown();

        if (successCount > 0) {
            Collections.sort(successfulGenerations);

            double sum = 0;
            for (int g : successfulGenerations) {
                sum += g;
            }
            double average = sum / successCount;

            int min = successfulGenerations.get(0);
            int max = successfulGenerations.get(successCount - 1);

            double median;
            if (successCount % 2 == 1) {
                median = successfulGenerations.get(successCount / 2);
            } else {
                median = (successfulGenerations.get(successCount / 2 - 1)
                        + successfulGenerations.get(successCount / 2)) / 2.0;
            }

            double sumSq = 0;
            for (int g : successfulGenerations) {
                sumSq += Math.pow(g - average, 2);
            }
            double stdDev = Math.sqrt(sumSq / successCount);

            double successRate = successCount * 100.0 / RUNS;
            System.out.printf("Statistics for %d/%d (%.2f%%) successful runs:%n", successCount, RUNS, successRate);
            System.out.printf("  Average generations: %.2f%n", average);
            System.out.printf("  Min generations: %d%n", min);
            System.out.printf("  Max generations: %d%n", max);
            System.out.printf("  Median generations: %.2f%n", median);
            System.out.printf("  StdDev generations: %.2f%n", stdDev);
        } else {
            System.out.println("No successful runs to report statistics.");
        }
    }
}
