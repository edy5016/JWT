package com.test.security.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.test.security.config.auth.PrincipalDetails;
import com.test.security.config.oauth.provider.GoogleUserInfo;
import com.test.security.config.oauth.provider.NaverUserInfo;
import com.test.security.config.oauth.provider.OAuth2UserInfo;
import com.test.security.model.User;
import com.test.security.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{

	@Autowired
	private BCryptPasswordEncoder byBCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리 되는 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("userRequest:" + userRequest.getClientRegistration().getRegistrationId());// registrationId로 어떤 Oauth로 로그인 했는지
		System.out.println("userRequest:" + userRequest.getAccessToken().getTokenValue());

		OAuth2User oAuth2User = super.loadUser(userRequest);
		
		// 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인 완료 -> code를 리턴(Oauth-Client라이브러리) -> AccessToken 요청
		// userRequset 정보 -> loadUser함수 호출 -> 구글로부터 회원프로필 받아준다.
//		System.out.println("userRequest:" + super.loadUser(userRequest).getAttributes());
		System.out.println("userRequest:" + oAuth2User.getAttributes()); // 위에 주석이랑동일
		
		OAuth2UserInfo oAuth2UserInfo = null;
 		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			
		} else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map<String, Object>)oAuth2User.getAttributes().get("response"));
		}
		
		String provider = oAuth2UserInfo.getProvider(); //google
		String providerId = oAuth2UserInfo.getProviderId();
		String usernamne = provider +"_" + providerId; 
		String passwd = byBCryptPasswordEncoder.encode("defaultPassword");
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(usernamne);
		
		if(userEntity == null) {
			userEntity = User.builder()
					.username(usernamne)
					.password(passwd)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		} 
		
		//회원가입을 강제로 진행해볼 예정
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
	}
}
