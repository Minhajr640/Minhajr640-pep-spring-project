package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import javax.transaction.Transactional;

import com.example.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("FROM Message m WHERE m.postedBy = :postedBy")
    List<Message> getAllMessagesByAccountId(@Param("postedBy") int postedBy);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.messageId = :messageId")
    Integer deleteByIdAndReturnCount(@Param("messageId") Integer messageId);
}
