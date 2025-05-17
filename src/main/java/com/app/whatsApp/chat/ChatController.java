package com.app.whatsApp.chat;

import com.app.whatsApp.user.User;
import com.app.whatsApp.user.UserDto;
import com.app.whatsApp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService service;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> save(
            @AuthenticationPrincipal User user,
            @RequestBody
            Chat entity) {

        entity.setSender(user);
        if (entity.getReceiver() == null) {
            throw new RuntimeException("the receiver does not exist");
        }
        User receiver = userRepository.findUserByPhone(entity.getReceiver()
                        .getPhone())
                .orElseThrow(() ->
                        new UsernameNotFoundException("the receiver does not exist"));

        entity.setReceiver(receiver);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.save(entity));

    }

    @GetMapping("/{Id}")
    public Optional<Chat> findById(@PathVariable Integer Id) {
        return service.findById(Id);
    }

    @GetMapping("/conversation")
    public List<ChatDto> findBySender_PhoneNumberAndReceive(
            @AuthenticationPrincipal
            User user
            , @RequestParam String receiverPhone) {
        Integer senderId = user.getId();
        String phone = receiverPhone.startsWith("+") ? receiverPhone : "+" + receiverPhone;
        Integer receiverId = userRepository.findUserByPhone(phone)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Receiver not found with phone: " + receiverPhone)).getId();
        List<Chat> chats = service.findBySenderAndReceiver(senderId, receiverId);
        List<ChatDto> chatDtoList = chats.stream()
                .map(a ->
                        ChatDto.builder()
                                .id(a.getId())
                                .time(new Time(a.getCreatedAt().getTime()))
                                .message(a.getMessage())
                                .sender(setUserDto(a.getSender()))
                                .receiver(setUserDto(a.getReceiver()))
                                .build()).toList();
        return chatDtoList;
    }

    @GetMapping("/me")
    public List<UserDto> findChatForUser(
            @AuthenticationPrincipal
            User user) {

        return service.findChatForUser(user.getId());
    }


    @DeleteMapping("/{Id}")
    public void deleteById(Integer Id) {
        service.deleteById(Id);
    }


    @GetMapping
    public List<ChatDto> findAll() {

        List<Chat> chats = service.findAll();
        List<ChatDto> chatDtoList = chats.stream()
                .map(a ->
                        ChatDto.builder()
                                .id(a.getId())
                                .time(new Time(a.getCreatedAt().getTime()))
                                .message(a.getMessage())
                                .sender(setUserDto(a.getSender()))
                                .receiver(setUserDto(a.getReceiver()))
                                .build()).toList();
        return chatDtoList;
    }

    public static UserDto setUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .imgUrl(user.getImgUrl())
                .build();
    }
}
