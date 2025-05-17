package com.app.whatsApp.chat;

import com.app.whatsApp.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatImpService implements ChatService {
    private final ChatRepository repository;

    @Override
    @Transactional
    public Chat save(Chat entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Chat> findById(Integer integer) {
        return Optional.of(repository.findById(integer).orElseThrow(() -> new RuntimeException("we cannot found the doc " + integer)));
    }

    @Override
    @Transactional
    public void deleteById(Integer integer) {
        repository.findById(integer);
    }


    @Override
    public List<Chat> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Chat> findBySenderAndReceiver(
            Integer senderId, Integer receiverId) {
        return repository.findBySenderAndReceiver(senderId, receiverId);
    }

    @Override
    public List<UserDto> findChatForUser(Integer userId) {
        return repository.findChatForUser(userId);
    }


}
