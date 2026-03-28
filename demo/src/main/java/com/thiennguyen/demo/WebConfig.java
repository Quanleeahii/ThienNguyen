package com.thiennguyen.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("upload_img").toAbsolutePath().toString();

        String location = "file:/" + uploadPath.replace("\\", "/") + "/";

        System.out.println("=====================================================");
        System.out.println("XÁC NHẬN THƯ MỤC ẢNH ĐANG ĐƯỢC MAP TỚI: " + location);
        System.out.println("=====================================================");

        registry.addResourceHandler("/upload_img/**")
                .addResourceLocations(location);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/chinh-sach-bao-mat").setViewName("introduce/privacy-policy");


        registry.addViewController("/dieu-khoan").setViewName("introduce/clause");

        registry.addViewController("/hoi-dap").setViewName("introduce/faq");
    }
}