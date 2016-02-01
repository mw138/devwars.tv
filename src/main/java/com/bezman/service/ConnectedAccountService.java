package com.bezman.service;

import com.bezman.Reference.DevBits;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Activity;
import com.bezman.model.ConnectedAccount;
import com.bezman.model.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectedAccountService {

    private UserService userService;

    @Autowired
    public ConnectedAccountService(UserService userService) {
        this.userService = userService;
    }

    public void connectProviderToUser(User user, String username, String provider, String providerID) {

        ConnectedAccount connectedAccount = userService.connectedAccountForProvider(user, provider);

        if (connectedAccount == null) {
            this.addNewProvider(user, username, provider, providerID);
        } else {
            this.reconnectProvider(user, username, provider, providerID);
        }

    }

    public void addNewProvider(User user, String username, String provider, String providerID) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        ConnectedAccount connectedAccount = new ConnectedAccount(user, provider, providerID, username);
        Activity activity = new Activity(user, user, "Connected your " + provider + " account", DevBits.ACCOUNT_CONNECTION, 0);

        user.getRanking().addPoints(DevBits.ACCOUNT_CONNECTION);

        session.save(connectedAccount);
        session.save(activity);
        session.merge(user);

        session.getTransaction().commit();
        session.close();
    }

    public void reconnectProvider(User user, String username, String provider, String providerID) {
        ConnectedAccount connectedAccount = userService.connectedAccountForProvider(user, provider);

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        connectedAccount.setDisconnected(false);
        connectedAccount.setProviderID(providerID);
        connectedAccount.setUsername(username);

        session.merge(connectedAccount);

        session.getTransaction().commit();
        session.close();
    }

    public void disconnectProviderFromUser(String provider, User user) {
        ConnectedAccount connectedAccount = this.userService.connectedAccountForProvider(user, provider);

        if (connectedAccount != null) {
            Session session = DatabaseManager.getSession();
            session.beginTransaction();

            connectedAccount.setUsername(null);
            connectedAccount.setProviderID(null);
            connectedAccount.setDisconnected(true);

            session.merge(connectedAccount);

            session.getTransaction().commit();
            session.close();
        }
    }
}
