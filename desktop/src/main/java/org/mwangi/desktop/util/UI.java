package org.mwangi.desktop.util;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UI {
    public static <T extends Pane> T create(Supplier<T> create, Function<T, List<Node>> builder){
        T pane = create.get();
        pane.getChildren().addAll(builder.apply(pane));
        return pane;
    }

    public static Scene scene(Supplier<Parent> rootbuilder){
        return new Scene(rootbuilder.get());
    }
    public static <T> T create(Supplier<T> creator, Consumer<T> builder){
        T component=creator.get();
        builder.accept(component);
        return  component;
    }

    public static ToolBar toolBar(Function<ToolBar,List<Node>> builder){
        ToolBar toolBar=new ToolBar();
        toolBar.getItems().addAll(builder.apply(toolBar));
        return  toolBar;
    }

    public  static Label boldLabel(String title){
        Label label=new Label(title);
        label.getStyleClass().add("text-bold");
        return label;
    }
    public static  ImageView icon(String icon){
       return icon(icon,16);
    }

    public  static ImageView icon(String icon,double size){
        String resourcePath = UI.class.getResource(icon).toString();
        return new ImageView(new Image(resourcePath,size,size,true,false));
    }

}
