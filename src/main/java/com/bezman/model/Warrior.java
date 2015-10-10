package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.bezman.annotation.PreFlush;
import com.bezman.annotation.PreFlushHibernateDefault;
import com.bezman.annotation.UserPermissionFilter;
import com.bezman.jackson.serializer.UserPermissionSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.PreUpdate;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(using = UserPermissionSerializer.class)
public class Warrior extends BaseModel
{

    private int id;

    @UserPermissionFilter(userField = "user")
    private String firstName;

    @NotEmpty
    private String favFood;

    @UserPermissionFilter(userField = "user")
    private String favTool;

    @UserPermissionFilter(userField = "user")
    @NotEmpty
    private String about;

    @NotEmpty
    private String c9Name;

    @UserPermissionFilter(userField = "user")
    private String company;

    @UserPermissionFilter(userField = "user")
    @NotEmpty
    private String location;

    @Min(1)
    @Max(5)
    private Integer htmlRate, cssRate, jsRate;

    @UserPermissionFilter(userField = "user")
    private Date dob;

    @JsonIgnore
    private User user;

    private Date updatedAt;

    public Warrior(String firstName, String favFood, String favTool, String about, String c9Name, String company, String location, Integer htmlRate, Integer cssRate, Integer jsRate, Date dob, int id)
    {
        this.firstName = firstName;
        this.favFood = favFood;
        this.favTool = favTool;
        this.about = about;
        this.c9Name = c9Name;
        this.company = company;
        this.location = location;
        this.htmlRate = htmlRate;
        this.cssRate = cssRate;
        this.jsRate = jsRate;
        this.dob = dob;
        this.id = id;
    }
}
