package org.mwangi.desktop.download;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.mwangi.desktop.customcontrols.ValidateTextField;
import org.mwangi.desktop.util.UI;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class DownloadDialog extends Dialog<DownloadProperties> {
    private  static  final int LABEL_SIZE =90;
    private final String os = System.getProperty("os.name").toLowerCase();

    private final SimpleStringProperty filePathProperty=new SimpleStringProperty();
    private final SimpleBooleanProperty isValidPath=new SimpleBooleanProperty();
    private final SimpleStringProperty fileNameProperty=new SimpleStringProperty();
    private final SimpleBooleanProperty isValidName=new SimpleBooleanProperty();

    public DownloadDialog(){
        this.setTitle("Enter path and filename to save files");
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResource("/icons/download_icon.png").toString()));
        getDialogPane().setMinWidth(500);
        getDialogPane().setContent(UI.create(GridPane::new, gridPane -> {
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.getColumnConstraints().addAll(
                    new ColumnConstraints(LABEL_SIZE),
                    UI.create(ColumnConstraints::new , c->{
                        c.setHgrow(Priority.ALWAYS);
                    })
            );
            gridPane.setPadding(new Insets(14,0,0,14));
            gridPane.addRow(0,UI.boldLabel("File Path"),UI.create(()-> new ValidateTextField(REQUIRED_VALIDATION_RULE),
                    fileTextField->{
                filePathProperty.bindBidirectional(fileTextField.textProperty());
                        isValidPath.bind(fileTextField.isValidPropertyProperty());
                    }
            ));
            gridPane.addRow(1,UI.boldLabel("File Name"),UI.create(()-> new ValidateTextField(REQUIRED_VALIDATION_RULE2),
                    fileNameTextField->{
                        fileNameProperty.bindBidirectional(fileNameTextField.textProperty());
                        isValidName.bind(fileNameTextField.isValidPropertyProperty());
                    }
            ));
        }));
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        Node okButton=getDialogPane().lookupButton(ButtonType.OK);
        if(okButton != null){
            okButton.disableProperty().bind(
                  Bindings.createBooleanBinding(()-> !isValidPath.get() && !isValidName.get(),isValidPath,isValidName
                  )
            );
        }
        this.setResultConverter(param->{
            if(param == ButtonType.OK){
                return new DownloadProperties(filePathProperty.get(),fileNameProperty.get());
            }
            return null;
        });
    }
    Predicate<String> REQUIRED_VALIDATION_RULE=p-> os.contains("win")? (!p.contains("/") && WINDOWS_DIR.matcher(p).matches())
            : (!p.contains("\\") && UNIX_DIR.matcher(p).matches());
    Predicate<String> REQUIRED_VALIDATION_RULE2= fileName ->
            fileName != null && !fileName.isBlank()
                    && !fileName.matches(".*[\\\\/:*?\"<>|].*")
                    && !fileName.matches(".*[/\0].*")
                    && fileName.length() <= 255
                    && !fileName.endsWith(".");
    private static final Pattern WINDOWS_DIR = Pattern.compile(
            "^(?:[A-Za-z]:)?\\\\?[^<>:\"/\\\\|?*]+(?:\\\\[^<>:\"/\\\\|?*]+)*\\\\?$"
    );

    private static final Pattern UNIX_DIR = Pattern.compile(
            "^/?[^/\0<>:\"\\\\|?*]+(?:/[^/\0<>:\"\\\\|?*]+)*/?$"
    );
}
