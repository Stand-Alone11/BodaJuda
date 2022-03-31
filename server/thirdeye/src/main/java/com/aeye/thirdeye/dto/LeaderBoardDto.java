package com.aeye.thirdeye.dto;

import com.aeye.thirdeye.entity.LeaderBoard;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Data
@Getter
@Setter
@NoArgsConstructor
public class LeaderBoardDto {

    private Long id;
    private String nickName;
    private int rank;
    private int imageAccepted;
    private String profileImage;

    public LeaderBoardDto(LeaderBoard leaderBoard){
        this.id = leaderBoard.getUserId();
        this.nickName = leaderBoard.getNickName();
        this.rank = leaderBoard.getRanking();
        this.imageAccepted = leaderBoard.getTotal();
        this.profileImage = leaderBoard.getProfileImage();
    }

}
