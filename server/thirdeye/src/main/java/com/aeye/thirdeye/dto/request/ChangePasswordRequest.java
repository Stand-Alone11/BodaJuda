package com.aeye.thirdeye.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String password;
    private String passwordConfirm;
}
