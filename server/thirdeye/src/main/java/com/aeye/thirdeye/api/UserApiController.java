package com.aeye.thirdeye.api;

import com.aeye.thirdeye.dto.LeaderBoardDto;
import com.aeye.thirdeye.dto.request.ChangePasswordRequest;
import com.aeye.thirdeye.dto.request.ChangeUserInfoRequest;
import com.aeye.thirdeye.dto.request.LoginRequest;
import com.aeye.thirdeye.dto.response.ApiResponse;
import com.aeye.thirdeye.dto.response.ErrorResponse;
import com.aeye.thirdeye.dto.response.ProfileResponseDto;
import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.entity.auth.ProviderType;
import com.aeye.thirdeye.entity.auth.RoleType;
import com.aeye.thirdeye.repository.UserRepository;
import com.aeye.thirdeye.service.UserService;
import com.aeye.thirdeye.service.auth.CustomGoogleUserService;
import com.aeye.thirdeye.token.JwtTokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "유저 관련 API")
public class UserApiController {

    private final UserRepository userRepository;

    private final UserService userService;
//    private final UserSettingService userSettingService;

    private final PasswordEncoder passwordEncoder;

    private final MessageSource messageSource;

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomGoogleUserService customGoogleUserService;

    /**
     * 회원 가입
     * @param request
     * @param bindingResult
     * @return
     * 반환 코드 : 201/ 404
     */
    @PostMapping("/accounts/signup")
    @ApiOperation(value = "회원가입", notes = "")
    public ResponseEntity signup(@Validated @RequestBody CreateUserRequest request,
                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setNickName(request.getNickname());
        user.setProviderType(ProviderType.LOCAL);
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());
        user.setRoleType(RoleType.USER);
        user.setProfileImage(request.getProfileImage());

