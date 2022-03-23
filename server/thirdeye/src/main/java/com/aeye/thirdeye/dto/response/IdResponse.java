package com.aeye.thirdeye.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 생성시 Id만 반환
 */
@Data
public class IdResponse {

    private Long id;

    public IdResponse(Long id) {
        this.id = id;
    }
}
