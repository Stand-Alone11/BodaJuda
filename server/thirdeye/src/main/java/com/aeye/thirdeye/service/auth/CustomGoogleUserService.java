package com.aeye.thirdeye.service.auth;

import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.entity.auth.ProviderType;
import com.aeye.thirdeye.entity.auth.RoleType;
import com.aeye.thirdeye.entity.auth.UserPrincipal;
import com.aeye.thirdeye.exception.OAuthProviderMissMatchException;
import com.aeye.thirdeye.info.OAuth2UserInfo;
import com.aeye.thirdeye.info.OAuth2UserInfoFactory;
import com.aeye.thirdeye.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomGoogleUserService {

    private final UserRepository userRepository;

    public User loadUser(GoogleIdToken.Payload payload) throws OAuth2AuthenticationException {

        try {
            return this.process(payload);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private User process(GoogleIdToken.Payload payload) {
        ProviderType providerType = ProviderType.valueOf("GOOGLE");

        User savedUser = userRepository.findByUUID(payload.getSubject());

        if (savedUser != null) {
            if (providerType != savedUser.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + savedUser.getProviderType() + " account to login."
                );
            }
            updateUser(savedUser, payload);
        } else {
            savedUser = createUser(payload, providerType);
        }

        return savedUser;
    }

    private User createUser(GoogleIdToken.Payload payload, ProviderType providerType) {
        LocalDateTime now = LocalDateTime.now();
        String profileImg = (String)(payload.get("picture"));
        User user = new User(
                payload.getEmail(),
                (String)payload.get("name"),
                payload.getEmail(),
                (String)payload.get("picture"),
                providerType,
                RoleType.USER,
                now,
                now,
                payload.getSubject()
        );

        if(payload.get("picture") == null || profileImg.trim().equals("")){
           // $$$Default profile Img 추가
        }

        return userRepository.saveAndFlush(user);
    }

    // 필요한가??
    private User updateUser(User user, GoogleIdToken.Payload payload) {
//        if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
//            user.setNickName(userInfo.getName());
//        }
//
//        if (userInfo.getImageUrl() != null && !user.getProfileImage().equals(userInfo.getImageUrl())) {
//            user.setProfileImage(userInfo.getImageUrl());
//        }

        return user;
    }
}
