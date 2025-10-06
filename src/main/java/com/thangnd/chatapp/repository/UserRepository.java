package com.thangnd.chatapp.repository;

import com.thangnd.chatapp.entity.Status;
import com.thangnd.chatapp.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository  extends MongoRepository<User, String> {
    List<User> findAllByStatus(Status status);
}
