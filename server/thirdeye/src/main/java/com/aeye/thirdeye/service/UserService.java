package com.aeye.thirdeye.service;

import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public Long join(User user) {
        validateDuplicateUser(user);
        user.setPassword(user.getPassword());
        user.encodePassword(passwordEncoder);
//        int randNum = (int)(Math.random()*20) + 1;
//        user.setProfileImage("#" + randNum);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }

    private void validateDuplicateUser(User user) {
        User findUsers = userRepository.findByUserId(user.getUserId());
        if (findUsers != null) {
            throw new IllegalStateException("일치하는 아이디가 존재합니다.");
        }
    }

}
