package org.mwangi.desktop.thememanager;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.Theme;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public class ThemeManager {
    private static final PseudoClass DARK=PseudoClass.getPseudoClass("DARK");
    private enum ThemeType{
        LIGHT(new NordLight()),
        DARK(new NordDark());
        private final Theme theme;
        ThemeType(Theme theme){
            this.theme=theme;
        }
    }
    private  ThemeType currentTheme= ThemeType.LIGHT;
    public Theme getCurrentTheme(){
        return currentTheme.theme;
    }
    public ThemeManager switchTheme(){
        if(currentTheme == ThemeType.LIGHT){
            currentTheme = ThemeType.DARK;
        }else{
            currentTheme = ThemeType.LIGHT;
        }
        return  this;
    }
    public  ThemeManager  applyCurrentTheme(){
        Application.setUserAgentStylesheet(currentTheme.theme.getUserAgentStylesheet());
        return  this;
    }
    public ThemeManager applyPseudoClasses(Node node){
        node.pseudoClassStateChanged(DARK,currentTheme.theme.isDarkMode());
        return this;
    }

}
