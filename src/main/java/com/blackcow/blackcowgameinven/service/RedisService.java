package com.blackcow.blackcowgameinven.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 데이터 저장
     * @param key
     * @param value
     */
    public void saveData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Redis 데이터 저장
     * @param key
     * @param value
     * @param time  초(sec)
     */
    public void saveData(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 데이터 가져오기
     * @param key
     * @param clazz 역직렬화 받을 객체클래스
     * @return
     * @param <T>
     * @throws ClassCastException
     */
    public <T> T getData(String key, Class<T> clazz) throws ClassCastException {
        Object result = redisTemplate.opsForValue().get(key);
        if(clazz.isInstance(result)) {
            return clazz.cast(result);
        }else{
            throw new ClassCastException();
        }
    }

    /**
     * 데이터 가져오기 JSON String
     * @param key
     * @return
     */
    public String getData(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

}
