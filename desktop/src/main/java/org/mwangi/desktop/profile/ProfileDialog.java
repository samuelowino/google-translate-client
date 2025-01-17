package org.mwangi.desktop.profile;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.mwangi.desktop.customcontrols.ValidatePasswordField;
import org.mwangi.desktop.customcontrols.ValidateTextField;
import org.mwangi.desktop.util.UI;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ProfileDialog extends Dialog<ProfileDetails> {
    private  static  final int LABEL_SIZE =90;
    private static final Map<String, Predicate<String>> REQUIRED_VALIDATION_RULE=Map.of("Required",s->s==null || s.isBlank());
    private final SimpleStringProperty profileNameProperty=new SimpleStringProperty();
    private final BooleanProperty profileNameValidProperty = new SimpleBooleanProperty();
    private  final SimpleStringProperty passwordProperty=new SimpleStringProperty();
    private final BooleanProperty passwordValidProperty=new SimpleBooleanProperty();
    private final SimpleStringProperty emailProperty=new SimpleStringProperty();
    public  final  BooleanProperty emailValidProperty=new SimpleBooleanProperty();
    private final BooleanProperty loginTabSelectedProperty=new SimpleBooleanProperty();
    private final BooleanProperty registerTabSelectedProperty=new SimpleBooleanProperty();
    public ProfileDialog(){
        setTitle("Login or Account creation");
        getDialogPane().setMinWidth(500);
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResource("/icons/login_icon.png").toString()));
        getDialogPane().setContent(UI.create(TabPane::new, Tabpane->{
            Tabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            Consumer<GridPane> defaultSettings=gridPane->{
                gridPane.setHgap(10);
                gridPane.setVgap(10);
                gridPane.getColumnConstraints().addAll(
                  new ColumnConstraints(LABEL_SIZE),
                  UI.create(ColumnConstraints::new , c->{
                      c.setHgrow(Priority.ALWAYS);
                  })
                );
                gridPane.setPadding(new Insets(14,0,0,14));
                gridPane.addRow(0,UI.boldLabel("username"),UI.create(()-> new ValidateTextField(REQUIRED_VALIDATION_RULE),
                             usernameTextField->{
                                profileNameProperty.bindBidirectional(usernameTextField.textProperty());
                                profileNameValidProperty.bind(usernameTextField.isValidPropertyProperty());
                             }
                        ));
                gridPane.addRow(1,UI.boldLabel("password"),UI.create(()-> new ValidatePasswordField(REQUIRED_VALIDATION_RULE),
                        passwordTextField->{
                            passwordProperty.bindBidirectional(passwordTextField.textProperty());
                            passwordValidProperty.bind(passwordTextField.isValidPropertyProperty());
                        }
                ));
            };
            Tabpane.getTabs().add(UI.create(Tab::new,tab->{
               tab.setText("Login");
               loginTabSelectedProperty.bind(tab.selectedProperty());
               tab.setContent(UI.create(GridPane::new, defaultSettings));
            }));
            Tabpane.getTabs().add(UI.create(Tab::new,tab->{
                tab.setText("register");
                registerTabSelectedProperty.bind(tab.selectedProperty());
                tab.setContent(UI.create(GridPane::new, gridPane->{
                    defaultSettings.accept(gridPane);
                    gridPane.addRow(2,UI.boldLabel("email"),UI.create(()->new ValidateTextField(REQUIRED_VALIDATION_RULE),
                            emailTextField->{
                             emailProperty.bindBidirectional(emailTextField.textProperty());
                             emailValidProperty.bind(emailTextField.isValidPropertyProperty());
                            }));
                }));

            }));
        }));
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        Node okButton=getDialogPane().lookupButton(ButtonType.OK);
        if(okButton != null){
              okButton.disableProperty().bind(
                      Bindings.createBooleanBinding(()->{
                          if(loginTabSelectedProperty.get())
                              return !(profileNameValidProperty.get() && passwordValidProperty.get());
                          else if (registerTabSelectedProperty.get())
                              return !(profileNameValidProperty.get() && passwordValidProperty.get() && emailValidProperty.get());
                          return true;
                      },loginTabSelectedProperty,registerTabSelectedProperty,profileNameValidProperty,passwordValidProperty,emailValidProperty)
              );
        }
        this.setResultConverter(param->{
            if(param == ButtonType.OK){
                if(loginTabSelectedProperty.get()){
                    return  new LoginProfileDetails(profileNameProperty.get(),passwordProperty.get());
                } else if (registerTabSelectedProperty.get()) {
                    return  new RegisterProfileDetails(profileNameProperty.get(),passwordProperty.get(),emailProperty.get());
                }
            }
            return null;
        });
    }

}
