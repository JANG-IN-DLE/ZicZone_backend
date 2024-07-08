package org.zerock.ziczone.service.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.repository.member.UserRepository;

import javax.transaction.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class LoginServiceImpl implements LoginService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public String changePassword(User user, String password) {
        user = user.toBuilder()
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);
        return "success";
    }
}
