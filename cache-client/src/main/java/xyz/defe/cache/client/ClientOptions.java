package xyz.defe.cache.client;

public class ClientOptions {
    private String ip = "127.0.0.1";
    private int port = 9120;
    private int requestTimeOut = 15;    //seconds
    private int initPooSize = 100;
    private int maxPoolSize = 1000;
    private int increaseCount = 100;
    final int poolCheckPeriod = 60000 * 15;   //seconds

    public String getIp() {
        return ip;
    }

    public ClientOptions setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ClientOptions setPort(int port) {
        this.port = port;
        return this;
    }

    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    public ClientOptions setRequestTimeOut(int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
        return this;
    }

    public int getInitPooSize() {
        return initPooSize;
    }

    public ClientOptions setInitPooSize(int initPooSize) {
        this.initPooSize = initPooSize;
        return this;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public ClientOptions setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public int getIncreaseCount() {
        return increaseCount;
    }

    public ClientOptions setIncreaseCount(int increaseCount) {
        this.increaseCount = increaseCount;
        return this;
    }
}
