package org.zerock.ziczone.service.login;

import org.zerock.ziczone.domain.member.User;

public interface LoginService {

    String changePassword(User user, String password);
}
