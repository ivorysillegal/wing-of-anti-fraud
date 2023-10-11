package com.gduf.service.impl;

import redis.clients.jedis.Jedis;

public class StringRedisServiceImpl {
    private Jedis jedis;

    public StringRedisServiceImpl(String redisHost, int redisPort) {
        jedis = new Jedis(redisHost, redisPort);
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public void expire(String key, int seconds) {
        jedis.expire(key, seconds);
    }

    public void close() {
        jedis.close();
    }
}

