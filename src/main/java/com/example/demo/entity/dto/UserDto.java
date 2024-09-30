package com.example.demo.entity.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class UserDto {

    private UUID id;
    private String name;
    private String email;
}
