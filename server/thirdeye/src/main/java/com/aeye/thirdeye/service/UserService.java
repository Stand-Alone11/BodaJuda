package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.response.ProfileResponseDto;
import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.repository.ImageRepository;
import com.aeye.thirdeye.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

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

    public GoogleIdToken.Payload testestset(String idTokenString) throws IOException, GeneralSecurityException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("81325643619-fdkkdvqs02u0cuj8hmij4gc7pqu6livj.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

// (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");


            // Use or store profile information
            // ...
            return payload;
        } else {
            System.out.println("Invalid ID token.");
        }
        return null;
    }

    public ProfileResponseDto getProfile(Long id){

        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            return null;
        }
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setEmail(user.getEmail());
        profileResponseDto.setNickName(user.getNickName());

        int[] counts = imageRepository.getCategoryUpload(id);
        int total = imageRepository.getTotalUpload(id);
        int rank = imageRepository.getRank(id).orElse(0);
        profileResponseDto.setImageTotal(total);
        if(counts != null && counts.length > 0) {
            profileResponseDto.setImageDeny(counts[0]);
            if(counts.length > 1) {
                profileResponseDto.setImageWait(counts[1]);
                if(counts.length > 2) {
                    profileResponseDto.setImageAccept(counts[2]);
                }
            }
        }
        profileResponseDto.setRank(rank);
        return profileResponseDto;
    }

}
