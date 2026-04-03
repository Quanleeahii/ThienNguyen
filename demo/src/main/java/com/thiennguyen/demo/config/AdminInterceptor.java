package com.thiennguyen.demo.config;

import com.thiennguyen.demo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // 1. Nếu chưa đăng nhập -> Đuổi ra trang Đăng nhập
        if (loggedInUser == null) {
            response.sendRedirect("/dang-nhap");
            return false;
        }

        // 2. Nếu đã đăng nhập nhưng KHÔNG CÓ QUYỀN ADMIN -> Đuổi về Trang chủ
        if (!"ROLE_ADMIN".equals(loggedInUser.getRole())) {
            response.sendRedirect("/");
            return false;
        }

        // 3. Nếu là Admin -> Mở cửa cho vào
        return true;
    }
}