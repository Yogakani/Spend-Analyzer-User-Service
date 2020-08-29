package com.yoga.spendanalyser.user.service;

import com.yoga.spendanalyser.user.api.request.CreateUserRequest;
import com.yoga.spendanalyser.user.api.response.GetUserResponse;

public interface UserManagementService {

    String generateOtp(final String mobileNum);
    String createUser(CreateUserRequest createUserRequest);
    GetUserResponse getUser(final long mobileNumber);
}
