package com.thiennguyen.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;

    // 1. Cấu hình Interceptor (Bảo vệ các route của Admin)
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }

    // 2. Cấu hình Resource Handler (Quản lý file upload)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("upload_img").toAbsolutePath().toString();
        String location = "file:/" + uploadPath.replace("\\", "/") + "/";
        registry.addResourceHandler("/upload_img/**")
                .addResourceLocations(location);
    }

    // 3. Cấu hình View Controller (Điều hướng trang tĩnh không cần Controller)
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/chinh-sach-bao-mat").setViewName("introduce/privacy-policy");
        registry.addViewController("/dieu-khoan").setViewName("introduce/clause");
        registry.addViewController("/hoi-dap").setViewName("introduce/faq");
    }
}