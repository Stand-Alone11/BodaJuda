package com.aeye.thirdeye.dto.response;

import lombok.*;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCountDto {

    SimpleProjectDto project;

    private int accepted = 0;
    private int denied = 0;
    private int waited = 0;
    private int total = 0;

}
