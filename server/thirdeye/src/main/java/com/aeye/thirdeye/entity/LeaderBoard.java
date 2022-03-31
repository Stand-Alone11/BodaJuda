package com.aeye.thirdeye.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "LeaderBoard")
@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
public class LeaderBoard {

    @Id
    private Long userId;

    @Size(max = 50)
    private String nickName;

    private int ranking;

    private int total;
    
    private String profileImage;
}
