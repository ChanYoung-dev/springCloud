package com.example.demo.security;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import com.example.demo.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;


    @Autowired
    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env){
        this.userService = userService;
        this.env = env;
        super.setAuthenticationManager(authenticationManager);
    }

    /**
     * 인증 시도
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String email = request.getParameter("email");

        String username = obtainUsername(request);//username 추출
        String password = obtainPassword(request);
        System.out.println("email = " + email);

        //RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
        RequestLogin creds = new RequestLogin(email, password);
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword(),
                        new ArrayList<>()
                )
        );
    }

    /**
     * 인가 작업
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        String userName = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .sameSite("Strict")
                .path("/")
                .build();
        response.addHeader("Set-Cookie", cookie.toString() + ";HttpOnly");

        response.addHeader("token", token);
        System.out.println("token = " + token);
        response.addHeader("userId", userDetails.getUserId());
        response.addHeader("Authorization","bearer " + token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("bearer " + token);
        //          chain.doFilter(request, response);


    }


}
