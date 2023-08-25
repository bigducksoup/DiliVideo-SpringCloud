package com.ducksoup.dilivideomain.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: redis 工具类
 * @Author: junqiang.lu
 * @Date: 2018/10/29
 */
@Component
@Slf4j
public class RedisUtil implements Serializable {

    private static final long serialVersionUID = 894291893913244121L;

    @Autowired
    private RedisTemplate<Serializable, Object> redisTemplate;


    /**
     * 写入缓存
     * 不指定保存时间,永久保存
     *
     * @param key
     * @param value
     * @return
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 写入缓存
     * 指定保存时间,单位：秒,超时将自动删除
     *
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public void set(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * 判断缓存中是否有对应的 key
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除一条记录
     *
     * @param key
     * @return
     */
    public boolean remove(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 批量删除
     *
     * @param keyList
     */
    public void removeBatch(List<String> keyList) {
        Set<Serializable> keys = new HashSet<>(keyList);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 向 map 集合插入一条数据
     *
     * @param key     集合 key
     * @param hashKey 元素 key
     * @param value   元素值
     */
    public void mapPut(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 向 map 集合插入多条数据
     *
     * @param key        集合 key
     * @param elementMap 元素 map 集合
     */
    public void mapPutBatch(String key, Map<String, Object> elementMap) {
        redisTemplate.opsForHash().putAll(key, elementMap);
    }

    /**
     * 从 map 集合中获取一个元素
     *
     * @param key     集合 key
     * @param hashKey 元素 key
     * @param clazz   元素值类
     * @return
     */
    public <V> V mapGet(String key, String hashKey, Class<V> clazz) {
        return (V) redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 从 map 集合中读取所有元素
     *
     * @param key   集合 key
     * @param clazz 元素值类
     * @return
     */
    public <V> List<V> mapGetAll(String key, Class<V> clazz) {
        return redisTemplate.opsForHash().values(key).stream().map(o -> (V) o).collect(Collectors.toList());
    }

    /**
     * 删除 map 集合中一个元素
     *
     * @param key     集合 key
     * @param hashKey 元素 key
     */
    public void mapRemove(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 批量删除 map 集合元素
     *
     * @param key         集合 key
     * @param hashKeyList 元素 key 列表
     */
    public void mapRemoveBatch(String key, List<String> hashKeyList) {
        redisTemplate.opsForHash().delete(key, hashKeyList.toArray());
    }

    public void addToSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void rmFromSet(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public long countSetItem(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size == null ? 0 : size;
    }

    public boolean checkExistSetItem(String key, Object value) {


        Boolean member = redisTemplate.opsForSet().isMember(key, value);
        if (member == null) {
            member = false;
        }
        return member;

    }


    public <T> Set<T> getSet(String key, Class<T> clazz) {

        return redisTemplate.opsForSet().members(key).stream().map(item -> (T) item).collect(Collectors.toSet());

    }


    public static final String SETNX_SCRIPT = "return redis.call('setnx',KEYS[1], ARGV[1])";


    /**
     * redis实现分布式锁
     *
     * @param key
     * @return
     */
    public boolean setNx(String key, int time) {
        //自定义脚本
        DefaultRedisScript<List> script = new DefaultRedisScript<>(SETNX_SCRIPT, List.class);
        //执行脚本,传入参数,由于value没啥用,这里随便写死的"1"
        List<Long> rst = redisTemplate.execute(script, Collections.singletonList(key), "1");
        //返回1,表示设置成功,拿到锁
        if (rst.get(0) == 1) {
            log.info(key + "成功拿到锁");
            //设置过期时间
            expire(key, time);
            log.info(key + "已成功设置过期时间:" + time + " 秒");
            return true;
        } else {
            long expire = getExpire(key);
            log.info(key + "未拿到到锁,还有" + expire + "释放");
            return false;
        }

    }


    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public void expire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }



    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }



    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<Serializable>) CollectionUtils.arrayToList(key));
            }
        }
    }


}
