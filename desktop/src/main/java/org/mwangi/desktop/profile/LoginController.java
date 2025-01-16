package org.mwangi.desktop.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import javafx.application.Platform;
import okhttp3.*;
import org.mwangi.desktop.dialogs.NotificationDialog;
import org.mwangi.desktop.payload.ErrorResponse;
import org.mwangi.desktop.payload.ValidResponse;
import org.mwangi.desktop.properties.PersistentProperties;
import org.mwangi.desktop.util.EventBus;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static org.mwangi.desktop.util.Constants.*;

import static org.mwangi.desktop.io.ExtendedIO.*;


public class LoginController {
    private static  final Logger log=Logger.getLogger(LoginController.class.getSimpleName());
  
    private static final String API_KEY ="";
   public static  final Predicate<Integer> TYPE_NOTIF=s -> s >199 && s < 300;
    private final OkHttpClient client;
    private final ObjectMapper json;
    private final EventBus eventBus;
    private  final PersistentProperties prefs;
    @Inject
    public LoginController(EventBus eventBus, OkHttpClient client, ObjectMapper objectMapper, PersistentProperties prefs){
        this.eventBus=eventBus;
        this.client=client;
        this.json=objectMapper;
        this.prefs=prefs;
    }

    public  void checkProfile(ProfileDetails profileDetails){
        eventBus.activity(CompletableFuture.runAsync(()->{
            Predicate<ProfileDetails> path=p-> p instanceof LoginProfileDetails;
            Function<ProfileDetails,Request> reqBuilder=req-> {
                String url =path.test(req)? LOGIN_URL: REGISTER_URL;
                RequestBody body = RequestBody.create(gson.toJson(req), MediaType.get("application/json"));
                return new Request.Builder().url(url).post(body).build();
               };
            Request request=reqBuilder.apply(profileDetails);
            try(Response response=handleRequest(() ->client.newCall(request).execute())) {
                      checkResponse(response);
            } catch (IOException e) {
                log.warning(e.getMessage());
                throw new RuntimeException(e);
            }
        }));
    }
    private  void checkResponse(Response response) throws IOException {
        if (TYPE_NOTIF.test(response.code())) handleProfile(response);
        else handleError(response);
    }
    private void handleError(Response response) throws IOException {
        String responseJson = response.body().string();
        ErrorResponse e = json.readValue(responseJson, ErrorResponse.class);
        CompletableFuture.runAsync(() -> new NotificationDialog(e.error(), e.details()).show(), Platform::runLater);
    }

    private   void handleProfile(Response response) throws IOException {
            switch (response.code()) {
                case OK:
                    ValidResponse val = json.readValue(response.body().string(), ValidResponse.class);
                    CompletableFuture.runAsync(() -> new NotificationDialog(val.response(), val.details()).show(), Platform::runLater);
                    apiKeyConfig(val.details());
                    break;
                case CREATED:
                    ValidResponse val2 = json.readValue(response.body().string(), ValidResponse.class);
                    CompletableFuture.runAsync(() -> new NotificationDialog(val2.response(), val2.details()).showAndWait(), Platform::runLater)
                            .thenAccept((v) -> new ProfileDialog().showAndWait().ifPresent(this::checkProfile));
                    break;
                default:
                    throw new IOException();

            }

    }
    private void apiKeyConfig(String val){
        prefs.getString(API_KEY, val);
        prefs.reset();
        prefs.save();
    }
}
