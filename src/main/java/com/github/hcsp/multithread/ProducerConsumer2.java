package com.github.hcsp.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer2 {
    private static final Lock lock = new ReentrantLock();
    private static final Condition isConsumed = lock.newCondition();
    private static final Condition isProduced = lock.newCondition();

    private static List<Integer> basket = new ArrayList<>(1);
    private static int index = 0;

    public static void main(String[] args) throws InterruptedException {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        producer.start();
        consumer.start();

        producer.join();
        producer.join();
    }

    public static class Producer extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                while (index < 10) {
                    if (basket.isEmpty()) {
                        int random = new Random().nextInt();
                        basket.add(random);
                        System.out.println("Producing " + basket.get(0));
                        isProduced.signal();
                    } else {
                        isConsumed.wait();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }

        }
    }

    public static class Consumer extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                while (index < 10) {
                    if (basket.isEmpty()) {
                        isProduced.wait();
                    } else {
                        System.out.println("Consuming " + basket.get(0));
                        basket.remove(0);
                        index++;
                        isConsumed.signal();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}
