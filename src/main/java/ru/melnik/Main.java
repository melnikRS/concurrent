package ru.melnik;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    private static Thread creation;

    public static void main(String[] args) throws InterruptedException {

        creation = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String string = generateText("abc", 100000);
                try {
                    queueA.put(string);
                    queueB.put(string);
                    queueC.put(string);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        creation.start();

        Thread a = new Thread(() -> {
            long i = findMaxChar(queueA, 'a');
            System.out.println("Максимальное количество символов a: " + i);
        });

        Thread b = new Thread(() -> {
            long i = findMaxChar(queueB, 'b');
            System.out.println("Максимальное количество символов b: " + i);
        });

        Thread c = new Thread(() -> {
            long i = findMaxChar(queueC, 'c');
            System.out.println("Максимальное количество символов c: " + i);
        });

        a.start();
        b.start();
        c.start();
        a.join();
        b.join();
        c.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static long findMaxChar(BlockingQueue<String> queue, char c) {
        long max = 0;

        try {
            while (creation.isAlive()) {
                String string = queue.take();
                long count = string.chars()
                        .filter(s -> s == c)
                        .count();
                if (max < count) {
                    max = count;
                }
            }
        } catch (InterruptedException e) {
            return 0;
        }

        return max;
    }

}