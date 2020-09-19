package com.yoga.spendanalyser.user.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoga.spendanalyser.user.api.external.SmsDto;
import com.yoga.spendanalyser.user.api.request.CreateUserRequest;
import com.yoga.spendanalyser.user.api.request.PreAuthRequest;
import com.yoga.spendanalyser.user.api.response.CreateUserResponse;
import com.yoga.spendanalyser.user.api.response.GetUserResponse;
import com.yoga.spendanalyser.user.api.response.PreAuthResponse;
import com.yoga.spendanalyser.user.api.response.Status;
import com.yoga.spendanalyser.user.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(value = "/api/v1.0/user")
@Profile({"dev", "production"})
@Slf4j
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/preAuth")
    public ResponseEntity<?> preAuthentication(@RequestBody PreAuthRequest preAuthRequest) throws JsonProcessingException {
        if(preAuthRequest.getMobileNumber() != null && preAuthRequest.getMobileNumber() > 0) {
            String otp = userManagementService.generateOtp(String.valueOf(preAuthRequest.getMobileNumber()));

            rabbitTemplate.convertAndSend("user.exchange", "user.otp.rk",
                                        prepareSMSdetails(preAuthRequest.getMobileNumber(), Long.parseLong(otp)));

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

    private String  prepareSMSdetails(long mobileNumber, long otp) throws JsonProcessingException {
        SmsDto smsDto =  new SmsDto()
                .setMobileNumber(mobileNumber)
                .setMessage("Your OTP is " + otp);
        return new ObjectMapper().writeValueAsString(smsDto);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            String userId = userManagementService.createUser(createUserRequest);
            CreateUserResponse createUserResponse = (CreateUserResponse) new CreateUserResponse()
                                                            .setUserId(userId)
                                                            .setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(createUserResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while creating user {}", e.getMessage());
            CreateUserResponse createUserResponse = (CreateUserResponse) new CreateUserResponse()
                                                        .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(createUserResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = {"/get", "/firstTimeCheck"})
    public ResponseEntity<?> getUser(@RequestHeader(value = "mobileNumber", required = true) String mobileNumber) {
        GetUserResponse userResponse = userManagementService.getUser(Long.parseLong(mobileNumber));
        if(userResponse != null) {
            log.info("User Record found with id {}", userResponse.getId());
            userResponse.setFirstTimeUser(false);
            userResponse.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } else {
            log.info("User Record not found with mobile number {}", mobileNumber);
            userResponse = (GetUserResponse) new GetUserResponse()
                                        .setFirstTimeUser(true)
                                        .setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/healthCheck")
    public ResponseEntity<?> serviceCheck() {
        return new ResponseEntity<>(new Status().setStatus(HttpStatus.OK.value()), HttpStatus.OK);
    }
}
