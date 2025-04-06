package com.devumut.DearDiary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordDto {
    private String current_password;
    private String new_password;
    private String new_password_confirm;
}
