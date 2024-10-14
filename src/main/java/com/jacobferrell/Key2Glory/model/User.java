package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.Entity;

public class User {
    private String given_name;
    private String family_name;
    private String nickname;
    private String name;
    private String picture;
    private String locale;
    private String updated_at;
    private String email;
    private boolean email_verified;
    private String sub;

    public User() {
    }

    public User(String given_name, String family_name, String nickname, String name, String picture, String locale, String updated_at, String email, boolean email_verified, String sub) {
        this.given_name = given_name;
        this.family_name = family_name;
        this.nickname = nickname;
        this.name = name;
        this.picture = picture;
        this.locale = locale;
        this.updated_at = updated_at;
        this.email = email;
        this.email_verified = email_verified;
        this.sub = sub;
    }

    public String getGiven_name() {
        return given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getLocale() {
        return locale;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public String getSub() {
        return sub;
    }

    @Override
    public String toString() {
        return "User{" + "given_name=" + given_name + ", family_name=" + family_name + ", nickname=" + nickname + ", name=" + name + ", picture=" + picture + ", locale=" + locale + ", updated_at=" + updated_at + ", email=" + email + ", email_verified=" + email_verified + ", sub=" + sub + '}';
    }
}
