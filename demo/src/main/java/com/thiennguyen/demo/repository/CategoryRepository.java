package com.thiennguyen.demo.repository;
import com.thiennguyen.demo.entity.Category;
import com.thiennguyen.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);
    Category findByName(String name);
}