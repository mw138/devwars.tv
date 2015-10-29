package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Contact extends BaseModel {

    private int id;

    private String text, type, name, email;

    @HibernateDefault
    private Date timestamp;

    public Contact(String name, String email, String type, String text) {
        this.setName(name);
        this.setEmail(email);
        this.setType(type);
        this.setText(text);
    }
}
