package org.plugsurfing.musicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MusicServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MusicServiceApplication.class, args);
    }

//    @Bean
//    public CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager("artist-information");
//    }
}
