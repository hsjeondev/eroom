package com.eroom.security;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final DataSource dataSource;
	
	@Bean
	WebSecurityCustomizer configure() {
		return web -> web.ignoring()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService customUserDetailsService) throws Exception {
		http.userDetailsService(customUserDetailsService)
			.authorizeHttpRequests(requests -> requests
					
//					.anyRequest().permitAll() // 로그인 없이도 승인.
					
					.requestMatchers("/login", "/assets/**", "/vendors/**").permitAll()
					.requestMatchers("/admin/**").hasRole("ADMIN") // admin은 필요할 때 주석 해제
					.anyRequest().authenticated() // 모든 요청에 대한 인증 권한 필요할 때 주석 해제
					)
			.formLogin(login -> login.loginPage("/login")
									.successHandler(new MyLoginSuccessHandler())
									.failureHandler(new MyLoginFailureHandler()))
			.logout(logout -> logout.logoutUrl("/logout")
									.clearAuthentication(true)
									.logoutSuccessUrl("/login")
									.invalidateHttpSession(true));
		
		return http.build();
	}
	
		// 데이터베이스 접근 Bean 등록
		@Bean
		PersistentTokenRepository tokenRepository() {
			JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
			jdbcTokenRepository.setDataSource(dataSource);
			return jdbcTokenRepository;
		}
		
		// 비밀번호 암호화 사용될 Bean 등록
		@Bean
		PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		// AuthenticationManager(인증 관리)
		@Bean
		AuthenticationManager authenticationManager(
				AuthenticationConfiguration authenticationCofiguration) throws Exception {
			return authenticationCofiguration.getAuthenticationManager();
		}
}
