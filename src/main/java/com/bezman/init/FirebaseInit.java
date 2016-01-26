package com.bezman.init;

import com.bezman.Reference.Reference;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class FirebaseInit implements IInit {

    @Override
    public void init() {
        Reference.firebase = new Firebase("https://devwars-tv.firebaseio.com");

        Reference.firebase.authWithCustomToken(Reference.getEnvironmentProperty("firebaseToken"), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

            }
        });
    }
}
