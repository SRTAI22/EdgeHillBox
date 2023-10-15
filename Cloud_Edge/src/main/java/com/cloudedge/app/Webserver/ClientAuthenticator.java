package com.cloudedge.app.Webserver;

import java.util.HashMap;
import java.util.Map;

class ClientAuthenticator {
    // declare test hash map
    private final Map<String, String> cred;

    // Constructor to initialize the hash map and add test cred
    public ClientAuthenticator() {
        this.cred = new HashMap<>();
        this.cred.put("Nelio", "password"); // manually adding creds for testing
    }

    // db/cred file handler method
    static class CredentialsHandler {
        private final Map<String, String> cred;

        CredentialsHandler(Map<String, String> cred) {
            this.cred = cred;
        }

        boolean CredentialAvailabilityCheck(String username) {
            if (cred.containsKey(username)) {
                return false;
            }

            return true;
        }

        // method to query and check credentials
        boolean CredentialQuery(String username, String password) {
            if (!cred.containsKey(username) || !cred.get(username).equals(password)) {
                return false;
            }
            return true;
        }

        // method to put new user
        void AddNewUser(String username, String password) {
            cred.put(username, password);
            System.out.println(cred);
        }
    }

    // Auth handshake methods
    boolean check_credentials(String username, String password) {
        // code to check the credentials on the credentials file

        // single CredentialsHandler instance
        CredentialsHandler credentialsHandler = new CredentialsHandler(cred);
        return credentialsHandler.CredentialQuery(username, password);
    }

    boolean append_new_user(String username, String password) {
        // code to add new users to credentials file

        CredentialsHandler credentialsHandler = new CredentialsHandler(cred);
        boolean exists = credentialsHandler.CredentialAvailabilityCheck(username);

        if (!exists) {
            return false;
        } else {
            credentialsHandler.AddNewUser(username, password);
        }
        return true;
    }
}
