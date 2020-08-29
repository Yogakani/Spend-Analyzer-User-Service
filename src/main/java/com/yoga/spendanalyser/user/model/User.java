package com.yoga.spendanalyser.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private long mobileNumber;

}
