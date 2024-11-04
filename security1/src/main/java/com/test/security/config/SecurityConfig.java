package com.test.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.test.security.config.oauth.PrincipalOauth2UserService;

@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig {

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/loginProc") // "/loginProc"로 오는 요청을 Spring Security가 처리
                .defaultSuccessUrl("/")
            .and()
            .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인이 완료된 두의 후처리가 필요함.Tip.코드x, (엑세스토큰 + 사용자 프로필정보 O)
        	    .userInfoEndpoint()
        	    .userService(principalOauth2UserService);
        	// 1.코드받기, 2.엑세스토큰(권한), 3.사용자프로필 정보를 가져오고, 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
            // 4-2. (이메일, 전화번호, 이름, 아이디) 쇼핑몰 -> (집주소), 백화점몰-> (vip등급, 일반등급인지) 추가적인 회원 가입창이 나와서 회원가입 해야됨.
        
        return http.build();
    }
   
}