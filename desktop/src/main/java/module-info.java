module desktop {
    requires javafx.controls;
    requires java.logging;
    requires atlantafx.base;
    requires io.reactivex.rxjava2;
    requires jakarta.inject;
    requires  dagger;
    requires java.compiler;
    requires javax.inject;
    requires jakarta.xml.bind;
    requires java.prefs;
    exports org.mwangi.desktop;
    exports org.mwangi.desktop.util;
}