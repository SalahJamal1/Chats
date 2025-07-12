package com.app.whatsApp.chat.dto;

import com.app.whatsApp.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
    private Integer id;
    private String message;
    private Time time;
    private UserDto sender;
    private UserDto receiver;
}
