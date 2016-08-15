package com.bezman.init;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.firebase.Firebase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FirebaseInit implements IInit {

    @Override
    public void init() {
        if (Reference.firebase == null) {
            String url = Reference.getEnvironmentProperty("firebaseUrl");
            String token = Reference.getEnvironmentProperty("firebaseToken");

            Reference.firebase = new Firebase(url, token);
        }
    }
}
