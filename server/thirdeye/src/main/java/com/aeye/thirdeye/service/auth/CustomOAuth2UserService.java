package com.aeye.thirdeye.service.auth;

import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.entity.auth.ProviderType;
import com.aeye.thirdeye.entity.auth.RoleType;
import com.aeye.thirdeye.entity.auth.UserPrincipal;
import com.aeye.thirdeye.exception.OAuthProviderMissMatchException;
import com.aeye.thirdeye.info.OAuth2UserInfo;
import com.aeye.thirdeye.info.OAuth2UserInfoFactory;
import com.aeye.thirdeye.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        User savedUser = userRepository.findByUserId(userInfo.getId());

        if (savedUser != null) {
            if (providerType != savedUser.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + savedUser.getProviderType() + " account to login."
                );
            }
            updateUser(savedUser, userInfo);
        } else {
            savedUser = createUser(userInfo, providerType);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(
                userInfo.getEmail(),
                userInfo.getName(),
                userInfo.getEmail(),
                userInfo.getImageUrl(),
                providerType,
                RoleType.USER,
                now,
                now,
                "zzzz"
        );
        // 프로필 이미지 쓰면 추가
//        if(user.getProfileImage() == null || user.getProfileImage().trim() == ""){
//            int randNum = (int)(Math.random()*20) + 1;
//            user.setProfileImage("#" + randNum);
//        }
        return userRepository.saveAndFlush(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
            user.setNickName(userInfo.getName());
        }
//
//        if (userInfo.getImageUrl() != null && !user.getProfileImage().equals(userInfo.getImageUrl())) {
//            user.setProfileImage(userInfo.getImageUrl());
//        }

        return user;
    }
}
