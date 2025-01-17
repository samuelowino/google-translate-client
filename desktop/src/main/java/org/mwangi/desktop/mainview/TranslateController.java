package org.mwangi.desktop.mainview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import javafx.application.Platform;
import okhttp3.*;
import org.mwangi.desktop.dialogs.NotificationDialog;
import org.mwangi.desktop.exceptions.StatusException;
import org.mwangi.desktop.payload.ErrorResponse;
import org.mwangi.desktop.payload.TranslatePayload;
import org.mwangi.desktop.payload.TranslationResponse;
import org.mwangi.desktop.payload.ValidResponse;
import org.mwangi.desktop.profile.LoginController;
import org.mwangi.desktop.profile.ProfileDialog;
import org.mwangi.desktop.properties.PersistentProperties;
import org.mwangi.desktop.util.EventBus;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static org.mwangi.desktop.util.Constants.*;
import static org.mwangi.desktop.io.ExtendedIO.*;


public class TranslateController {
    final static  String API_KEY="";


    private final OkHttpClient client;
    private final ObjectMapper json;
    private final EventBus eventBus;

    private final PersistentProperties prefs;
    private final LoginController loginController;
    private Consumer<TranslationResponse> preview;

   private final Logger log =Logger.getLogger(TranslateController.class.getSimpleName());

 @Inject
  public TranslateController(EventBus eventBus, LoginController loginController, OkHttpClient client, ObjectMapper objectMapper, PersistentProperties prefs){
      this.eventBus=eventBus;
      this.loginController=loginController;
      this.client=client;
      this.json=objectMapper;
      this.prefs=prefs;
      eventBus.getPublished().subscribe(val->{
          if(preview !=null){
              preview.accept(val);
          }
      });
  }

    public  void sendTranslationRequest(TranslatePayload translatePayload){
        eventBus.activity(CompletableFuture.supplyAsync(()-> checkResponse(sendRequest(translatePayload)))
                .thenApply(this::poll)
                .thenAccept(this::Publisher)
                .exceptionally(throwable -> {
                    handleLoginError(throwable);
               return null;
        }));

    }
    private   Request  sendRequest(TranslatePayload translatePayload){
        RequestBody body = RequestBody.create(gson.toJson(translatePayload), MediaType.get("application/json"));
        return new Request.Builder().url(TRANSLATE_URL).post(body).header("X-API-KEY", prefs.getString(API_KEY).getValue()).build();
    }
    private   void handleLoginError(Throwable throwable){
        if(throwable.getCause() instanceof StatusException){
            CompletableFuture.runAsync(()->new ProfileDialog().showAndWait().ifPresent(loginController::checkProfile),Platform::runLater);
        }
    }
    private   String checkResponse(Request request){
        String jobId;
        try(Response response=handleRequest(()->client.newCall(request).execute())){
            String responseJson = response.body().string();
            switch (response.code()){
                case ACCEPTED:
                    ValidResponse val=json.readValue(responseJson, ValidResponse.class);
                    CompletableFuture.runAsync(()->new NotificationDialog(val.response(), val.details()).show(),Platform::runLater);
                    jobId= val.response();
                    break;
                case  UNAUTHORIZED:
                    ErrorResponse e = json.readValue(responseJson, ErrorResponse.class);
                    log.info(e.details());
                    throw new CompletionException(
                            new StatusException(e.details())
                    );

                default:
                    throw new CompletionException(
                            new RuntimeException()
                    );

            }
        }catch (IOException  e) {
            throw new RuntimeException(e);
        }
        return jobId;

    }

private String poll(String job_id) {
    String responsejson=null;
    String job_url=JOB_URL+job_id;
    while (responsejson == null) {
           Request request = new Request.Builder().url(job_url).addHeader("X-API-KEY",prefs.getString(API_KEY).getValue()).build();
        try (Response response = handleRequest(() -> client.newCall(request).execute())) {
              switch (response.code()){
                  case OK:
                      responsejson=response.body().string();
                      break;
                  case NOT_FOUND:
                      ErrorResponse e = json.readValue(response.body().string(), ErrorResponse.class);
                      CompletableFuture.runAsync(() -> new NotificationDialog(e.error(), e.details()).show(), Platform::runLater);
                      break;
              }
        } catch (IOException e) {
            log.warning(e.getMessage());
            throw new RuntimeException(e);
        }
        fixedDelay();
    }
    return responsejson;
}

    public void setPreview(Consumer<TranslationResponse> preview) {
        this.preview = preview;
    }



    private  void Publisher(String sjson){
    try {
        TranslationResponse translationResponse=json.readValue(sjson,TranslationResponse.class);
        eventBus.publisher(translationResponse);
        } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
       }
}
  private void fixedDelay(){
      try {
          Thread.sleep(5000);
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }
  }
}
