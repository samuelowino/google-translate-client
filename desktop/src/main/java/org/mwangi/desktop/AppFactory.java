package org.mwangi.desktop;

import dagger.Component;
import jakarta.inject.Singleton;
import javafx.scene.layout.Region;
import org.mwangi.desktop.io.NetWorkModule;
import org.mwangi.desktop.mainview.MainModule;
import org.mwangi.desktop.properties.PreferencesModule;
import org.mwangi.desktop.thememanager.ThemeManager;
import org.mwangi.desktop.thememanager.ThemeManagerModule;

@Component(modules = {NetWorkModule.class, ThemeManagerModule.class, MainModule.class, PreferencesModule.class})
@Singleton
public interface AppFactory {

    Region mainView();
    ThemeManager provideThemeManager();
}
