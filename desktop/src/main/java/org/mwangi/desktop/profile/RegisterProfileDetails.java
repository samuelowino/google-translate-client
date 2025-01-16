package org.mwangi.desktop.profile;

public class RegisterProfileDetails extends ProfileDetails{
    private final String email;

    public RegisterProfileDetails(String username, String password, String email) {
        super(username, password);
        this.email = email;
    }


}
