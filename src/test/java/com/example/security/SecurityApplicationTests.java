package com.example.security;

import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
class SecurityApplicationTests {
	@Autowired

	private UserRepository userRepository;
	@Test
	void contextLoads() {
		List<User> userList = userRepository.findAll();
		userList.forEach(s-> System.out.println(s.toString()));

		BCryptPasswordEncoder bcRyptPasswordEncoder = new BCryptPasswordEncoder();

		System.out.println("bcRyptPasswordEncoder.encode(\"123456\"):"+bcRyptPasswordEncoder.encode("123456").toString());

	//$2a$10$JNVWTh5Yq56kJtrCZkcDk.DL/L/i8g3KrTAshcHW3mFf8//lnfG56
	}

}
