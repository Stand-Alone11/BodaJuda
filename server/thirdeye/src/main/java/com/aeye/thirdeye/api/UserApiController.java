package com.aeye.thirdeye.api;

import com.aeye.thirdeye.dto.response.ErrorResponse;
import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.entity.auth.ProviderType;
import com.aeye.thirdeye.entity.auth.RoleType;
import com.aeye.thirdeye.repository.UserRepository;
import com.aeye.thirdeye.service.UserService;
import com.aeye.thirdeye.token.JwtTokenProvider;
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

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
//        @Size(max = 512) String profileImageUrl;
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
    public ResponseEntity<?> signout(@RequestHeader(value = "Authorization") String token) {
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
     * @param userInfo {email, password}
     * @return
     */
    @PostMapping("/accounts/login")
    @ApiOperation(value = "일반 로그인", notes = "")
    public ResponseEntity<?> login( @RequestBody Map<String, String> userInfo) {
        User user = userRepository.findByUserId(userInfo.get("userId"));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(messageSource.getMessage("error.none.user", null, LocaleContextHolder.getLocale())));
        }

        if (!passwordEncoder.matches(userInfo.get("password"), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(messageSource.getMessage("error.wrong.password", null, LocaleContextHolder.getLocale())));
        }

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getId());

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

}
