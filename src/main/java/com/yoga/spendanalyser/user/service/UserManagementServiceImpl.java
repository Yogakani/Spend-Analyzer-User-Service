package com.yoga.spendanalyser.user.service;

import com.yoga.spendanalyser.user.api.external.OtpRequest;
import com.yoga.spendanalyser.user.api.request.CreateUserRequest;
import com.yoga.spendanalyser.user.api.response.GetUserResponse;
import com.yoga.spendanalyser.user.api.response.Status;
import com.yoga.spendanalyser.user.model.User;
import com.yoga.spendanalyser.user.repository.UserRepostiory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;

import static com.yoga.spendanalyser.user.mapper.UserMapper.convert;

@Component
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepostiory userRepostiory;

    @Autowired
    private Environment environment;

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

    @Override
    public boolean persistOtp(String mobileNumber, String otp) {
        OtpRequest otpRequest = new OtpRequest().setMobileNumber(Long.parseLong(mobileNumber)).setOtp(otp);
        String url = environment.getProperty("common.baseUrl") + "/common/persistOtp";
        Status status = new RestTemplate().postForObject(url, otpRequest, Status.class);
        if(status.getStatus() == 200) {
            return true;
        }
        return false;
    }
}
