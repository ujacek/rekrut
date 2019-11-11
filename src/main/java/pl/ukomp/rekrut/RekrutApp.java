package pl.ukomp.rekrut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RekrutApp {

    public static void main(String[] args) {
        SpringApplication.run(RekrutApp.class, args);
    }

}
