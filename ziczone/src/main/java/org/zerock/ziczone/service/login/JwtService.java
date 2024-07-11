package org.zerock.ziczone.service.login;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.member.UserType;
import org.zerock.ziczone.repository.member.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Service
public class JwtService {

    private final UserRepository userRepository;

    // 토큰의 유효기간
    static final long EXPIRE_TIME = 1000 * 60 * 60 * 24; // 1일
    static final String PREFIX = "Ziczone "; // 토큰을 빨리 찾기 위해 붙여주는 문자열
    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 비밀키

    // 비밀키로 서명된 JWT토큰 발급
    public String getToken(String email) {

        Optional<UserType> optionalUserType = userRepository.findUserTypeByEmail(email);
        if(!optionalUserType.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        String role = optionalUserType.get().name();
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key)
                .compact();

        return PREFIX + token;
    }

    // 클라이언트가 보내온 요청 헤더에서, 토큰을 확인하고 사용자 이름으로 전환함(로그인이외의 다른 컨트롤러에서 적절하게 사용해야함)
    public String getAuthUser(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 헤더에 존재한다면
        if (token != null && token.startsWith(PREFIX)) {
            String user = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody()
                    .getSubject();

            // token을 비밀키로 풀었을 때 user가 잘 추출되면
            if (user != null)
                return user;
        }
        return null;
    }

    // 토큰에서 모든 클레임 추출
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace(PREFIX, ""))
                .getBody();
    }

    // 특정 클레임 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 사용자 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 토큰에서 역할 추출
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // 토큰 만료 여부 확인
    public Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // 토큰 유효성 검증
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}