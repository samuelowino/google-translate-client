package org.mwangi.desktop.thememanager;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;

@Module
public class ThemeManagerModule {
    @Provides
    @Singleton
    public ThemeManager provideThemeManager(){
        return new ThemeManager();
    }
}
