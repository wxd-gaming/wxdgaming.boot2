package wxdgaming.boot2.core.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 容器对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-23 13:49
 **/
public class kryoPool {

    public static final kryoPool DEFAULT = new kryoPool();

    private final ArrayBlockingQueue<Kryo> KRYO_THREAD_LOCAL;

    public kryoPool() {
        this(Runtime.getRuntime().availableProcessors() * 4);
    }

    public kryoPool(int size) {
        KRYO_THREAD_LOCAL = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Kryo kryo = new Kryo();
            /*开放式序列化*/
            kryo.setRegistrationRequired(false);
            // 配置 Kryo 使用兼容性序列化器
            kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
            KRYO_THREAD_LOCAL.add(kryo);
        }
    }

    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); Output output = new Output(baos)) {
            Kryo kryo = take();
            try {
                kryo.writeObject(output, obj);
                output.flush();
            } finally {
                KRYO_THREAD_LOCAL.add(kryo);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Kryo 序列化失败", e);
        }
    }

    // 反序列化：byte[] → Object
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); Input input = new Input(bais)) {
            Kryo kryo = take();
            try {
                return kryo.readObject(input, clazz);
            } finally {
                release(kryo);
            }
        } catch (Exception e) {
            throw new RuntimeException("Kryo 反序列化失败", e);
        }
    }

    public Kryo take() {
        try {
            return KRYO_THREAD_LOCAL.take();
        } catch (InterruptedException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void release(Kryo kryo) {
        KRYO_THREAD_LOCAL.offer(kryo);
    }

}
