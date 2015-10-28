package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.bezman.hibernate.validation.annotation.AlphaNumeric;
import com.bezman.init.DatabaseManager;
import com.bezman.service.UserTeamService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.validator.constraints.Length;

import javax.persistence.PostLoad;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserTeam extends BaseModel
{

    private int id;

    private User owner;

    private String avatarURL;

    @AlphaNumeric(message = "must be alphanumeric with spaces")
    @Length(min = 0, max = 24)
    private String name;

    @AlphaNumeric(spaces = false, message = "must be alphanumeric")
    @Length(min = 2, max = 4)
    private String tag;

    private Rank rank, nextRank;

    private Set<User> members;

    @JsonIgnore
    private Set<UserTeamInvite> invites;

    @JsonIgnore
    private Set<Team> gameTeams;

    @HibernateDefault("0")
    private Integer xp;

    private Long gamesWon, gamesLost;

    public UserTeam(String name, String tag, User owner)
    {
        this.name = name;
        this.tag = tag;
        this.owner = owner;

        this.members = new HashSet<>();
        this.invites = new HashSet<>();

        this.members.add(owner);
    }

    @PostLoad
    public void setRanks()
    {
        Session session = DatabaseManager.getSession();

        this.rank = (Rank) session.createCriteria(Rank.class)
                .add(Restrictions.le("xpRequired", this.getXp().intValue()))
                .addOrder(Order.desc("xpRequired"))
                .setMaxResults(1)
                .uniqueResult();

        this.nextRank = (Rank) session.get(Rank.class, this.rank == null ? 1 : this.rank.getLevel() + 1);

        session.close();
    }

    public void addXP(int xp)
    {
        this.setXp(this.getXp() + xp);
    }
}

