
package com.test.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.security.config.auth.PrincipalDetails;
import com.test.security.model.User;
import com.test.security.repository.UserRepository;

@Controller
public class indexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String loginTest(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
		System.out.println("=====/test/login");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // 구글로그인시 에러 (캐스팅 에러)
		
		System.out.println("User :" + principalDetails.getUser());
		System.out.println("userDetails : " + userDetails.getUser());
		return "세션 정보 확인";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String loginOauthTest(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) {
		System.out.println("=====/test/login");
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 구글로그인시 에러
		
		System.out.println("User :" + oAuth2User.getAttributes());
		System.out.println("oauth2User:" + oauth.getAuthorities());
		return "세션 정보 확인";
	}
	
	
	@GetMapping({"","/"})
	public String index() {
		// 머스테치 기본폴더 src/main/resources/
		// 뷰리졸버 설정 : templates (prefix), .mustache (suffix) 설정은 생략가능!
		return "index";
	}
	
	//Oauth 로그인을 해도 PrincipalDetails
	//일반로그인 해도 PrincipalDetails 
	// @AuthenticationPrincipal PrincipalDetails 리턴될떄 Authentication객체에 저장이됨.
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails:" + principalDetails.getUser());
		return "user";
	}
	
	
	@GetMapping("/admin")
	public @ResponseBody  String admin() {
		return "admin";
	}
	
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		String rawPass = user.getPassword();
		String encRawPass = bCryptPasswordEncoder.encode(rawPass);
		user.setPassword(encRawPass);
		userRepository.save(user); // 비번:1234면 =>시큐리티로 로그인 할수 없음 이유는 패스워드가 암호화 되지않음
		return "redirect:/loginForm";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	

	// data 메서드 실행 직전에 실행
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "data";
	}
}
