package com.app.whatsApp.chat;

import com.app.whatsApp.user.UserDto;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    Chat save(Chat entity);


    Optional<Chat> findById(Integer integer);


    void deleteById(Integer integer);


    List<Chat> findAll();

    List<Chat> findBySenderAndReceiver(Integer senderId, Integer receiverId);

    List<UserDto> findChatForUser(Integer userId);

}
