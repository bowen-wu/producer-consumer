package com.github.hcsp.multithread;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumer3 {
    private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(1);
    private static final BlockingQueue<Integer> signalQueue = new LinkedBlockingQueue<>();

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
            new CreateRunInstance().run(Type.PRODUCE);
        }
    }

    public static class Consumer extends Thread {
        @Override
        public void run() {
            new CreateRunInstance().run(Type.CONSUME);
        }
    }


    private static class CreateRunInstance implements CreateRun {
        @Override
        public void run(Enum<Type> type) {
            for (int i = 0; i < 10; i++) {
                try {
                    if (type == Type.CONSUME) {
                        consume();
                    } else {
                        produce();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void produce() throws InterruptedException {
            int random = new Random().nextInt();
            System.out.println("Producing " + random);
            queue.put(random);
            signalQueue.take();
        }

        @Override
        public void consume() throws InterruptedException {
            System.out.println("Consuming " + queue.take());
            signalQueue.put(0);
        }
    }

}
