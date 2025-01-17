package org.mwangi.desktop.mainview;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.mwangi.desktop.customcontrols.ActivityIndicator;
import org.mwangi.desktop.customcontrols.ValidateTextArea;
import org.mwangi.desktop.customcontrols.ValidateTextField;
import org.mwangi.desktop.payload.*;
import org.mwangi.desktop.profile.LoginController;
import org.mwangi.desktop.profile.ProfileDialog;
import org.mwangi.desktop.thememanager.ThemeManager;
import org.mwangi.desktop.util.EventBus;
import org.mwangi.desktop.util.UI;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static org.mwangi.desktop.io.ExtendedIO.*;
import static org.mwangi.desktop.util.Constants.*;


public class MainView extends VBox {
    Predicate<String> REQUIRED_VALIDATION_RULE= s -> LOCALITIES.contains(s);
    private final SimpleStringProperty localityProperty=new SimpleStringProperty();
    private  final BooleanProperty isValidlocalityProperty=new SimpleBooleanProperty();
    private  final SimpleStringProperty androidDataProperty=new SimpleStringProperty();
    private  final  BooleanProperty isValidAndroidDataProperty=new SimpleBooleanProperty();
    private final SimpleStringProperty iosDataProperty=new SimpleStringProperty();
    private final  BooleanProperty isValidIosProperty=new SimpleBooleanProperty();
    private final EventBus bus;
    public volatile static Stage previewStage;
   @Inject
    public MainView(LoginController loginController, TranslateController translateController, EventBus eventBus, ThemeManager themeManager, ActivityIndicator activityIndicator) {
        this.bus=eventBus;
        translateController.setPreview(this::preview);
        this.getChildren().addAll(
                UI.toolBar(toolBar -> {
                    toolBar.setOrientation(Orientation.HORIZONTAL);
                    toolBar.getStyleClass().add(getClass().getResource("/css/toggle-buttons.css").toExternalForm());
                    return List.of(
                            UI.create( Button::new, button ->{
                                button.setTooltip(new Tooltip("profile"));
                                button.setText("Profile");
                                button.setOnAction(_ -> new ProfileDialog().showAndWait().ifPresent(loginController::checkProfile));
                            }),
                            UI.create(ToggleButton::new,toggleButton->{
                                toggleButton.setGraphic(UI.icon("/icons/earth_night.png"));
                                toggleButton.getStyleClass().addAll(BUTTON_ICON);
                                toggleButton.setOnAction(_->{
                                    themeManager.switchTheme().applyCurrentTheme();
                                    String icon=themeManager.getCurrentTheme().isDarkMode()?"/icons/weather_sun.png":"/icons/earth_night.png";
                                    toggleButton.setGraphic(UI.icon(icon));
                                    Window.getWindows().forEach(window -> themeManager.applyPseudoClasses(window.getScene().getRoot()));
                                });
                            })

                    );
                }),
                UI.boldLabel("Translation request"),
                UI.create(TabPane::new, (TabPane pane) -> {
                    pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                    pane.getTabs().addAll(
                            UI.create(Tab::new,  tab -> {
                                tab.setText("iOS Translation");
                                tab.setContent(UI.create(VBox::new,p1->{
                                    p1.getChildren().addAll(
                                            UI.boldLabel("Target Languages (comma separated)"),
                                            UI.create(()->new ValidateTextField(REQUIRED_VALIDATION_RULE), localities->{
                                                localityProperty.bindBidirectional(localities.textProperty());
                                                isValidlocalityProperty.bind(localities.isValidPropertyProperty());
                                            }),
                                            UI.boldLabel("IOS strings"),
                                            UI.create(()->new ValidateTextArea(REQUIRED_IOS_VALIDATION_RULE), iostextArea->{
                                                iosDataProperty.bindBidirectional(iostextArea.textProperty());
                                                isValidIosProperty.bind(iostextArea.isValidPropertyProperty());
                                            }),
                                            new Label(IOS_EXAMPLE),
                                            UI.create(Button::new, button ->{
                                                button.setTooltip(new Tooltip("submit translation request"));
                                                button.setText("submit translation request");
                                                button.disableProperty().bind(
                                                        Bindings.createBooleanBinding(()-> !isValidlocalityProperty.get() || !isValidIosProperty.get(),isValidlocalityProperty,isValidIosProperty)
                                                );
                                                button.setOnAction(_ ->{
                                                    String[] localStrs=localityProperty.getValue().split(",");
                                                    List<IOSMessage> iosMessages=fromIos(iosDataProperty.getValue());
                                                    List<String> localities= Arrays.stream(localStrs).toList();
                                                    translateController.sendTranslationRequest(new IOSPayload(WORKFLOW,IOS,localities,iosMessages));
                                                });
                                            })
                                    );
                                }));

                            }),

                            UI.create(Tab::new, (Consumer<Tab>)tab -> {
                                tab.setText("Android Translation");
                                tab.setContent(UI.create(VBox::new,p1->{
                                    p1.getChildren().addAll(
                                            UI.boldLabel("Target Languages (comma separated)"),
                                            UI.create(()->new ValidateTextField(REQUIRED_VALIDATION_RULE),localities->{
                                                localityProperty.bindBidirectional(localities.textProperty());
                                                isValidlocalityProperty.bind(localities.isValidPropertyProperty());
                                            }),
                                            UI.boldLabel("Android XML"),
                                            UI.create(()->new ValidateTextArea(REQUIRED_ANDROID_VALIDATION_RULE), androidtextArea->{
                                                androidDataProperty.bindBidirectional(androidtextArea.textProperty());
                                                isValidAndroidDataProperty.bind(androidtextArea.isValidPropertyProperty());
                                            }),
                                            new Label(ANDROID_EXAMPLE),
                                            UI.create( Button::new, button ->{
                                                button.setTooltip(new Tooltip("submit translation request"));
                                                button.setText("submit translation request");
                                                button.disableProperty().bind(
                                                        Bindings.createBooleanBinding(()-> !isValidlocalityProperty.get() || !isValidAndroidDataProperty.get(),isValidlocalityProperty,isValidAndroidDataProperty)
                                                );
                                                button.setOnAction(_ ->{
                                                    String[] localstrs=localityProperty.getValue().split(",");
                                                    List<String> localities=Arrays.stream(localstrs).toList();
                                                    List<XmlMessage> androidMessages=  Objects.requireNonNull(fromXml(androidDataProperty.getValue())).getStrings();
                                                    translateController.sendTranslationRequest(new AndroidPayload(WORKFLOW,ANDROID,localities,androidMessages));
                                                });
                                            })
                                    );
                                }));
                            })
                    );
                }),
             activityIndicator
        );
    }
    private synchronized void initializePreviewStage() {
        previewStage=UI.create(Stage::new,stage -> {
           stage.setTitle("translation preview");
           stage.initModality(Modality.NONE);
           stage.setResizable(false);
           stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/icons/preview_icon.png")).toString()));
           stage.setScene(new Scene(new PreviewBox()));
        });
    }
    private void   preview(TranslationResponse p){
        bus.activity(CompletableFuture.runAsync(()->{
            if (previewStage == null)  initializePreviewStage();
            PreviewBox previewBox=(PreviewBox) previewStage.getScene().getRoot();
            previewBox.updateStuff(p);
            if (!previewStage.isShowing()) previewStage.show();
        },Platform::runLater));

    }
}
