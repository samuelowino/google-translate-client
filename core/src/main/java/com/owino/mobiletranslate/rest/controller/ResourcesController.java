package com.owino.mobiletranslate.rest.controller;

import com.owino.mobiletranslate.android.AndroidTranslationProcessor;
import com.owino.mobiletranslate.android.model.Resources;
import com.owino.mobiletranslate.ios.iOSTranslationProcessor;
import com.owino.mobiletranslate.rest.db.DatabaseManager;
import com.owino.mobiletranslate.rest.exception.ErrorResponse;
import com.owino.mobiletranslate.rest.exception.ValidationException;
import com.owino.mobiletranslate.rest.payload.*;
import com.owino.mobiletranslate.rest.response.ValidResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
/*
 * routes to the services provided
 */
@Slf4j
public class ResourcesController {
    /*
     *This going to be shared across  all requests
     *A in memory  approach was favoured but its vulnerable to server restarts
     *This does not pay attention to scalability (better options are available )
     */
    private static  final Map<String,JobInformation> ongoingTranslations =new ConcurrentHashMap<>();
    private  static  final ScheduledExecutorService cleanupExecutor= Executors.newSingleThreadScheduledExecutor();
    static Resources translationResources =new Resources();
    private static final long API_KEY_EXPIRATION = 7L * 24 * 60 * 60 * 1000;  // 7 days in milliseconds
    /*
     * since translation are long running an async method is used
     * receives translation and initiates a CompletableFuture
     */
    public static void startTranslation(Context ctx)  {
        java.lang.String apiKey = ctx.header("X-API-Key");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse("Unauthorized","API key is required"));
            return;
        }
        if(ctx.body().isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad Request","provide a request"));
            return;
        }

        try (Connection conn = DatabaseManager.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT user_id, created_at FROM api_keys WHERE api_key = ?")) {

            pstmt.setString(1, apiKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    int userId = rs.getInt("user_id");
                    if (isApiKeyExpired(createdAt)) {
                        ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse("Unauthorized","API key has expired"));

                    } else {
                        try {
                            TranslatePayload payload = ctx.bodyAsClass(TranslatePayload.class);
                            String jobID = UUID.randomUUID().toString();
                            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                                try {
                                    return processPayload(payload, ctx);
                                } catch (ValidationException e) {
                                    log.warn("Error processing logs: {}", e.getMessage(), e);
                                    throw new CompletionException(e);
                                }
                            }).orTimeout(40, TimeUnit.HOURS);
                            ongoingTranslations.put(jobID, new JobInformation(future, Instant.now()));
                            ctx.status(202).json(new ValidResponse(202, jobID, "Translation has started"));
                        }catch (Exception e){
                            log.warn("Error reading request body: {}", e.getMessage());
                            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Bad Request", "Could not process the request body"));
                        }
                    }
                } else {
                    ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse("Invalid API key","Refresh api token or login to acquire new key"));
                }
            }
        } catch (SQLException e) {
            log.warn("Error validating API key {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Error validating API key","Error occurred trying to resolve api key"));
        }

    }
    /*
     *process payload according to type
     */
    private static Object processPayload(TranslatePayload payload,Context ctx) throws ValidationException {
        switch(payload){
            case AndroidPayload androidPayload ->{
                List<String> target_lang=androidPayload.distinctLanguages();
                List<XmlMessage> xml_content=androidPayload.xmlContent();
                com.owino.mobiletranslate.android.model.String[] androidJsonstring;
                androidJsonstring=xml_content.stream().map(x->new com.owino.mobiletranslate.android.model.String(x.name(), x.content()))
                                .toArray(com.owino.mobiletranslate.android.model.String[]::new);
                translationResources.setStrings(androidJsonstring);
                return new AndroidTranslationProcessor().runTranslation(translationResources,target_lang);

            }
            case IOSPayload iosPayload ->{
                List<IOSMessage> ios_content =iosPayload.iosContent();
                List<String>  distinct_languages=iosPayload.distinctLanguages();
                return new iOSTranslationProcessor().runTranslation(ios_content,distinct_languages);
            }
            default -> throw new ValidationException("Unsupported type detected ");
        }
    }
    /*
     * Since an async model is used this method is the one
     * clients check to see if their request is done
     * When you get the TranslateResponse payload its automatically removed from memory
     * so retrying with same  id will lead to a job not found response
     */
    @SneakyThrows
    public static void getTranslationStatus(Context ctx) {
        String jobId=ctx.pathParam("jobId");

        JobInformation jobInformation=ongoingTranslations.get(jobId);
        if (jobInformation == null) {
            ctx.status(404).json(new ErrorResponse("Not Found", "Job not found"));
            return;
        }
        CompletableFuture<Object> future =jobInformation.future();
        if(future.isDone()){
            try {
                TranslationResponse result = (TranslationResponse) future.get();
                ctx.status(HttpStatus.OK).json(result);
            }finally {
                ongoingTranslations.remove(jobId);
            }
        }else{
            ctx.status(HttpStatus.ACCEPTED).json(new ValidResponse(202,"In Progress","Translation in progress"));
        }
    }
    /*
     *House Keeping function to clean forgotten jobs or jobs which their lifetime is done
     */
    public static void startCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(() -> {
            Instant cutoff = Instant.now().minus(Duration.ofHours(1));
            ongoingTranslations.entrySet().removeIf(entry -> {
                JobInformation info = entry.getValue();
                return info.startTime().isBefore(cutoff) && info.future().isDone();
            });
        }, 1, 1, TimeUnit.HOURS);

    }
    public static void shutdown() {
        cleanupExecutor.shutdown();
    }
    private static boolean isApiKeyExpired(Timestamp createdAt) {
        return System.currentTimeMillis() - createdAt.getTime() > API_KEY_EXPIRATION;
    }


}
