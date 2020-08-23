package com.yoga.spendanalyser.user.api.controller;

import com.yoga.spendanalyser.user.api.request.PreAuthRequest;
import com.yoga.spendanalyser.user.api.response.PreAuthResponse;
import com.yoga.spendanalyser.user.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/v1.0/user")
@Profile({"dev", "production"})
@Slf4j
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @PostMapping(value = "/preAuth")
    public ResponseEntity<?> preAuthentication(@RequestBody PreAuthRequest preAuthRequest) {
        if(preAuthRequest.getMobileNumber() != null && preAuthRequest.getMobileNumber() > 0) {
            String otp = userManagementService.generateOtp(String.valueOf(preAuthRequest.getMobileNumber()));
            PreAuthResponse preAuthResponse = (PreAuthResponse) new PreAuthResponse()
                                                        .setOtp(otp)
                                                        .setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(preAuthResponse, HttpStatus.OK);
        } else {
            log.error("Mobile Number should be entered");
            PreAuthResponse preAuthResponse = (PreAuthResponse) new PreAuthResponse()
                                                            .setStatus(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(preAuthResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
