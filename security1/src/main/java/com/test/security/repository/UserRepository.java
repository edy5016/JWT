package com.test.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.security.model.User;

// CRUD 함수를 JpaRepository가 들고있음

public interface UserRepository extends JpaRepository<User, Integer> {

	//findBy규칙 -> Username 문법
	// select * from user where username = ?
	public User findByUsername(String username);

}
