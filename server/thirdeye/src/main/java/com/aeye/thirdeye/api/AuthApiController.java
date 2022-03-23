package com.aeye.thirdeye.api;

import com.aeye.thirdeye.config.Auth.AppProperties;
import com.aeye.thirdeye.dto.auth.AuthReqModel;
import com.aeye.thirdeye.dto.response.ApiResponse;
import com.aeye.thirdeye.entity.auth.UserPrincipal;
import com.aeye.thirdeye.token.AuthToken;
import com.aeye.thirdeye.token.AuthTokenProvider;
import com.aeye.thirdeye.token.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/accounts/auth")
@RequiredArgsConstructor
@Api(tags = "소셜로그인 API")
public class AuthApiController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    @PostMapping("/login")
    @ApiOperation(value = "소셜로그인 진행", notes = "")
    public ApiResponse login(
            @RequestBody AuthReqModel authReqModel
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReqModel.getId(),
                        authReqModel.getPassword()
                )
        );

        String userId = authReqModel.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userId,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // jwt 토큰 발급
        return ApiResponse.success("token", accessToken.getToken());
    }

}