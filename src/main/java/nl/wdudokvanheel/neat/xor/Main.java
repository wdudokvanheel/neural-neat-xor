package nl.wdudokvanheel.neat.xor;

import java.util.concurrent.*;

public class Main {
    public static final int RUNS = 100;
    public static final int MAX_GENERATIONS = 300;

    public static void main(String[] args) {
        ExecutorService exec = Executors.newFixedThreadPool(10);
        CompletionService<Integer> svc = new ExecutorCompletionService<>(exec);

        for (int i = 0; i < RUNS; i++) {
            svc.submit(() -> new XorTest().run());
        }

        int totalGenerations = 0;
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < RUNS; i++) {
            try {
                int g = svc.take().get();

                if (g >= MAX_GENERATIONS) {
                    failCount++;
                    System.out.printf("Run %d failed (hit %d generations)%n", i + 1, g);
                } else {
                    successCount++;
                    totalGenerations += g;
                    System.out.printf("Run %d solved in %d generations%n", i + 1, g);
                }

            } catch (InterruptedException | ExecutionException e) {
                exec.shutdownNow();
                throw new RuntimeException(e);
            }
        }

        exec.shutdown();

        if (successCount > 0) {
            double average = totalGenerations / (double) successCount;
            System.out.printf("Average generations across %d successful runs: %.2f%n", successCount, average);
        } else {
            System.out.println("No successful runs to average.");
        }

        System.out.println("Failed runs: " + failCount);
    }
}
