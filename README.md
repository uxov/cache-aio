# Cache server and client implement in Java AIO.
- Java Asynchronous IO
- Kryo Serialization

## Requirements
JDK 14+, Gradle

## Quick Start

- Download the Jar : [cache-aio-1.0.jar][da1179d3]

  [da1179d3]: https://github.com/uxov/cache-aio/releases/download/1.0/cache-aio-1.0.jar "cache-aio-1.0.jar"

- Server
```java
new Server(9120).start();
```

- Client
```java
CacheClient client = new CacheClient("127.0.0.1", 9120);

String key = "testStr";
String str = "test cache client";

client.put(key,  str);
Set<String> keys = client.getKeys();
String val = (String) client.get(key);
client.delete(key);
```
