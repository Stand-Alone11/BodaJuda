package com.aeye.thirdeye.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
public class User {

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
    private OffsetDateTime createdAt;

    @PrePersist
    private void beforeSaving() {
        createdAt = OffsetDateTime.now();
    }

    @OneToMany(mappedBy = "user")
    private List<Image> imageList = new ArrayList<>();

}