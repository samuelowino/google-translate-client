module desktop {
    requires javafx.controls;
    requires java.logging;
    requires atlantafx.base;
    requires io.reactivex.rxjava2;
    requires jakarta.inject;

    requires jakarta.xml.bind;
    exports org.mwangi.desktop;
    exports org.mwangi.desktop.util;
}