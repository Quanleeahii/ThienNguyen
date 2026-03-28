package com.thiennguyen.demo.repository;
import com.thiennguyen.demo.entity.Notification;
import com.thiennguyen.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}