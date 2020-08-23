package com.yoga.spendanalyser.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

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
}
