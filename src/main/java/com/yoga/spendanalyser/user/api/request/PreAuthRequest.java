package com.yoga.spendanalyser.user.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class PreAuthRequest implements Serializable {

    private Long mobileNumber;

}
