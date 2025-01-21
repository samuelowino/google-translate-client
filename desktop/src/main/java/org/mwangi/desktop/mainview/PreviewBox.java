package org.mwangi.desktop.mainview;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import org.mwangi.desktop.dialogs.NotificationDialog;
import org.mwangi.desktop.download.DownloadDialog;
import org.mwangi.desktop.download.DownloadProperties;
import org.mwangi.desktop.payload.IOSMessage;
import org.mwangi.desktop.payload.Resources;
import org.mwangi.desktop.payload.TranslationResponse;
import org.mwangi.desktop.payload.XmlMessage;
import org.mwangi.desktop.util.UI;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.mwangi.desktop.download.zip.ZipManager.androidZipper;
import static org.mwangi.desktop.download.zip.ZipManager.iosZipper;
import static org.mwangi.desktop.util.Constants.IOS;



public class PreviewBox extends VBox {
    private TextArea textArea;
    private ToolBar toolBar;
    private Button previewPackageButton;
    private static final Logger log=Logger.getLogger(PreviewBox.class.getSimpleName());

    public PreviewBox(){
           buildUI();
    }

    private  void buildUI(){
            textArea = new TextArea();
            toolBar = UI.create(ToolBar::new, toolBar -> {
                toolBar.setOrientation(Orientation.HORIZONTAL);
            });
            previewPackageButton = new Button("preview and save");
            this.getChildren().addAll(
                    UI.boldLabel("Translation preview"),
                    toolBar,
                    textArea,
                    previewPackageButton
            );
    }
    public void updateStuff(TranslationResponse translationResponse){
        if(translationResponse == null) return;
        previewPackageButton.setOnAction(_ -> new DownloadDialog().showAndWait()
                .ifPresent(downloadProperties -> zipTranslations(downloadProperties,translationResponse)));
        List<Button> buttons = translationResponse.translations().keySet()
                .stream()
                .map(Button::new)
                .toList();
        for (Button button : buttons) button.setOnAction(_ -> buttonAction(translationResponse,button));
        toolBar.getItems().clear();
        toolBar.getItems().addAll(buttons);
    }
    private void buttonAction(TranslationResponse translationResponse,Button button){
        if(translationResponse.targetOS().equals(IOS))
            textArea.setText(formatIosStr(translationResponse.translations().get(button.textProperty().getValue())));
        else
            textArea.setText(toXml(translationResponse.translations().get(button.textProperty().getValue())));

    }
     private  void zipTranslations(DownloadProperties downloadProperties, TranslationResponse translationResponse){
         try {
             if (translationResponse.targetOS().equals(IOS))iosZipper(downloadProperties, translationResponse.translations());
             else
                 androidZipper(downloadProperties, translationResponse.translations());
         } catch (IOException e) {
             new NotificationDialog("Failed to save translations",e.getMessage()).showAndWait();
             log.warning(e.getMessage());
         }

     }

    private String formatIosStr(Map<String, String> translations)
    {
        List<IOSMessage> iosMessageList = translations.entrySet().stream()
                .map(entry -> new IOSMessage(entry.getKey(), entry.getValue()))
                .toList();
        StringBuilder s=new StringBuilder();
        iosMessageList.forEach((p)-> s.append(p.key()).append(" = ").append(p.content()).append(";\n"));
        StringWriter write =new StringWriter();
        write.append(s);
        return write.toString();
    }
    private String toXml(Map<String, String> translations) {
        try {
            Resources resources = new Resources();
            List<XmlMessage> xmlMessages = translations.entrySet().stream()
                    .map(entry -> new XmlMessage(entry.getKey(),entry.getValue()))
                    .toList();
            resources.setStrings(xmlMessages);
            JAXBContext contextObj = JAXBContext.newInstance(Resources.class);
            Marshaller marshaller = contextObj.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(resources, writer);
            return writer.toString();

        } catch (JAXBException e) {
            return null;
        }
    }
}
