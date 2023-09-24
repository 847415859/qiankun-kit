package com.qiankun.spring.convert;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 将金钱序列化为2位
 * BigDecimal 金额序列化 小数位
 *
 * Example usage:
 *   使用在需要序列化转化的属性
 *   @JsonSerialize(using = Decimal2Serializer.class)
 */
public class Decimal2Serializer extends JsonSerializer<Object> {

    /**
     * 将返回的BigDecimal保留两位小数，再返回给前端
     * @param value
     * @param jsonGenerator
     * @param serializerProvider
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (value != null) {
            BigDecimal val = new BigDecimal(value.toString());
            if(val.scale() < 2) {
                BigDecimal bigDecimal = new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP);
                jsonGenerator.writeString(bigDecimal.toString());
            }else {
                jsonGenerator.writeString(val.toString());
            }
        }
    }
}