package com.yoga.spendanalyser.user.mapper;

import com.yoga.spendanalyser.user.api.request.CreateUserRequest;
import com.yoga.spendanalyser.user.api.response.GetUserResponse;
import com.yoga.spendanalyser.user.model.User;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class UserMapper {

    public static Supplier<User> convert(CreateUserRequest createUserRequest) {
        return () -> new User()
                        .setName(createUserRequest.getName())
                        .setEmail(createUserRequest.getEmail())
                        .setMobileNumber(createUserRequest.getMobileNumber());
    }

    public static Supplier<GetUserResponse> convert(User user) {
        return () -> new GetUserResponse()
                        .setId(user.getId())
                        .setName(user.getName())
                        .setMobileNumber(user.getMobileNumber())
                        .setEmail(user.getEmail());
    }
}
