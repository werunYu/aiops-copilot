package site.werun.aiops.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 13:12
 * @description * Jackson JSON 序列化/反序列化工具类
 * 使用静态持有的 ObjectMapper 实例，线程安全，可直接静态调用。
 * 如果项目中已通过 Spring 注入了自定义配置的 ObjectMapper（比如统一命名策略），
 * 建议优先使用注入的 Bean，本工具类作为兜底/独立场景使用。
 **/
@Slf4j
public class JsonUtils {

    private static final ObjectMapper MAPPER = createDefaultMapper();

    private JsonUtils() {
        // 工具类禁止实例化
    }

    private static ObjectMapper createDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 支持 LocalDate/LocalDateTime 等 Java 8 时间类型
        mapper.registerModule(new JavaTimeModule());
        // 时间不序列化为时间戳，而是 ISO-8601 字符串
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略未知字段，避免反序列化时因多余字段报错
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 忽略 null 字段，减少序列化后的体积（对拼 prompt 场景友好，能省 token）
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /**
     * 获取内部使用的 ObjectMapper 实例（用于需要额外定制的场景）
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * 对象转 JSON 字符串，失败返回 null（不抛异常，适合日志打印等非关键路径）
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象序列化为JSON失败, obj={}", obj, e);
            return null;
        }
    }

    /**
     * 对象转格式化（美化）JSON 字符串，便于调试查看
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象序列化为格式化JSON失败, obj={}", obj, e);
            return null;
        }
    }

    /**
     * JSON 字符串转指定类型对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败, json={}, class={}", json, clazz, e);
            return null;
        }
    }

    /**
     * JSON 字符串转复杂泛型对象，例如 List<User>、Map<String, Object>
     * 用法: JsonUtils.fromJson(json, new TypeReference<List<User>>() {})
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败, json={}, typeReference={}", json, typeReference, e);
            return null;
        }
    }

    /**
     * JSON 字符串转 List<T>
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (JsonProcessingException e) {
            log.error("JSON转List失败, json={}, elementClass={}", json, elementClass, e);
            return null;
        }
    }

    /**
     * JSON 字符串转 Map<String, Object>
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("JSON转Map失败, json={}", json, e);
            return null;
        }
    }

    /**
     * 对象转 JsonNode，适合需要动态访问/遍历字段的场景
     * （比如解析 OpenAI 返回的不确定结构的 JSON）
     */
    public static JsonNode toJsonNode(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("JSON转JsonNode失败, json={}", json, e);
            return null;
        }
    }

    /**
     * 判断字符串是否为合法 JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 对象之间的转换（先序列化再反序列化，常用于 DTO <-> Entity 转换）
     */
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        if (fromValue == null) {
            return null;
        }
        return MAPPER.convertValue(fromValue, toValueType);
    }
}
