package com.bezman.model;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.UserPermissionFilter;
import com.bezman.exclusion.GsonExclude;
import com.bezman.jackson.serializer.UserPermissionSerializer;
import com.bezman.service.Security;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.persistence.PostLoad;
import java.util.*;

/**
 * Created by Terence on 12/22/2014.
 */
@JsonSerialize(using = UserPermissionSerializer.class)
public class User extends BaseModel
{

    public enum Role
    {
        NONE, PENDING, USER, BLOGGER, ADMIN
    }

    private int id;

    private Rank rank;

    private Rank nextRank;

    private String username;

    @UserPermissionFilter
    private String email;

    private String provider;

    @JsonIgnore
    private String password;

    @JsonIgnore
    public UserSession session;

    private UserReset passwordReset;

    private Ranking ranking;

    @JsonIgnore
    private EmailConfirmation emailConfirmation;

    private Role role;

    private Set<Integer> appliedGames;

    @UserPermissionFilter
    private Set<ConnectedAccount> connectedAccounts;

    @JsonIgnore
    private Set<Activity> activityLog;

    @JsonIgnore
    private Set<Badge> badges;

    @UserPermissionFilter
    private String providerID;

    private Integer referredUsers;

    private Integer avatarChanges;

    private Integer gamesPlayed = 0;
    private Integer gamesWon = 0;
    private Integer gamesLost = 0;

    private Integer gameStreak;

    private Warrior warrior;

    private String location, url, company;

    private Integer usernameChanges;

    private Integer score;

    private Boolean veteran;

    private Integer bettingBitsEarned;

    private Integer gamesWatched;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRole()
    {
        return role == null ? "PENDING" : role.toString();
    }

    public void setRole(String role)
    {
        this.role = Role.valueOf(role == null ? "PENDING" : role);
    }

    public UserSession getSession()
    {
        return session;
    }

    public void setSession(UserSession session)
    {
        this.session = session;
    }

