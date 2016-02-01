package com.bezman.model;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.HibernateDefault;
import com.bezman.annotation.PreFlush;
import com.bezman.annotation.UserPermissionFilter;
import com.bezman.init.DatabaseManager;
import com.bezman.jackson.serializer.UserPermissionSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.persistence.PostLoad;
import java.util.*;

@JsonSerialize(using = UserPermissionSerializer.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User extends BaseModel {

    public enum Role {
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

    @JsonIgnore
    public String resetKey;

    private Ranking ranking;

    @JsonIgnore
    private EmailConfirmation emailConfirmation;

    private Role role;

    @JsonIgnore
    private Set<UserTeam> ownedTeams;

    @JsonIgnore
    private UserTeam ownedTeam;

    @JsonIgnore
    private UserTeam team;

    private Set<Integer> appliedGames;

    @UserPermissionFilter
    private Set<ConnectedAccount> connectedAccounts;

    @JsonIgnore
    private Set<Activity> activityLog;

    @JsonIgnore
    private Set<Badge> badges;

    @UserPermissionFilter
    private String providerID;

    @HibernateDefault("5")
    private Integer referredUsers;

    @HibernateDefault("/assets/img/default-avatar.png")
    private String avatarURL;

    private Integer gamesPlayed = 0;
    private Integer gamesWon = 0;
    private Integer gamesLost = 0;

    private Integer gameStreak;

    private Warrior warrior;

    private String location, url, company;

    @HibernateDefault("0")
    private Integer score;

    private Boolean veteran;

    @HibernateDefault("0")
    private Integer bettingBitsEarned;

    @HibernateDefault("0")
    private Integer gamesWatched;

    @HibernateDefault
    private UserInventory inventory;

    public User() {}

    @JsonIgnore
    public boolean isNative() {
        return this.getProvider() == null || this.getProvider().isEmpty();
    }

    public UserTeam getMyTeam() {
        UserTeam userTeam = this.getTeam();

        return userTeam.getOwner() == null ? null : userTeam;
    }

    public String newSession() {
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
    public void performCalculatedProperties() {
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

        if (this.getOwnedTeams() != null) {
            Optional<UserTeam> ownedTeamOptional = this.getOwnedTeams().stream().findFirst();
            if (ownedTeamOptional.isPresent()) {
                this.setOwnedTeam(ownedTeamOptional.get());
            }
        }

        session.close();
    }

    public boolean canBuyItem(ShopItem item) {
        return this.getRanking().getPoints() >= item.getPrice() && this.rank.getLevel() >= item.getRequiredLevel();
    }

    public List<Badge> tryAllBadges() {
        List<Badge> badgesToAward = new ArrayList<>();

        if (this.getEmailConfirmation() == null) {
            badgesToAward.add(Badge.badgeForName("Authentic"));
        }

        if (this.getConnectedAccounts().size() > 0) {
            badgesToAward.add(Badge.badgeForName("Making Links"));
        }

        if (this.getConnectedAccounts().size() >= 5) {
            badgesToAward.add(Badge.badgeForName("Full Coverage"));
        }

        if (this.getRanking().getPoints() >= 5000) {
            badgesToAward.add(Badge.badgeForName("Feed The Pig"));
        }

        if (this.getRanking().getPoints() >= 25000) {
            badgesToAward.add(Badge.badgeForName("Penny-Pincher"));
        }

        if (this.getBettingBitsEarned() > 10000) {
            badgesToAward.add(Badge.badgeForName("High Roller"));
        }

        Session session = DatabaseManager.getSession();

        Query allObjectivesQuery = session.createQuery("select player.user from Player player where player.user.id = :id AND SIZE(player.team.completedObjectives) = size(player.team.game.objectives) AND size(player.team.game.objectives) > 0 ");
        allObjectivesQuery.setInteger("id", this.getId());

        User user = (User) allObjectivesQuery.uniqueResult();

        session.close();

        if (user != null) {
            badgesToAward.add(Badge.badgeForName("Ace High"));
        }

        if (this.gamesWon > 0) {
            badgesToAward.add(Badge.badgeForName("Beginner's Luck"));
        }

        if (this.gamesWon >= 5) {
            badgesToAward.add(Badge.badgeForName("Victorious"));
        }

        if (this.gamesWon >= 10) {
            badgesToAward.add(Badge.badgeForName("Hotshot"));
        }

        if (this.gamesWon >= 25) {
            badgesToAward.add(Badge.badgeForName("Steamroller"));
        }

        if (this.gamesWon > 0) {
            badgesToAward.add(Badge.badgeForName("First Timer"));
        }

        if (this.gamesWon >= 5) {
            badgesToAward.add(Badge.badgeForName("Hobbyist"));
        }

        if (this.gamesWon >= 25) {
            badgesToAward.add(Badge.badgeForName("Biggest Fan"));
        }

        if (this.gamesWon >= 50) {
            badgesToAward.add(Badge.badgeForName("Obsessed"));
        }

        if (this.gamesWatched >= 1) {
            badgesToAward.add(Badge.badgeForName("First Timer"));
        }

        if (this.gamesWatched >= 5) {
            badgesToAward.add(Badge.badgeForName("Hobbyist"));
        }

        if (this.gamesWatched >= 25) {
            badgesToAward.add(Badge.badgeForName("Biggest Fan"));
        }

        if (this.gamesWatched >= 50) {
            badgesToAward.add(Badge.badgeForName("Obsessed"));
        }

        session = DatabaseManager.getSession();

        Query playersWonQuery = session.createQuery("from Player player where player.user.id = :id order by player.team.game.timestamp desc");
        playersWonQuery.setInteger("id", this.getId());

        List<Player> playersWon = playersWonQuery.list();

        for (int i = 0; i < playersWon.size(); i++) {
            if (i < playersWon.size() - 2) {
                Player firstPlayer = playersWon.get(i);
                Player secondPlayer = playersWon.get(i + 1);
                Player thirdPlayer = playersWon.get(i + 2);


                System.out.println(firstPlayer.getTeam().getGame().getId());
                System.out.println(secondPlayer.getTeam().getGame().getId());
                System.out.println(thirdPlayer.getTeam().getGame().getId());

                if (firstPlayer.getTeam().isWin() && secondPlayer.getTeam().isWin() && thirdPlayer.getTeam().isWin()) {
                    badgesToAward.add(Badge.badgeForName("Hot Streak"));
                }
            }
        }

        session.close();

        if (this.getWarrior() != null && this.getWarrior().getDob() != null) {
            Calendar dob = Calendar.getInstance();
            dob.setTime(this.getWarrior().getDob());

            Calendar today = Calendar.getInstance();
            today.setTime(new Date());

            if (dob.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                badgesToAward.add(Badge.badgeForName("Cake Day"));
            }
        }

        session = DatabaseManager.getSession();

        Query pastPlayersQuery = session.createQuery("from Player p where p.user.id = :id order by p.team.game.timestamp desc");
        pastPlayersQuery.setInteger("id", this.getId());

        List<Player> pastPlayers = pastPlayersQuery.list();

        int streak = 0;

        for (Player player : pastPlayers) {
            if (player.getTeam().isWin()) {
                streak++;
            } else {
                break;
            }
        }

        this.gameStreak = streak;

        session.close();

        return badgesToAward;
    }

    public boolean awardBadgeForName(String name) {
        return this.awardBadge(Badge.badgeForName(name));
    }

    public boolean awardBadge(Badge badge) {
        if (!this.hasBadge(badge)) {
            this.getRanking().addPoints(badge.getBitsAwarded());
            this.getRanking().addXP(badge.getXpAwarded());

            this.getBadges().add(badge);

            return true;
        }

        return false;
    }

    public boolean hasBadge(Badge badge) {
        for (Badge currentBadge : this.getBadges()) {
            if (currentBadge.getId() == badge.getId()) return true;
        }

        return false;
    }

    @PreFlush
    public void preFlush() {
        this.getInventory().setUser(this);
    }

    @PostLoad
    public void postLoad() {
        Session session = DatabaseManager.getSession();

        if (this.getRanking() != null) {
            this.rank = (Rank) session.createCriteria(Rank.class)
                .add(Restrictions.le("xpRequired", this.getRanking().getXp().intValue()))
                .addOrder(Order.desc("xpRequired"))
                .setMaxResults(1)
                .uniqueResult();

            this.nextRank = (Rank) session.get(Rank.class, this.rank == null ? 1 : this.rank.getLevel() + 1);
        }

        session.close();
    }
}
