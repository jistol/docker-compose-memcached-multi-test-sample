package io.jistol.memcached;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@EnableCaching
@SpringBootApplication
public class MemcachedApplication implements CommandLineRunner {
	@Autowired private CacheManager cacheManager;

	public static void main(String[] args) {
		SpringApplication.run(MemcachedApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("==============================================================================");
		log.info("Using cache manager : " + this.cacheManager.getClass().getName());
		log.info("==============================================================================");
	}
}
