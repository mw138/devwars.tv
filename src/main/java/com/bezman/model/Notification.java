package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseModel
{

    private int id;

    @JsonIgnore
    private User user;

    private String notificationText;

    private boolean hasRead;

    public Notification(User user, String notificationText, boolean hasRead)
    {
        this.user = user;
        this.notificationText = notificationText;
        this.hasRead = hasRead;
    }
}
