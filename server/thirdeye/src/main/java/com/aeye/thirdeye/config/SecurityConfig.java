package com.aeye.thirdeye.config;

import com.aeye.thirdeye.config.Auth.AppProperties;
import com.aeye.thirdeye.config.Auth.JwtAuthenticationFilter;
import com.aeye.thirdeye.config.filter.TokenAuthenticationFilter;
import com.aeye.thirdeye.config.interceptor.OAuth2AuthenticationFailureHandler;
import com.aeye.thirdeye.config.interceptor.OAuth2AuthenticationSuccessHandler;
import com.aeye.thirdeye.config.interceptor.TokenAccessDeniedHandler;
import com.aeye.thirdeye.exception.RestAuthenticationEntryPoint;
import com.aeye.thirdeye.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.aeye.thirdeye.service.auth.CustomOAuth2UserService;
import com.aeye.thirdeye.service.auth.CustomUserDetailsService;
import com.aeye.thirdeye.token.AuthTokenProvider;
import com.aeye.thirdeye.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // OAUTH
    //private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
//    private final UserRefreshTokenRepository userRefreshTokenRepository;

    // LOCAL
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * WebSecurityConfigurerAdapter ?????? configure ??????
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // ?????? REST API??? ???????????? ??????
                .httpBasic().disable()
                .cors().and()
                .csrf().disable()
                .headers()
                .frameOptions().sameOrigin()
                .and()
                // ?????? ????????? JWT ??????????????? ????????????
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and()
                // JwtAuthenticationFilter??? UsernamePasswordAuthenticationFilter?????? ????????? ??????
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(tokenAccessDeniedHandler)
//            .and()
//                .authorizeRequests()
//                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//                .antMatchers("/api/**").hasAnyAuthority(RoleType.USER.getCode())
//                .antMatchers("/api/**/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())
//                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler())
                .failureHandler(oAuth2AuthenticationFailureHandler());

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * ???????????? ????????? : ?????? ?????? : bycrypt
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /**
     * OAUTH
     */
    // auth ????????? ??????
    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    // ?????? ?????? ??????
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    // ?????? ?????? ?????? Repository
    // ?????? ????????? ???????????? ????????? ??? ??????
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    // OAUTH ?????? ?????? ?????????
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider,
                appProperties,
//                userRefreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    // Oauth ?????? ?????? ?????????
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }


}