    public String getUsername()
    {
        return username.replace(" ", "_");
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Ranking getRanking()
    {
        return ranking;
    }

    public void setRanking(Ranking ranking)
    {
        this.ranking = ranking;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    @JsonIgnore
    public Set<Integer> getAppliedGames()
    {
        return appliedGames;
    }

    public void setAppliedGames(Set<Integer> appliedGames)
    {
        this.appliedGames = appliedGames;
    }

    public Set<ConnectedAccount> getConnectedAccounts()
    {
        return connectedAccounts;
    }

    public void setConnectedAccounts(Set<ConnectedAccount> connectedAccounts)
    {
        this.connectedAccounts = connectedAccounts;
    }

    public UserReset getPasswordReset()
    {
        return passwordReset;
    }

    public void setPasswordReset(UserReset passwordReset)
    {
        this.passwordReset = passwordReset;
    }

    public String getProviderID()
    {
        return providerID;
    }

    public void setProviderID(String providerID)
    {
        this.providerID = providerID;
    }

    @JsonIgnore
    public Set<Activity> getActivityLog()
    {
        return activityLog;
    }

    public void setActivityLog(Set<Activity> activityLog)
    {
        this.activityLog = activityLog;
    }

    public Integer getReferredUsers()
    {
        return referredUsers == null ? 0 : referredUsers;
    }

    public void setReferredUsers(Integer referredUsers)
    {
        this.referredUsers = referredUsers;
    }

    public Rank getRank()
    {
        return rank;
    }

    public void setRank(Rank rank)
    {
        this.rank = rank;
    }

    public Integer getAvatarChanges()
    {
        return avatarChanges == null ? 1 : avatarChanges;
    }

    public void setAvatarChanges(Integer avatarChanges)
    {
        this.avatarChanges = avatarChanges;
    }

    public Warrior getWarrior()
    {
        return warrior;
    }

    public void setWarrior(Warrior warrior)
    {
        this.warrior = warrior;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public Integer getUsernameChanges()
    {
        return usernameChanges == null ? (this.provider == null ? 0 : 1) : usernameChanges;
    }

    public void setUsernameChanges(Integer usernameChanges)
    {
        this.usernameChanges = usernameChanges;
    }

    public Boolean getVeteran()
    {
        return veteran == null ? false : veteran;
    }

    public void setVeteran(Boolean veteran)
    {
        this.veteran = veteran;
    }

    public Integer getScore()
    {
        return score;
    }

    public void setScore(Integer score)
    {
        this.score = score;
    }

    public EmailConfirmation getEmailConfirmation()
    {
        return emailConfirmation;
    }

    public void setEmailConfirmation(EmailConfirmation emailConfirmation)
    {
        this.emailConfirmation = emailConfirmation;
    }

    @JsonIgnore
    public Set<Badge> getBadges()
    {
        return badges;
    }

    public void setBadges(Set<Badge> badges)
    {
        this.badges = badges;
    }

    public Integer getBettingBitsEarned()
    {
        return bettingBitsEarned == null ? 0 : bettingBitsEarned;
    }

    public void setBettingBitsEarned(Integer bettingBitsEarned)
    {
        this.bettingBitsEarned = bettingBitsEarned;
    }

    public Integer getGamesWatched()
    {
        return gamesWatched == null ? 0 : gamesWatched;
    }

    public void setGamesWatched(Integer gamesWatched)
    {
        this.gamesWatched = gamesWatched;
    }

    @JsonIgnore
    public boolean isNative()
    {
        return this.getProvider().equals("");
    }

    public void logout()
    {
        Session hibernateSession = DatabaseManager.getSession();
        hibernateSession.beginTransaction();

        hibernateSession.delete(this.session);

        hibernateSession.getTransaction().commit();
        hibernateSession.close();
    }

    public String newSession()
    {
        Session hibernateSession = DatabaseManager.getSession();
        hibernateSession.beginTransaction();

        Query query = hibernateSession.createQuery("delete from UserSession where id = :id");
        query.setInteger("id", this.getId());

        query.executeUpdate();

        String token = Util.randomText(64);

        UserSession userSession = new UserSession();
        userSession.setId(this.getId());
        userSession.setSessionID(token);

        hibernateSession.save(userSession);

        hibernateSession.getTransaction().commit();
        hibernateSession.close();

        return token;
    }

    @PostLoad
    public void performCalculatedProperties()
    {
        Session session = DatabaseManager.getSession();

        Query gamesQuery = session.createQuery("select count(*) from Player p where p.user.id = :id");
        gamesQuery.setInteger("id", id);

        this.gamesPlayed = ((Long) DatabaseUtil.getFirstFromQuery(gamesQuery)).intValue();

        Query gamesWonQuery = session.createQuery("select count(*) from Player p where p.user.id = :id and p.team.win = true and p.team.game.done = true");
        gamesWonQuery.setInteger("id", id);

        this.gamesWon = ((Long) DatabaseUtil.getFirstFromQuery(gamesWonQuery)).intValue();

        Query gamesLostQuery = session.createQuery("select count(*) from Player p where p.user.id = :id and p.team.win = false and p.team.game.done = true");
        gamesLostQuery.setInteger("id", id);

        this.gamesLost = ((Long) DatabaseUtil.getFirstFromQuery(gamesLostQuery)).intValue();

        if(this.getRanking() != null)
        {

            Query rankQuery = session.createQuery("from Rank r where r.xpRequired <= :xp order by r.xpRequired desc");
            rankQuery.setInteger("xp", this.getRanking().getXp().intValue());
            rankQuery.setMaxResults(1);

            this.rank = (Rank) DatabaseUtil.getFirstFromQuery(rankQuery);

            this.nextRank = (Rank) session.get(Rank.class, this.rank == null ? 1 : this.rank.getLevel() + 1);
        }

        session.close();
    }

    public boolean canBuyItem(ShopItem item)
    {
        return this.getRanking().getPoints() >= item.getPrice() && this.getRank().getLevel() >= item.getRequiredLevel();
    }

    public void purchaseItem(ShopItem item)
    {
        this.getRanking().setPoints(this.getRanking().getPoints() - item.getPrice());

        Activity activity = new Activity(this, this, "Purchased : " + item.getName(), -1 * item.getPrice(), 0);
        DatabaseUtil.saveObjects(false, activity);
        DatabaseUtil.updateObjects(false, this.getRanking());
    }

    public List<Badge> tryAllBadges()
    {
        List<Badge> badgesToAward = new ArrayList<>();

        if (this.getEmailConfirmation() == null)
        {
            badgesToAward.add(Badge.badgeForName("Authentic"));
        }

        if (this.getConnectedAccounts().size() > 0)
        {
            badgesToAward.add(Badge.badgeForName("Making Links"));
        }

        if (this.getConnectedAccounts().size() >= 5)
        {
            badgesToAward.add(Badge.badgeForName("Full Coverage"));
        }

        if (this.getRanking().getPoints() >= 5000)
        {
            badgesToAward.add(Badge.badgeForName("Feed The Pig"));
        }

        if (this.getRanking().getPoints() >= 25000)
        {
            badgesToAward.add(Badge.badgeForName("Penny-Pincher"));
        }

        if(this.getBettingBitsEarned() > 10000)
        {
            badgesToAward.add(Badge.badgeForName("High Roller"));
        }

        Session session = DatabaseManager.getSession();

        Query allObjectivesQuery = session.createQuery("select player.user from Player player where player.user.id = :id AND SIZE(player.team.completedObjectives) = size(player.team.game.objectives) AND size(player.team.game.objectives) > 0 ");
        allObjectivesQuery.setInteger("id", this.getId());

        User user = (User) allObjectivesQuery.uniqueResult();

        session.close();

        if (user != null)
        {
            badgesToAward.add(Badge.badgeForName("Ace High"));
        }

        if(this.gamesWon > 0)
        {
            badgesToAward.add(Badge.badgeForName("Beginner's Luck"));
        }

        if(this.gamesWon >= 5)
        {
            badgesToAward.add(Badge.badgeForName("Victorious"));
        }

        if (this.gamesWon >= 10)
        {
            badgesToAward.add(Badge.badgeForName("Hotshot"));
        }

        if (this.gamesWon >= 25)
        {
            badgesToAward.add(Badge.badgeForName("Steamroller"));
        }

        if (this.gamesWon > 0)
        {
            badgesToAward.add(Badge.badgeForName("First Timer"));
        }

        if (this.gamesWon >= 5)
        {
            badgesToAward.add(Badge.badgeForName("Hobbyist"));
        }

        if (this.gamesWon >= 25)
        {
            badgesToAward.add(Badge.badgeForName("Biggest Fan"));
        }

        if (this.gamesWon >= 50)
        {
            badgesToAward.add(Badge.badgeForName("Obsessed"));
        }

        session = DatabaseManager.getSession();

        Query playersWonQuery = session.createQuery("from Player player where player.user.id = :id order by player.team.game.timestamp desc");
        playersWonQuery.setInteger("id", this.getId());

        List<Player> playersWon = playersWonQuery.list();

        for(int i = 0; i < playersWon.size(); i++)
        {
            if (i < playersWon.size() - 2)
            {
                Player firstPlayer = playersWon.get(i);
                Player secondPlayer = playersWon.get(i + 1);
                Player thirdPlayer = playersWon.get(i + 2);


                System.out.println(firstPlayer.getTeam().getGame().getId());
                System.out.println(secondPlayer.getTeam().getGame().getId());
                System.out.println(thirdPlayer.getTeam().getGame().getId());

                if (firstPlayer.getTeam().isWin() && secondPlayer.getTeam().isWin() && thirdPlayer.getTeam().isWin())
                {
                    badgesToAward.add(Badge.badgeForName("Hot Streak"));
                }
            }
        }

        session.close();

        if (this.getWarrior() != null && this.getWarrior().getDob() != null)
        {
            Calendar dob = Calendar.getInstance();
            dob.setTime(this.getWarrior().getDob());

            Calendar today = Calendar.getInstance();
            today.setTime(new Date());

            if (dob.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                badgesToAward.add(Badge.badgeForName("Cake Day"));
            }
        }

        session = DatabaseManager.getSession();

        Query pastPlayersQuery = session.createQuery("from Player p where p.user.id = :id order by p.team.game.timestamp desc");
        pastPlayersQuery.setInteger("id", this.getId());

        List<Player> pastPlayers = pastPlayersQuery.list();

        int streak = 0;

        for(Player player : pastPlayers)
        {
            if (player.getTeam().isWin())
            {
                streak++;
            } else
            {
                break;
            }
        }

        this.gameStreak = streak;

        session.close();

        return badgesToAward;
    }

    public boolean awardBadgeForName(String name)
    {
        return this.awardBadge(Badge.badgeForName(name));
    }

    public boolean awardBadge(Badge badge)
    {
        if(!this.hasBadge(badge))
        {
            this.getRanking().addPoints(badge.getBitsAwarded());
            this.getRanking().addXP(badge.getXpAwarded());

            this.getBadges().add(badge);

            return true;
        }

        return false;
    }

    public boolean hasBadge(Badge badge)
    {
        for (Badge currentBadge : this.getBadges())
        {
            if (currentBadge.getId() == badge.getId()) return true;
        }

        return false;
    }
}
