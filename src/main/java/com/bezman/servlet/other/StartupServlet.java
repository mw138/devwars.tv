package com.bezman.servlet.other;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.AngularServiceBuilder;
import com.bezman.init.DatabaseManager;
import com.bezman.init.FirebaseInit;
import com.bezman.init.IInit;
import com.bezman.init.TwitterInit;
import com.bezman.storage.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;

@Controller
@RequestMapping
public class StartupServlet {
    @Autowired
    FileStorage fileStorage;

    @PostConstruct
    public void postConstruct() {
        Reference.loadDevWarsProperties();

        Class[] initializations = new Class[]{
                FirebaseInit.class,
                DatabaseManager.class,
                TwitterInit.class
        };

        //Run all init classes
        for (Class theClass : initializations) {
            try {
                IInit iInit = (IInit) theClass.newInstance();

                iInit.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        AngularServiceBuilder.buildServicesFromPackage("com.bezman.controller", "C:\\Users\\teren\\IdeaProjects\\DevWars Maven\\target\\services\\", Reference.rootURL, "app");
    }

    @PreDestroy
    public void preDestroy() {
        try {
            Reference.connection.close();
            DatabaseManager.sessionFactory.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
