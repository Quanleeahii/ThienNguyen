package com.thiennguyen.demo.repository;

import com.thiennguyen.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // --- BỘ 4 CÂU TRUY VẤN NÂNG CẤP CHO TÌM KIẾM VÀ PHÂN TRANG ---

    // 1. Lấy tất cả trừ những người ĐÃ XÓA
    @Query("SELECT u FROM User u WHERE u.status != 'DELETED'")
    Page<User> findAllActive(Pageable pageable);

    // 2. Lọc chính xác theo trạng thái (Cho phép tìm cả người DELETED nếu muốn)
    @Query("SELECT u FROM User u WHERE u.status = :status")
    Page<User> findByStatus(@Param("status") String status, Pageable pageable);

    // 3. Tìm kiếm theo từ khóa (Bỏ qua những người ĐÃ XÓA)
    @Query("SELECT u FROM User u WHERE u.status != 'DELETED' AND (u.fullName LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%)")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 4. Vừa tìm từ khóa Vừa lọc trạng thái
    @Query("SELECT u FROM User u WHERE u.status = :status AND (u.fullName LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%)")
    Page<User> findByStatusAndKeyword(@Param("status") String status, @Param("keyword") String keyword, Pageable pageable);
}