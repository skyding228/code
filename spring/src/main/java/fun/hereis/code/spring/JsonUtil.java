package fun.hereis.code.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;

/**
 * Created by Administrator on 2017/6/11 0011.
 */
public class JsonUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
// 属性可见度只打印public
//     mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static void setDateFormat(DateFormat dateFormat) {
        mapper.setDateFormat(dateFormat);
    }

    /**
     * 把Java对象转为JSON字符串
     *
     * @param obj the object need to transfer into json string.
     * @return json string.
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            LOG.error("to json exception.", e);
        }
        return null;
    }

    /**
     * 把json字符串转换为期望的格式
     * @param json
     * @param klass
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> klass) {
        try {
            return mapper.readValue(json, klass);
        } catch (IOException e) {
            LOG.error("from json exception.", e);
        }
        return null;
    }
}

