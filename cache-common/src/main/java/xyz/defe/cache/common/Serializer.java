package xyz.defe.cache.common;

public interface Serializer {
    <T> byte[] serialize(T object);

    <T> T deserialize(byte[] data);
}
