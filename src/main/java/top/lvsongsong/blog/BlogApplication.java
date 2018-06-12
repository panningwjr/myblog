package top.lvsongsong.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BlogApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
        //SpringApplication app = new SpringApplication(BlogApplication.class);
        //app.setBannerMode(Banner.Mode.OFF);
        //app.run(args);
    }

}
