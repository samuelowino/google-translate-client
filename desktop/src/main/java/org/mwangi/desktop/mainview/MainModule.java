package org.mwangi.desktop.mainview;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import javafx.scene.layout.Region;
import okhttp3.OkHttpClient;
import org.mwangi.desktop.customcontrols.ActivityIndicator;
import org.mwangi.desktop.profile.LoginController;
import org.mwangi.desktop.properties.PersistentProperties;
import org.mwangi.desktop.util.EventBus;
import org.mwangi.desktop.util.FxExecutor;

@Module
public class MainModule {
    @Provides
    public ActivityIndicator provideActivityIndicator(EventBus eventBus){
        return  new ActivityIndicator(eventBus);
    }
    @Provides
    public Region mainView(MainView mainview){
        return mainview;
    }
    @Provides
    @Singleton
    public EventBus provideEventBus(){
         return  new EventBus(FxExecutor.getInstance());
     }
     @Provides
    public LoginController loginController(EventBus eventBus, OkHttpClient okHttpClient, ObjectMapper objectMapper, PersistentProperties prefs){
        return new LoginController(eventBus,okHttpClient,objectMapper,prefs);
     }
     @Provides
    public TranslateController translateController(EventBus eventBus,LoginController loginController,OkHttpClient okHttpClient,ObjectMapper objectMapper, PersistentProperties prefs){
        return  new TranslateController(eventBus,loginController,okHttpClient,objectMapper,prefs);
     }
}
