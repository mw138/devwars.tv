package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailConfirmation extends BaseModel {
    private int id;

    private String uid;
}
