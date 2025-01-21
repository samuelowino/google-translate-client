package org.mwangi.desktop.properties;

import dagger.Module;
import dagger.Provides;
import org.mwangi.desktop.Launcher;

@Module
public class PreferencesModule {
    @Provides
    public  PersistentProperties providePersistenceProperties(){
        return new PersistentProperties(Launcher.class);
    }
}
