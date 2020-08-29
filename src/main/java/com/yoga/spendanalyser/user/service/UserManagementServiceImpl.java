package com.yoga.spendanalyser.user.service;

import com.yoga.spendanalyser.user.api.request.CreateUserRequest;
import com.yoga.spendanalyser.user.api.response.GetUserResponse;
import com.yoga.spendanalyser.user.model.User;
import com.yoga.spendanalyser.user.repository.UserRepostiory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

import static com.yoga.spendanalyser.user.mapper.UserMapper.convert;

@Component
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepostiory userRepostiory;

    /**
     * Method to generate OTP based on mobile number
     * @param mobileNum
     * @return String
     */
    @Override
    public String generateOtp(final String mobileNum) {
        char[] otp = new char[6];
        for (int i=0; i<6; i++) {
            otp[i] = mobileNum.charAt(new Random().nextInt(mobileNum.length()));
        }
        log.info("Generated OTP : {}", String.valueOf(otp));
        return String.valueOf(otp);
    }

    /**
     * Method to create user.
     * @param createUserRequest
     * @return String
     */
    @Override
    public String createUser(CreateUserRequest createUserRequest) {
        User user = convert(createUserRequest).get();
        user = userRepostiory.save(user);
        log.info("User Created with id {}", user.getId());
        return user.getId();
    }

    /**
     * Method to get User details
     * @param mobileNumber
     * @return GetUserResponse
     */
    @Override
    public GetUserResponse getUser(final long mobileNumber) {
        User user = userRepostiory.findByMobileNumber(mobileNumber);
        return user != null ? convert(user).get() : null;
    }
}
