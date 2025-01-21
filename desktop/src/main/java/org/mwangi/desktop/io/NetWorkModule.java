package org.mwangi.desktop.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

@Module
public class NetWorkModule {
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(){
        return  new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();
    }
    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper(){
        return  new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


}
