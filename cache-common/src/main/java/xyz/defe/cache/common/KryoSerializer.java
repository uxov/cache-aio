package xyz.defe.cache.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo can serialize object without implements serializable
 */
public class KryoSerializer implements Serializer {
    private final ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T object) {
        if (object == null) {return null;}
        Kryo kryo = threadLocal.get();
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        Output output = new Output(bo);
        kryo.writeClassAndObject(output, object);
        output.close();
        return bo.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data) {
        if (data.length == 0) {return null;}
        Kryo kryo = threadLocal.get();
        ByteArrayInputStream bi = new ByteArrayInputStream(data);
        Input input = new Input(bi);
        T t = (T) kryo.readClassAndObject(input);
        input.close();
        return t;
    }
}
