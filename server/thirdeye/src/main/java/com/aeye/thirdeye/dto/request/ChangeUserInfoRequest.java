package com.aeye.thirdeye.dto.request;

import lombok.Data;

@Data
public class ChangeUserInfoRequest {
    private String nickName;
    private String email;
}
