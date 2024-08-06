import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import javafx.stage.FileChooser.*;

import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Viewer extends Application {

    static Stage stage;

    @Override
    public void start(Stage stage){
        //Sets the fxml file to the application and executes
        Viewer.stage = stage;
        try{
            final int initWidth = 800;
            final int initHeight = 400;
            final double ratio = initWidth/initHeight;
            final Pane root = new Pane();
            
            //Controller pane created for resizing
            Pane controllerPane = FXMLLoader.load(getClass().getResource("main.fxml"));
            controllerPane.setPrefWidth(initWidth);
            controllerPane.setPrefHeight(initHeight);
            root.getChildren().add(controllerPane);
            
            //Adds items to the scene and listeners for resizing
            Scene scene = new Scene(root, initWidth, initHeight);
            SceneSizeChangeListener sizeListener = new SceneSizeChangeListener(scene, ratio, initHeight, initWidth, root);
            scene.widthProperty().addListener(sizeListener);
            scene.heightProperty().addListener(sizeListener);
            
            //Main Window Configs
            Image icon = new Image("https://images.crunchbase.com/image/upload/c_pad,h_256,w_256,f_auto,q_auto:eco,dpr_1/mky0fkibqswnxfbvhk3i");
            stage.getIcons().add(icon);
            stage.setMinWidth(initWidth+10);
            stage.setMinHeight(initHeight+70);
            stage.setTitle("Log Info Viewer");
            stage.setScene(scene);
            stage.show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //Popup Selector for choosing a log file
    public static File openFileSelecter(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Log File");
        ExtensionFilter extensionFilter = new ExtensionFilter("Text Files", "*.txt", "*.log", "*.doc", "*.docx", "*.pdf");
        ExtensionFilter extensionFilter2 = new ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(extensionFilter, extensionFilter2);
        File file = fileChooser.showOpenDialog(stage);
        return file;

    }
    //Popup Selector for choosing multiple log files
    public static List<File> openMultipleFiles(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Log Files");
        ExtensionFilter extensionFilter = new ExtensionFilter("Text Files", "*.txt", "*.log", "*.doc", "*.docx", "*.pdf");
        ExtensionFilter extensionFilter2 = new ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(extensionFilter, extensionFilter2);
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        return files;
    }
    //Popup Selector for exporting
    public static File exportFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export XML File");
        ExtensionFilter extensionFilter = new ExtensionFilter(".xml", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(stage);
        return file;
    }
    public static void main(String[] args) {
        launch(args);
    }

    //Custom Listener Class for Changing Window Size
    private static class SceneSizeChangeListener implements ChangeListener<Number> {
        private final Scene scene;
        private final double ratio;
        private final double initHeight;
        private final double initWidth;
        private final Pane contentPane;
    
        public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth, Pane contentPane) {
          this.scene = scene;
          this.ratio = ratio;
          this.initHeight = initHeight;
          this.initWidth = initWidth;
          this.contentPane = contentPane;
        }
    
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
          final double newWidth  = scene.getWidth();
          final double newHeight = scene.getHeight();
    
          double scaleFactor =
              newWidth / newHeight > ratio
                  ? newHeight / initHeight
                  : newWidth / initWidth;
    
          if (scaleFactor >= 1) {
            Scale scale = new Scale(scaleFactor, scaleFactor);
            scale.setPivotX(0);
            scale.setPivotY(0);
            scene.getRoot().getTransforms().setAll(scale);
    
            contentPane.setPrefWidth (newWidth  / scaleFactor);
            contentPane.setPrefHeight(newHeight / scaleFactor);
          } else {
            contentPane.setPrefWidth (Math.max(initWidth,  newWidth));
            contentPane.setPrefHeight(Math.max(initHeight, newHeight));
          }
        }
      }

}


