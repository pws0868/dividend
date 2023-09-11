package com.devidend01;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

// 알파벳O와 숫자0 을 구분해서 잘 써라
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Devidend01Application {

    public static void main(String[] args) {
        SpringApplication.run(Devidend01Application.class, args);

    }

    // 16 래디스 설치부터 하면 됨

}
