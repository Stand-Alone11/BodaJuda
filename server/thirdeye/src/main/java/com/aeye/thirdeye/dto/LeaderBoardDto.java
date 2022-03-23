package com.aeye.thirdeye.dto;

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

    private BigInteger id;
    private String nickName;
    private BigInteger rank;
    private BigInteger imageTotal;

    public LeaderBoardDto(
            BigInteger id,
            String nickName,
            BigInteger rank,
            BigInteger imageTotal
            ){
        this.id = id;
        this.nickName = nickName;
        this.rank = rank;
        this.imageTotal = imageTotal;
    }

}
