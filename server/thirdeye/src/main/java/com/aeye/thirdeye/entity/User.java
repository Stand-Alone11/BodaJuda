package com.aeye.thirdeye.entity;

import com.aeye.thirdeye.entity.auth.ProviderType;
import com.aeye.thirdeye.entity.auth.RoleType;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "User")
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 30)
    private String userId;

    @Size(max = 20)
    private String password;

    private String email;

    @Size(max = 20)
    private String nickName;

    @Column(name = "CREATED_AT")
//    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "MODIFIED_AT")
//    @NotNull
    private LocalDateTime modifiedAt;

    @Column(name = "PROVIDER_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
//    @NotNull
    private ProviderType providerType;  // 구글, 카카오, 네이버

    @Column(name = "ROLE_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
//    @NotNull
    private RoleType roleType;

    private String UUID;

    private String profileImage;

    @OneToMany(mappedBy = "user")
    private List<Image> imageList = new ArrayList<>();



    /**
     * Security 회원 가입
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return nickName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User(
            @Size(max = 64) String userId,
            @NotNull @Size(max = 100) String nickName,
            @NotNull @Size(max = 512) String email,
            @Size(max = 512) String profileImage,
            ProviderType providerType,
            RoleType roleType,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            String uuid
            ) {
        this.userId = userId;
        this.nickName = nickName;
        this.password = "NO_PASS";
        this.email = email != null ? email : "NO_EMAIL";
        this.profileImage = profileImage != null ? profileImage : "";
        this.providerType = providerType != null ? providerType : ProviderType.LOCAL;
        this.roleType = roleType != null ? roleType : RoleType.USER;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.UUID = uuid;
    }

    @Transactional
    public void encodePassword(PasswordEncoder passwordEncoder){
        password = passwordEncoder.encode(password);
    }
}