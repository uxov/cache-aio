package xyz.defe.cache.client;

public class TestData {
    public static final String message = "package xyz.defe.cache.client;\n" +
            "\n" +
            "import java.util.*;\n" +
            "import java.util.concurrent.CountDownLatch;\n" +
            "import java.util.concurrent.atomic.AtomicInteger;\n" +
            "\n" +
            "public class PerformanceTest {\n" +
            "    static final int ROUNDS = 10;\n" +
            "    static final int REQUESTS = 4;\n" +
            "    static final int THREADS = 2500;\n" +
            "\n" +
            "    static final String IP = \"127.0.0.1\";\n" +
            "    static final int PORT = 9120;\n" +
            "    static List<List<String>> result = new ArrayList<>();\n" +
            "\n" +
            "    public static void main(String args[]){\n" +
            "        try {\n" +
            "            CacheClient client = new CacheClient(IP, PORT);\n" +
            "\n" +
            "            int round = ROUNDS;\n" +
            "            while (round > 0) {\n" +
            "                run(client, round);\n" +
            "                round--;\n" +
            "            }\n" +
            "\n" +
            "            result.forEach(list -> {\n" +
            "                list.forEach(s -> {\n" +
            "                    System.out.println(s);\n" +
            "                });\n" +
            "                System.out.println();\n" +
            "            });\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    static void run(CacheClient client, int round) throws InterruptedException {\n" +
            "        AtomicInteger requesetCount = new AtomicInteger(0);\n" +
            "        int n = THREADS;\n" +
            "        CountDownLatch countDownLatch = new CountDownLatch(n);\n" +
            "        long a = System.currentTimeMillis();\n" +
            "        for (int i = 0; i < n; i++) {\n" +
            "            new Thread(() -> request(client, requesetCount, countDownLatch)).start();\n" +
            "        }\n" +
            "        countDownLatch.await();\n" +
            "        long b = System.currentTimeMillis();\n" +
            "\n" +
            "        int sum = n*REQUESTS;\n" +
            "        int requestCount = requesetCount.get();\n" +
            "        long seconds = (b-a)/1000;\n" +
            "\n" +
            "        List<String> list = new ArrayList<>();\n" +
            "        list.add(\"Round \" + round);\n" +
            "        list.add(\"request times: \" + sum);\n" +
            "        list.add(\"request successful times: \" + requesetCount);\n" +
            "        list.add(\"request successful percent: \" + ((float)requestCount/sum)*100);\n" +
            "        list.add(\"took seconds: \" + seconds);\n" +
            "        list.add(\"frequency: \" + ((float)requestCount/seconds) + \" times per second\");\n" +
            "        result.add(list);\n" +
            "    }\n" +
            "\n" +
            "    static void request(CacheClient cacheClient, AtomicInteger requestCount, CountDownLatch countDownLatch){\n" +
            "        String key = UUID.randomUUID().toString();\n" +
            "\n" +
            "        int count = cacheClient.put(key, TestData.message);\n" +
            "        if (count > 0) {requestCount.addAndGet(1);}\n" +
            "\n" +
            "        String responseData = (String) cacheClient.get(key);\n" +
            "        if (responseData != null && responseData.length() == TestData.message.length()) {requestCount.addAndGet(1);}\n" +
            "\n" +
            "        Set<String> set = cacheClient.getKeys();\n" +
            "        if (set.contains(key)) {requestCount.addAndGet(1);}\n" +
            "\n" +
            "        boolean boo = cacheClient.delete(key);\n" +
            "        if (boo) {requestCount.addAndGet(1);}\n" +
            "\n" +
            "        countDownLatch.countDown();\n" +
            "    }\n" +
            "\n" +
            "}\n";
}
