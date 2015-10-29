package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConnectedAccount extends BaseModel {

    private int id;

    private String username, provider;

    @JsonIgnore
    private User user;

    @HibernateDefault("0")
    private Boolean disconnected;

    public ConnectedAccount(User user, String provider, String username) {
        this.user = user;
        this.provider = provider;
        this.username = username;
        this.disconnected = false;
    }
}
