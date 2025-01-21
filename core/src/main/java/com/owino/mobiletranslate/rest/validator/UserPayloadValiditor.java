package com.owino.mobiletranslate.rest.validator;

import com.owino.mobiletranslate.rest.payload.User;
import com.owino.mobiletranslate.rest.exception.ErrorResponse;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.logging.Logger;

public class UserPayloadValiditor {


    public static  void userRegistrationValidator(Context ctx){
        try {
            User validator = ctx.bodyValidator(User.class)
                    .check(
                            obj -> obj.username() != null && !obj.username().isEmpty(), "Username must not be empty"
                    ).check(
                            obj -> obj.email() != null && !obj.email().isEmpty(), "Email must not be empty"
                    ).check(
                            obj -> obj.password() != null && !obj.password().isEmpty(), "Password must not be empty"
                    ).get();
        }catch (BadRequestResponse e){
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad Request", "Invalid body"));
        }
    }
    public static  void userLoginValidator(Context ctx){
        try {
            User validator = ctx.bodyValidator(User.class)
                    .check(
                            obj -> obj.username() != null && !obj.username().isEmpty(), "Username must not be empty"
                    )
                    .check(
                            obj -> obj.password() != null && !obj.password().isEmpty(), "Password must not be empty"
                    ).get();
        }catch (BadRequestResponse e){
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad Request", "Invalid body"));
        }
    }
}
