package com.owino.mobiletranslate.rest.controller;

import com.owino.mobiletranslate.rest.db.DatabaseManager;
import com.owino.mobiletranslate.rest.payload.User;
import com.owino.mobiletranslate.rest.response.ValidResponse;
import com.owino.mobiletranslate.rest.exception.ErrorResponse;
import com.owino.mobiletranslate.rest.validator.UserPayloadValiditor;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.UUID;
import java.util.regex.Pattern;
@Slf4j
public class UserController {

    static Argon2 argon2 = Argon2Factory.create();


    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");

   /*
    * method to register a user
    */
   public static void registerUser(Context ctx) {
       if (ctx.body().isEmpty()) {
           ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad request", "Request body required"));
           return;
       }
       UserPayloadValiditor.userRegistrationValidator(ctx);
       User userDetails = ctx.bodyAsClass(User.class);
       if (!isValidUsername(userDetails.username()) || !isValidEmail(userDetails.email()) || !isValidPassword(userDetails.password())) {
           ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad request", "Invalid username or password format. Check the accepted formats."));
           return;
       }
       try (Connection conn = DatabaseManager.getDataSource().getConnection()) {
           conn.setAutoCommit(false);
           try (PreparedStatement checkStmt = conn.prepareStatement("SELECT username, email FROM users WHERE username = ? OR email = ?")) {
               checkStmt.setString(1, userDetails.username());
               checkStmt.setString(2, userDetails.email());

               try (ResultSet rs = checkStmt.executeQuery()) {
                   if (rs.next()) {
                       String existingUsername = rs.getString("username");
                       String existingEmail = rs.getString("email");

                       if (existingUsername.equalsIgnoreCase(userDetails.username())) {
                           ctx.status(HttpStatus.CONFLICT).json(new ErrorResponse("Conflict", "Username already in use"));
                           return;
                       } else if (existingEmail.equalsIgnoreCase(userDetails.email())) {
                           ctx.status(HttpStatus.CONFLICT).json(new ErrorResponse("Conflict", "Email already in use"));
                           return;
                       }
                   }
               }
           }
           String hashedPassword = argon2.hash(2, 65536, 1, userDetails.password());
           try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {
               insertStmt.setString(1, userDetails.username());
               insertStmt.setString(2, userDetails.email());
               insertStmt.setString(3, hashedPassword);
               insertStmt.executeUpdate();

               conn.commit();
               ctx.status(HttpStatus.CREATED).json(new ValidResponse(200,"Requested accepted","User registered successfully"));

           } catch (SQLException e) {
               conn.rollback();
               throw e;
           }
       } catch (SQLException e) {

           ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Error registering user", "An error occurred processing request. You can try again after a while."));
       }
   }
     /*
     * user login
     */
   public static void loginUser(Context ctx) {
       if(ctx.body().isEmpty()) {
           ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad request","Request body required"));
           return;
       }
       UserPayloadValiditor.userLoginValidator(ctx);
       User userDetails = ctx.bodyAsClass(User.class);

       try (Connection conn = DatabaseManager.getDataSource().getConnection()) {
           conn.setAutoCommit(false);
           try (PreparedStatement pstmt = conn.prepareStatement("SELECT id, password FROM users WHERE username = ?")) {
               pstmt.setString(1, userDetails.username());
               try (ResultSet rs = pstmt.executeQuery()) {
                   if (rs.next() && argon2.verify(rs.getString("password"), userDetails.password())) {
                       int userId = rs.getInt("id");

                       java.lang.String apiKey = generateApiKey(userId, conn);
                       conn.commit();
                       ctx.json(new ValidResponse(200,"Login successful. Your API key is: ",apiKey));

                   } else {
                       ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse("Unauthorized","Invalid username or password"));

                   }
               }
           }
       } catch (SQLException e) {
           log.warn("Error during login process for user: {} {}" , userDetails.username() , e);
           ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Internal Server Error","Error logging in"));
       }
   }
     /*
     *generating api key method
     */
   private static java.lang.String generateApiKey(int userId, Connection conn) throws SQLException {
       String apiKey = UUID.randomUUID().toString();

       try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO api_keys (user_id, api_key) VALUES (?, ?)")) {
           pstmt.setInt(1, userId);
           pstmt.setString(2, apiKey);
           pstmt.executeUpdate();
       } catch (SQLException e) {
           log.warn("Error generating API key for user ID: {} {} " ,userId ,e);
           throw e;
       }

       return apiKey;
   }
     /*
     *refreshing api key very skeptic about use cases
     */
   public static void refreshApiKey(Context ctx) {
       java.lang.String oldApiKey = ctx.header("X-API-Key");
       if (oldApiKey == null || oldApiKey.trim().isEmpty()) {
           ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad request","Request body required"));
           return;
       }

       try (Connection conn = DatabaseManager.getDataSource().getConnection()) {
           conn.setAutoCommit(false);

           try (PreparedStatement selectStmt = conn.prepareStatement("SELECT user_id FROM api_keys WHERE api_key = ?");
                PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM api_keys WHERE api_key = ?")) {
               selectStmt.setString(1, oldApiKey);
               try (ResultSet rs = selectStmt.executeQuery()) {
                   if (rs.next()) {
                       int userId = rs.getInt("user_id");
                       String newApiKey = generateApiKey(userId, conn);
                       deleteStmt.setString(1, oldApiKey);
                       deleteStmt.executeUpdate();
                       conn.commit();
                       ctx.json(new ValidResponse(200,"New API key: ",newApiKey));

                   } else {
                       conn.rollback();
                       ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse("Unauthorized","Invalid API key"));
                   }
               }
           } catch (SQLException e) {
               conn.rollback();
               throw e;
           }
       } catch (SQLException e) {
           log.warn("Error refreshing API key: {}" , e.getMessage(),e);
           ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Internal Server Error","Error logging in"));
       }
   }

     /*
     *validation of accepted patterns for data
     *kind of pattern matching inputs
     */
   private static  boolean isValidEmail(java.lang.String email){
       return  EMAIL_PATTERN.matcher(email).matches();
   }
    private static boolean isValidPassword(java.lang.String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private static boolean isValidUsername(java.lang.String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }
}
