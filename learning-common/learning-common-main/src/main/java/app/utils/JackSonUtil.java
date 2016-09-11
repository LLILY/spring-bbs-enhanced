package app.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by lili19289 on 2016/8/25.
 */
public class JackSonUtil {

    protected static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.
     *
     *
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }

        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
     */
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param jsonStr
     * @param type
     * @return json数据转换成map
     */
    public static Map<?, ?> fromJsonToMap(String jsonStr, JavaType type) {
        if (jsonStr == null || jsonStr.length() == 0) {
            return null;
        }
        try {
            return mapper.readValue(jsonStr, type);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param jsonStr
     * @param type
     * @return json数据转换成list
     */
    public static <T extends Collection> T fromJsonToCollection(String jsonStr, JavaType type) {
        if (jsonStr == null || jsonStr.length() == 0) {
            return null;
        }
        try {
            return (T) mapper.readValue(jsonStr, type);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param jsonStr
     * @param elementClasses
     * @return json数据转换成数组
     */
    public static <T extends Object> T[] fromJsonToArray(String jsonStr, Class<?> elementClasses) {
        if (jsonStr == null || jsonStr.length() == 0) {
            return null;
        }
        try {
            return (T[]) mapper.readValue(jsonStr, elementClasses);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param collectionClass
     *            泛型的Collection
     * @param elementClasses
     *            元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
