package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.bezman.init.DatabaseManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Badge extends BaseModel
{

    private int id;

    private String name, description;

    @HibernateDefault("0")
    private Integer bitsAwarded, xpAwarded, userCount;

    @JsonIgnore
    private Set<User> users;

    public Badge(String name, int bitsAwarded, int xpAwarded, String description)
    {
        this.name = name;
        this.bitsAwarded = bitsAwarded;
        this.xpAwarded = xpAwarded;
        this.description = description;
    }

    public static Badge badgeForName(String name)
    {
        Session session = DatabaseManager.getSession();

        Badge badge = (Badge) session.createCriteria(Badge.class)
                .add(Restrictions.eq("name", name))
                .uniqueResult();

        session.close();

        return badge;
    }
}
