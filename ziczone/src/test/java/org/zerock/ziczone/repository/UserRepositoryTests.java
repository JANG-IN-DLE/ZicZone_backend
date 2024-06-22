package org.zerock.ziczone.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.ziczone.domain.User;
import org.zerock.ziczone.domain.UserType;

@SpringBootTest
@Log4j2
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testInsert(){
        User user = new User().builder()
                .userName("전민재")
                .email("alswo9672@gmail.com")
                .password("1234")
                .userIntro("전민재입니다.")
                .userType(UserType.MALE)
                .build();
        userRepository.save(user);

        log.info("User saved: " + user);

    }
}
