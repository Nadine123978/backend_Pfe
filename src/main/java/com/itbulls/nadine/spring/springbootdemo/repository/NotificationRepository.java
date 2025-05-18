package com.itbulls.nadine.spring.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itbulls.nadine.spring.springbootdemo.model.Notification;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
	long countByIsReadFalse();
}