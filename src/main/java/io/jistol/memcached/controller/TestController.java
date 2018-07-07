package io.jistol.memcached.controller;

import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.api.format.SerializationType;
import com.google.code.ssm.providers.CacheException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
public class TestController {
    @Autowired
    private CacheFactory defaultMemcachedClient;

    @PostMapping("/{key}/{value}")
    public String postValue(@PathVariable("key") String key, @PathVariable("value") String value) throws TimeoutException, CacheException {
        defaultMemcachedClient.getCache().set(key, 500000, value, SerializationType.PROVIDER);
        return "SUCCESS";
    }

    @GetMapping("/{key}")
    public String getValue(@PathVariable("key") String key) throws TimeoutException, CacheException {
        return defaultMemcachedClient.getCache().get(key, SerializationType.PROVIDER);
    }
}
