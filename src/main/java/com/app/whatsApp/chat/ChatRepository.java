package com.app.whatsApp.chat;

import com.app.whatsApp.user.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("SELECT c FROM Chat c WHERE " +
            "(c.sender.id=:senderId AND c.receiver.id=:receiverId) OR " +
            "(c.sender.id=:receiverId AND c.receiver.id=:senderId) " +
            "ORDER BY c.createdAt")
    List<Chat> findBySenderAndReceiver(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

    @Query("Select DISTINCT new com.app.whatsApp.user.UserDto(" +
            " case when c.sender.id=:userId then c.receiver.id  else c.sender.id end as id, " +
            " case when c.sender.id=:userId then c.receiver.name  else c.sender.name end as name, " +
            " case when c.sender.id=:userId then c.receiver.phone  else c.sender.phone end as phone, " +
            " case when c.sender.id=:userId then c.receiver.imgUrl  else c.sender.imgUrl end as imgUrl) " +
            " from Chat c WHERE c.sender.id=:userId OR c.receiver.id=:userId")
    List<UserDto> findChatForUser(Integer userId);
}
