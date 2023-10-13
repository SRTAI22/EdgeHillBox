package com.cloudedge.app.Webserver;

import java.util.HashMap;
import java.util.Map;

class ClientAuthenticator {
    // Auth handshake methods
    boolean check_credentials(String username, String password) {
        // code to check the credentials on the credentials file
        Map<String, String> cred = new HashMap<>();

        cred.put("Nelio", "password"); // manually adding creds

        // test retrival and passing
        if (cred.containsKey(username)) {
            if (cred.get(username).equals(password)) {
                return true;
            }
        }

        return false;
    }

    boolean append_new_user(String username, String password) {
        // code to add new users to credentials file
        return true;
    }

}