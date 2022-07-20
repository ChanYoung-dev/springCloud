package com.example.demo.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "이메일은 두글자 이상이어야합니다")
    private String email;

    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "이름은 두글자 이상이어야합니다")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Size(min = 8, message = "비밀번호은 두글자 이상이어야합니다")
    private String pwd;
}
