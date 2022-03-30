package com.aeye.thirdeye.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ProfileResponseDto {

    private String profileImgUrl;
    private String nickName;
    private String email;

    private int imageTotal;
    private int imageWait;
    private int imageAccept;
    private int imageDeny;
    private int rank;

    private List<HistoryCountDto> history;

}
