package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.LeaderBoardDto;
import com.aeye.thirdeye.dto.request.ChangeUserInfoRequest;
import com.aeye.thirdeye.dto.response.ProfileResponseDto;
import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.repository.ImageRepository;
import com.aeye.thirdeye.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    @PersistenceContext
    EntityManager em;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public Long join(User user) {
        validateDuplicateUser(user);
        user.setPassword(user.getPassword());
        user.encodePassword(passwordEncoder);
        System.out.println(user.getNickName());
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public void deleteUser(Long id) {
        imageRepository.deleteAllByUser(id);
        userRepository.deleteById(id);
    }

    private void validateDuplicateUser(User user) {
        User findUsers = userRepository.findByUserId(user.getUserId());
        if (findUsers != null) {
            throw new IllegalStateException("일치하는 아이디가 존재합니다.");
        }
    }

    public GoogleIdToken.Payload googleLogin(String idTokenString) throws IOException, GeneralSecurityException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("81325643619-id2cv0v6ulnggm10an4r8s1a6sudrh0k.apps.googleusercontent.com"))
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

        String[] counts = imageRepository.getCategoryUpload(id);
        int total = imageRepository.getTotalUpload(id);
        int rank = imageRepository.getRank(id).orElse(0);
        profileResponseDto.setImageTotal(total);
        if(counts != null && counts.length > 0) {
            for (String s: counts){
                if(s.endsWith("N")){
                    profileResponseDto.setImageDeny(Integer.parseInt(s.substring(0,s.length()-1)));
                }else if(s.endsWith("W")){
                    profileResponseDto.setImageWait(Integer.parseInt(s.substring(0,s.length()-1)));
                }else if(s.endsWith("Y")){
                    profileResponseDto.setImageAccept(Integer.parseInt(s.substring(0,s.length()-1)));
                }
            }
//            profileResponseDto.setImageDeny(counts[0]);
//            if(counts.length > 1) {
//                profileResponseDto.setImageWait(counts[1]);
//                if(counts.length > 2) {
//                    profileResponseDto.setImageAccept(counts[2]);
//                }
//            }
        }
        profileResponseDto.setRank(rank);
        return profileResponseDto;
    }

    public List<LeaderBoardDto> getLeaderBoard(int page, int size){
        int pageStart = page * size;

        String query = "SELECT u.id, u.nick_name, ranked.ranking, ranked.total " +
                "FROM (SELECT i.user_id, rank() over(order by count(*) DESC) AS RANKING, " +
                "COUNT(*) total from image i group by i.user_id ) ranked, user u " +
                "WHERE ranked.user_id = u.id order by ranked.ranking asc limit " + pageStart + " ," + size;

        JpaResultMapper result = new JpaResultMapper();
        Query resultQuery = em.createNativeQuery(query);
        List<LeaderBoardDto> leaderBoardDtos = result.list(resultQuery, LeaderBoardDto.class);
        return leaderBoardDtos;
    }

    @Transactional
    public void updatePassword(Long id, String password){
        User user = userRepository.findById(id).orElse(null);
        if(user == null) return;
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void updateUserInfo(Long id, String nickName, String email, String profileImage) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) return;
        user.setNickName(nickName);
        user.setEmail(email);
        user.setProfileImage(profileImage);

        userRepository.save(user);

    }

    public String getProfileImgUrl(Long id){
        User user = userRepository.findById(id).orElse(null);
        return user.getProfileImage();
    }
}
