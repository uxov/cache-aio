package xyz.defe.cache.client;

import xyz.defe.cache.common.Message;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTest {
    static final int ROUNDS = 10;
    static final int REQUESTS = 4;
    static final int THREADS = 2500;

    static final String IP = "127.0.0.1";
    static final int PORT = 9120;
    static List<List<String>> result = new ArrayList<>();

    public static void main(String args[]){
        try {
            CacheClient client = new CacheClient(IP, PORT);

            int round = ROUNDS;
            while (round > 0) {
                run(client, round);
                round--;
            }

            result.forEach(list -> {
                list.forEach(s -> {
                    System.out.println(s);
                });
                System.out.println();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void run(CacheClient client, int round) throws InterruptedException {
        AtomicInteger requesetCount = new AtomicInteger(0);
        int n = THREADS;
        CountDownLatch countDownLatch = new CountDownLatch(n);
        long a = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            try {
                new Thread(() -> request(client, requesetCount, countDownLatch)).start();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        countDownLatch.await();
        long b = System.currentTimeMillis();

        int sum = n*REQUESTS;
        int requestCount = requesetCount.get();
        long seconds = (b-a)/1000;

        List<String> list = new ArrayList<>();
        list.add("Round " + round);
        list.add("request times: " + sum);
        list.add("request successful times: " + requesetCount);
        list.add("request successful percent: " + ((float)requestCount/sum)*100);
        list.add("took seconds: " + seconds);
        list.add("frequency: " + ((float)requestCount/seconds) + " times per second");
        result.add(list);
    }

    static void request(CacheClient cacheClient, AtomicInteger requestCount, CountDownLatch countDownLatch){
        Message testObject = new Message();
        testObject.setValue(TestData.message);
        String key = UUID.randomUUID().toString();

        int count = cacheClient.put(key, testObject);
        if (count > 0) {requestCount.addAndGet(1);}

        Message msg = (Message) cacheClient.get(key);
        if (msg != null && msg.getValue().equals(testObject.getValue())) {requestCount.addAndGet(1);}

        Set<String> set = cacheClient.getKeys();
        if (set.contains(key)) {requestCount.addAndGet(1);}

        boolean boo = cacheClient.delete(key);
        if (boo) {requestCount.addAndGet(1);}

        countDownLatch.countDown();
    }

}
