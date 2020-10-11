package com.yoga.spendanalyser.user.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoga.spendanalyser.user.api.external.SmsDto;
import com.yoga.spendanalyser.user.api.request.AuditRequest;
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

import java.time.LocalDate;
import java.time.LocalTime;


@RestController
@RequestMapping(value = "/api/v1.0/user")
@Profile({"dev", "production"})
@Slf4j
@CrossOrigin("*")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @PostMapping(value = "/preAuth")
    public ResponseEntity<?> preAuthentication(@RequestBody PreAuthRequest preAuthRequest) throws JsonProcessingException {
        if(preAuthRequest.getMobileNumber() != null && preAuthRequest.getMobileNumber() > 0) {
            String otp = userManagementService.generateOtp(String.valueOf(preAuthRequest.getMobileNumber()));

            boolean status = userManagementService.persistOtp(String.valueOf(preAuthRequest.getMobileNumber()), otp);

            persistAuditLogs(prepareOtpAudit(String.valueOf(preAuthRequest.getMobileNumber())));

            if(status) {
                PreAuthResponse preAuthResponse = (PreAuthResponse) new PreAuthResponse()
                        .setOtp(otp)
                        .setStatus(HttpStatus.OK.value());
                return new ResponseEntity<>(preAuthResponse, HttpStatus.OK);
            } else {
                log.error("Otp persist is not successfull");
                PreAuthResponse preAuthResponse = (PreAuthResponse) new PreAuthResponse()
                        .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return new ResponseEntity<>(preAuthResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.error("Mobile Number should be entered");
            PreAuthResponse preAuthResponse = (PreAuthResponse) new PreAuthResponse()
                                                            .setStatus(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(preAuthResponse, HttpStatus.BAD_REQUEST);
        }
    }

    private AuditRequest prepareOtpAudit(String mobileNumber) throws JsonProcessingException {
        return new AuditRequest()
                .setMobileNumber(mobileNumber)
                .setEvent("Otp Generated successfully");
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

    @PutMapping(value = "/audit")
    public ResponseEntity<?> auditLogin(@RequestBody AuditRequest auditRequest) throws JsonProcessingException {
        persistAuditLogs(auditRequest);
        Status status = new Status().setStatus(HttpStatus.OK.value());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    private void persistAuditLogs(AuditRequest auditRequest) throws JsonProcessingException {
        String time = LocalDate.now().atTime(LocalTime.now()).toString();
        auditRequest.setTime(time);
        rabbitTemplate.convertAndSend("user.exchange", "user.audit.rk", new ObjectMapper().writeValueAsString(auditRequest));
    }

}
