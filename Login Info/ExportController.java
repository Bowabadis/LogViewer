import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

import ReaderFiles.User;
import ReaderFiles.UserCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;


//Associated With export.fxml as the controller, handles the conversion of raw data to specified formats
public class ExportController implements Initializable {

    @FXML
    private Button btnExport;

    @FXML
    private ChoiceBox<String> cBoxExport;

    @FXML
    private CheckBox cBoxTemplate;
    
    @FXML
    private VBox vUserHolder;

    CheckBox cBox;
    UserCollection userCollection;
    UserCollection expCollection = new UserCollection();

    final String[] exportOptions = {".xml", ".csv"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    //loads the user checkboxes
    public void load(UserCollection uCol){
        userCollection = uCol;
        vUserHolder.getChildren().clear();
        for (User user : userCollection.userList) {
            CheckBox cBox = new CheckBox();
            cBox.setFont(cBoxTemplate.getFont());
            cBox.setTextFill(cBoxTemplate.getTextFill());
            cBox.setGraphicTextGap(cBoxTemplate.getGraphicTextGap());
            cBox.setText(user.getName());
            vUserHolder.getChildren().add(cBox);
        }

        cBoxExport.getItems().addAll(exportOptions);
    }
    //adds all the selected users to a new ArrayList and exports that ArrayList
    @SuppressWarnings("unchecked")
    public void exportUsers(){
        expCollection.userList = (ArrayList<User>) userCollection.userList.clone();
        for(int i = vUserHolder.getChildren().size()-1; i>=0; i--){
            Node child = vUserHolder.getChildren().get(i);
            if(child instanceof CheckBox && !((CheckBox) child).isSelected()){
                //add to export list
                expCollection.userList.remove(userCollection.getUserNameIndex(((CheckBox) child).getText()));
            }
        }
        exportFile(expCollection);
    }

    //Exports the file, auto converts it to a .xml file and if .csv is selected it converts it to .csv
    public void exportFile(UserCollection collection){
        System.out.println("EXPORTING FILE...");
        File selectedFile = Viewer.exportFile(cBoxExport.getValue());
        if(selectedFile == null) return;

        try{
            //converts to xml
            JAXBContext jaxbContext = JAXBContext.newInstance(UserCollection.class);
        
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //writes the xml data to the selected file
            marshaller.marshal(collection, selectedFile);

            if(cBoxExport.getValue().equals(".csv")){
                  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
                  DocumentBuilder builder = factory.newDocumentBuilder(); 
                  Document document = builder.parse(selectedFile); 
        
                  //uses a StreamSource (XSLT file) to take the xml data and format it to csv
                  StreamSource stylesource = new StreamSource("ReaderFiles\\style.xsl"); 
                  Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource); 
                  Source source = new DOMSource((org.w3c.dom.Node) document); 
                  StreamResult outputTarget = new StreamResult(selectedFile); 
                  transformer.transform(source, outputTarget);
            }
            System.out.println("--EXPORT COMPLETE--");
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    public void selectAll(){
        for(Node child : vUserHolder.getChildren()){
            if(child instanceof CheckBox){
                ((CheckBox) child).setSelected(true);
            }
        }
    }
    public void deselectAll(){
        for(Node child : vUserHolder.getChildren()){
            if(child instanceof CheckBox){
                ((CheckBox) child).setSelected(false);
            }
        }
    }

}
