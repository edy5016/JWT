package com.test.security.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.test.security.model.User;
import com.test.security.repository.UserRepository;

// 시큐리티 설정에서 loginProcessingUrl("login")
// login 요청이 오면 자동으로 UserDetailsService  타입으로 IOC되어 있는 loadUserByUsername함수가 실행됨. 규칙
// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
@Service
public class PrincipalDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	// 시큐리티 session = Authentication = UserDetails 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println(username);
		// 매개변수 username은 로그인페이지에서 파라미터 username임 => 로그인페이지에서 username으로안하면 파라미터 안받아짐. 다른걸로 하고 싶을떄 SecurityConfig 설정에서 .usernameParametr로 설정해줘야댐 
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return null;
	}
	
}
