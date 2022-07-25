package com.example.demo.security;

import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;




    /**
     * 권한관련Configure
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/users/**").permitAll();


//        http.authorizeRequests().antMatchers("/**")
//                .hasIpAddress("127.0.0.1")
//                .and()
//                .addFilter(getAuthenticationFilter());
//
//        http.formLogin()
//                .loginPage("/user-service/loginForm")
//                .defaultSuccessUrl("/order-service/health_check")
//                .usernameParameter("email")
//                .passwordParameter("password").permitAll();
        http
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .formLogin()//Form 로그인 인증 기능이 작동함
                .loginPage("/user-service/loginForm")//사용자 정의 로그인 페이지
                .defaultSuccessUrl("http://localhost:8090/order-service/health_check")//로그인 성공 후 이동 페이지
                .failureUrl("/login.html?error=true")// 로그인 실패 후 이동 페이지
                .usernameParameter("email")//아이디 파라미터명 설정
                .passwordParameter("password")//패스워드 파라미터명 설정
                .loginProcessingUrl("/login")//로그인 Form Action Url
                .permitAll()
                .and()
                .addFilterBefore(new AuthenticationFilter(authenticationManager(), userService, env), UsernamePasswordAuthenticationFilter.class);//사용자 정의 로그인 페이지 접근 권한 승인





        http.headers().frameOptions().disable(); // for H2 frame

    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
        //authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    /**
     * 인증 관련 Configure
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
