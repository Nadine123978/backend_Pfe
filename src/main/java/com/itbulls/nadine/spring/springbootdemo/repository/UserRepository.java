package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // يمكنك إضافة استعلامات مخصصة هنا إذا لزم الأمر
    User findByEmail(String email);
}
