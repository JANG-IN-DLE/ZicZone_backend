package org.zerock.ziczone.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.ziczone.service.login.JwtService;
import org.zerock.ziczone.service.login.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//JWT토큰을 검증하고 사용자 정보를 설정하는 역할
@Log4j2
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private UserDetailsServiceImpl userDetailsService;

    // doFilterInternal : 요청을 처리하고 필터 체인을 통해 다음 필터로 전달하는 역할
    // request : 클라이언트의 HTTP요청
    // response : 서버가 클라이언트로 보내는 ㅗㅆ쎼dmdekq
    // chain : 필터 체인의 다음 필터를 호출
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        //Authorization헤더에서 JWT토큰 추출
        final String authorizationHeader = request.getHeader("Authorization");

        //username, jwt 초기화
        String username = null;
        String jwt = null;

        log.info("Request URI: {}", request.getRequestURI());
        log.info("Authorization Header: {}", authorizationHeader);


        //헤더가 존재하고 ziczone으로 시작하는 경우, 토큰에서 username추출
        if (authorizationHeader != null && authorizationHeader.startsWith("ziczone ")) {
            jwt = authorizationHeader.substring(8);
            username = jwtService.extractUsername(jwt); //토큰에서 사용자이름 추출(email)
        }

        // 사용자이름이 존재하고, 현재 SecurityContext에 인증정보가 없다면
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // UserDetails를 로드
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);


            //토큰 유효검사
            if (jwtService.validateToken(jwt, userDetails.getUsername())) {

                //사용자 정보를 바탕으로 UsernamePasswordAuthenticationToken을 생성
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        //다음 필터를 호출
        chain.doFilter(request, response);
    }
}
