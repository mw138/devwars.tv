package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.ConnectedAccount;
import com.bezman.model.User;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MigrationService {

    private UserService userService;

    @Autowired
    public MigrationService(UserService userService)
    {
        this.userService = userService;
    }

    public void migrate_primary_o_auth_to_connected_account() {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        List<User> users = session.createCriteria(User.class)
                .add(Restrictions.isNotNull("provider"))
                .list();

        users.parallelStream().forEach(user -> {
            if (!userService.userHasConnectedProvider(user, user.getProvider())) {
                String cutUsername = user.getUsername().substring(0, user.getUsername().length() - 4);
                ConnectedAccount connectedAccount = new ConnectedAccount(user, user.getProvider(), user.getProviderID(), cutUsername);

                session.save(connectedAccount);

                user.setProvider(null);
                user.setProviderID(null);
            }
        });

        session.getTransaction().commit();
        session.close();
    }

}
