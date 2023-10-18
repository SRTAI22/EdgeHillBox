package com.cloudedge.app.Webserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class ClientAuthenticator {
    private Map<String, String> map;

    public ClientAuthenticator() {
        map = new HashMap<>();
        // deserialize map here
        EncryptionService encryptionService = new EncryptionService();
        try {
            map = encryptionService.deserialize("Credentials.ser");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    static class CredentialsHandler {
        private Map<String, String> cred;
        EncryptionService encryptionService = new EncryptionService();

        CredentialsHandler(Map<String, String> cred) {
            this.cred = cred;
        }

        boolean credentialAvailabilityCheck(String username) {
            return !cred.containsKey(username);
        }

        boolean credentialQuery(String username, String password) {
            return cred.containsKey(username) && cred.get(username).equals(password);
        }

        void addNewUser(String username, String password) throws IOException {
            cred.put(username, password);
            encryptionService.serialize("Credentials.ser", cred);
        }
    }

    boolean checkCredentials(String username, String password) {
        CredentialsHandler credentialsHandler = new CredentialsHandler(map);
        return credentialsHandler.credentialQuery(username, password);
    }

    boolean appendNewUser(String username, String password) {
        CredentialsHandler credentialsHandler = new CredentialsHandler(map);
        if (credentialsHandler.credentialAvailabilityCheck(username)) {
            try {
                credentialsHandler.addNewUser(username, password);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
