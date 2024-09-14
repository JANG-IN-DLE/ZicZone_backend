package org.zerock.ziczone.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.repository.member.UserRepository;
import org.springframework.security.core.userdetails.User.UserBuilder;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    //AuthenticationManager의 인증과정에서 호출되어짐
    //이곳에서는 테이블의 사용자가 있는지를 확인하고, 사용자 id, password, role을 넣어줘야함
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> user = this.userRepository.findByEmail(email);

        UserBuilder builder = null;
        //User테이블에 email이 존재
        if(user.isPresent()) {
            //email, password, role을 builder에 전달
            User currentUser = user.get();
            builder = org.springframework.security.core.userdetails.User.withUsername(currentUser.getEmail());
            builder.password(currentUser.getPassword());
            builder.roles(String.valueOf(currentUser.getUserType()));
        }else{
            throw new UsernameNotFoundException("User not found : " + email);
        }

        return builder.build();
    }

}
