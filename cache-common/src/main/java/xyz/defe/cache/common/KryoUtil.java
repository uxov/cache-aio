package xyz.defe.cache.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo can serialize object without implements serializable
 */
public class KryoUtil {

    private static Kryo getKryoInstance(){
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    }

    public static <T> byte[] serialize(T object) {
        if (object == null) {return null;}
        Kryo kryo = getKryoInstance();
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        Output output = new Output(bo);
        kryo.writeClassAndObject(output, object);
        output.close();
        return bo.toByteArray();
    }

    public static <T> T deserialize(byte[] data) {
        if (data.length == 0) {return null;}
        Kryo kryo = getKryoInstance();
        ByteArrayInputStream bi = new ByteArrayInputStream(data);
        Input input = new Input(bi);
        T t = (T) kryo.readClassAndObject(input);
        input.close();
        return t;
    }
    
}
