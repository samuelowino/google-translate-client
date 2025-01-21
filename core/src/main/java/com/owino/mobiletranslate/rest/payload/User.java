package com.owino.mobiletranslate.rest.payload;

/**
 * represents  user data
 * @param username unique name for a user
 * @param email    email address of user
 * @param password
 */
public record User(String username,String email,String password) {
}
