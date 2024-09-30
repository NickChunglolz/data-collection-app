package com.example.demo.entity.po;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {

    private String id;
    private String name;
    private String email;
}