        try {
            Long id = userService.join(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        }
        catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(messageSource
                            .getMessage("error.same.id", null, LocaleContextHolder.getLocale())));
        }
    }

    @Data
    static class CreateUserRequest {
        // email validation 설정
//        @Email(message = "{error.format.email}", regexp = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")
        @NotNull(message = "{email.notempty}")
        private String email;
        private String nickname;
        // password validation 설정
//        @Size(min=4, max=12, message = "{error.size.password}")
//        @Length(min=3, max=128, message = "비밀번호 길이 불일치")
        private String password;
        @Size(max = 64) String userId;
        @Size(max = 512) String profileImage;
//        ProviderType providerType;
//        RoleType roleType;
        LocalDateTime createdAt;
        LocalDateTime modifiedAt;
    }

    /**
     * 회원 탈퇴
     * @param token
     * @return
     */
    @DeleteMapping("/accounts/signout")
    @ApiOperation(value = "회원탈퇴", notes = "")
    public ResponseEntity<?> signout(@ApiParam(value = "jwt 토큰")@RequestHeader(value = "Authorization") String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(messageSource.getMessage("error.valid.jwt", null, LocaleContextHolder.getLocale())));
        }
        Long id = jwtTokenProvider.getId(token);

        userService.deleteUser(id);

        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    /**
     * 로그인 JWT 발급
     * @param loginRequest // {id , password}
     * @return
     */
    @PostMapping("/accounts/login")
    @ApiOperation(value = "일반 로그인", notes = "")
    public ResponseEntity<?> login(@ApiParam(value = "id, Password")@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUserId(loginRequest.getUserId());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(messageSource.getMessage("error.none.user", null, LocaleContextHolder.getLocale())));
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(messageSource.getMessage("error.wrong.password", null, LocaleContextHolder.getLocale())));
        }

        String token = jwtTokenProvider.createToken(user.getNickName(), user.getId());

        return ResponseEntity.ok(new LoginUserResponse(token));
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class LoginUserResponse {
        private String token;
        public LoginUserResponse(String accessToken) {
            this.token = accessToken;
        }
    }

    /**
     * 프로필 조회 API
     * @param id
     * @return
     * 반환 코드 : 200 / 404
     */
    @GetMapping("/accounts/info/{id}")
    @ApiOperation(value = "프로필 조회", notes = "")
    public ResponseEntity<?> getProfile(@ApiParam(value = "user 시퀀스 값") @PathVariable("id") Long id){
        ProfileResponseDto profileResponseDto = userService.getProfile(id);
        if(profileResponseDto == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(profileResponseDto);
    }

    /**
     * 리더보드 순위 API
     * @param page
     * @param size
     * @return
     * 반환 코드 : 200 / 404
     */
    @GetMapping("/accounts/rank")
    @ApiOperation(value = "리더보드 순위 API", notes = "")
    public ResponseEntity<?> getLeaderBoard(@ApiParam(value = "page (0부터 시작)")
                                            @RequestParam(value = "page") int page,
                                            @ApiParam(value = "size (페이지 당 size)")
                                            @RequestParam(value = "size") int size){
        List<LeaderBoardDto> leaderBoardDtos = userService.getLeaderBoard(page, size);
        if(leaderBoardDtos == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(leaderBoardDtos);
    }


    /**
     * 구글 로그인 API
     * @param token
     * @return
     * 반환 코드 : 200
     */
    @PostMapping("/acoounts/auth/login")
    @ApiOperation(value = "구글 로그인", notes = "")
    public ApiResponse googleLogin(@ApiParam(value = "구글 토큰")
                                       @RequestHeader(value = "Authorization") String token) {
        GoogleIdToken.Payload payload = null;
        String tokenResult = "";
        try {
            payload = userService.googleLogin(token);

            // DB에 유저 저장
            User user = customGoogleUserService.loadUser(payload);

            // Token 생성
            tokenResult = jwtTokenProvider.createToken(user.getNickName(), user.getId());

            // Token return
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return ApiResponse.success("token", tokenResult);
    }

    /**
     * 패스워드 변경 API
     * @param token
     * @param changePasswordRequest // {paswword, passwordConfirm}
     * @return
     * 반환 코드 : 200 / 403 / 406
     */
    @PostMapping("/acoounts/update/password")
    @ApiOperation(value = "패스워드 변경", notes = "")
    public ResponseEntity<?> changePassword(@ApiParam(value = "Jwt 토큰")
                                                @RequestHeader(value = "Authorization") String token,
                                            @ApiParam(value = "Password") @RequestBody ChangePasswordRequest changePasswordRequest) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(messageSource.getMessage("error.valid.jwt", null, LocaleContextHolder.getLocale())));
        }
        else{
            Long id = jwtTokenProvider.getId(token);
            if(changePasswordRequest == null ||
                    !changePasswordRequest.getPassword().trim().equals(changePasswordRequest.getPasswordConfirm().trim())){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Password Confirm is not Accepted");
            }
            userService.updatePassword(id, changePasswordRequest.getPassword());
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }



    // 회원 정보 변경
    @PostMapping("/acoounts/update/userinfo")
    @ApiOperation(value = "회원 정보 변경", notes = "")
    public ResponseEntity<?> changeUserInfo(@ApiParam(value = "jwt 토큰")@RequestHeader(value = "Authorization") String token,
                                            @RequestBody ChangeUserInfoRequest changeUserInfoRequest) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(messageSource.getMessage("error.valid.jwt", null, LocaleContextHolder.getLocale())));
        }
        else{
            Long id = jwtTokenProvider.getId(token);
            if(changeUserInfoRequest == null || changeUserInfoRequest.getEmail() == null ||
                    changeUserInfoRequest.getNickName() == null || changeUserInfoRequest.getProfileImage() == null){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Content is null");
            }
            userService.updateUserInfo(id, changeUserInfoRequest.getNickName(), changeUserInfoRequest.getEmail(),changeUserInfoRequest.getProfileImage());
        }


        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
