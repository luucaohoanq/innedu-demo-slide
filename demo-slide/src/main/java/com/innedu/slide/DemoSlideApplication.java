package com.innedu.slide;

import io.github.lcaohoanq.annotations.BrowserLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@BrowserLauncher(
    url = "http://localhost:8080/swagger-ui/index.html"
)
public class DemoSlideApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSlideApplication.class, args);
    }

}
