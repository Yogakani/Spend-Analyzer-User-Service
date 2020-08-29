package com.yoga.spendanalyser.user.repository;

import com.yoga.spendanalyser.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepostiory extends MongoRepository<User, String> {

     User findByMobileNumber(long mobileNumber);
}
