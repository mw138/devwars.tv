package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSession extends BaseModel
{
    private int id;

    private String sessionID;

    @JsonIgnore
    private User user;
}
