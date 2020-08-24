package com.yoga.spendanalyser.user.api.external;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SmsDto implements Serializable {

    private long mobileNumber;
    private String message;
}
