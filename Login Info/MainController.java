
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


import ReaderFiles.DocumentReader;
import ReaderFiles.User;
import ReaderFiles.UserCollection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


//Associated With main.fxml as the controller
public class MainController implements Initializable {

    @FXML
    private Button btnMoreInfo;

    @FXML
    private Button btnMoreInfoClose;

    @FXML
    private Button btnUpdate;
    
    @FXML
    private Button btnExport;

    @FXML
    private Button btnViewTotal;

    @FXML
    private LineChart<String, Number> chrtMainChart;

    @FXML
    private AnchorPane extraInfoMainPane;
    
    @FXML
    private HBox hBoxExtraInfo;

    @FXML
    private Label lblUserTitleLogin;

    @FXML
    private Label lblUserTitleName;

    @FXML
    private CategoryAxis mainXAxis;

    @FXML
    private NumberAxis mainYAxis;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private VBox vUsers;


    public User selectedUser;

    ArrayList<User> listOfUsers;
    UserCollection userCollection;
    Map<String, Object> map = new HashMap<>();

    private LocalDate earliestDate, latestDate;

    //Called on startup
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewTotalGraph();

    }

    //Called when valid log file is uploaded
    private void start(){
        listOfUsers = userCollection.userList;
        setSelectedUser(listOfUsers.get(0));

        //makes extra calcualtions the that document reader does not do
        calculateSummaryData();
        
        viewTotalGraph();
        
        //listener for search box
        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadUsers();
        });

        loadUsers();
    }
    //Called from fxml file
    public void showExtraMenu(){
        extraInfoMainPane.setVisible(true);
        loadExtraInfo();
    }
    //Called from fxml file
    public void hideExtraMenu(){
        extraInfoMainPane.setVisible(false);
    }
    //Loads the text of the individual log occurences
    public void loadExtraInfo(){
        //dates should be automatically sorted since data is captured in the txt file by time so I don't sort them here
        try{
            //creates new fxml file with the text if it hasn't been created -> otherwise retrieves it
            Object loaded = map.get(selectedUser.getName());
            if(loaded == null){
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("extraInfoHolder.fxml"));
                loaded = loader.load();
                ExtraInfoHolderController controller = loader.getController();
                controller.setUser(selectedUser);
                map.put(selectedUser.getName(), loaded);
            }
            hBoxExtraInfo.getChildren().setAll((Parent) loaded);
            hBoxExtraInfo.getChildren().add(0, btnMoreInfoClose);
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    public void loadUsers(){
        final ArrayList<User> userList = userCollection.userList;
        try{
            Node[] nodes = new Node[userCollection.userList.size()];

            vUsers.getChildren().clear();
            //loades user.fxml files below the search box, and adds listeners to them
            for(int i = 0; i<nodes.length; i++){
                if(userList.get(i).getName().toLowerCase().contains(txtFieldSearch.getText().toLowerCase())){
                    //name matches search box input
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("user.fxml"));
                    nodes[i] = loader.load();
                    final int h = i;
                    UserController controller = loader.getController();
                    controller.setUserInfo(userList.get(i).getName(), userList.get(i).getLogAttempts());
                    nodes[i].setOnMouseEntered(evt->{
                        //add effect
                        nodes[h].setStyle("-fx-background-color: #009999");
                    });
                    nodes[i].setOnMouseExited(evt->{
                        //add effect
                        nodes[h].setStyle("-fx-background-color: #000028");
                    });
                    nodes[i].setOnMouseClicked(evt->{
                        //load data
                        setSelectedUser(userList.get(h));
                        updateGraph();
                        nodes[h].setStyle("-fx-border-color: white");
                        nodes[h].setStyle("-fx-border-width: 2px");
                        nodes[h].setStyle("-fx-background-color: #009999");
                    });
                    vUsers.getChildren().add(nodes[i]);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void setSelectedUser(User user){
        selectedUser = user;
        btnMoreInfo.setVisible(true);
        lblUserTitleName.setText(selectedUser.getName());
        lblUserTitleLogin.setText("Logs: "+ Integer.toString(selectedUser.getLogAttempts()));
    }
    private void updateGraph(){   
        chrtMainChart.getData().clear();
        
        //adds data to the graph
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        for(int i =0; i<=earliestDate.until(latestDate, ChronoUnit.DAYS); i++){
            LocalDate date = earliestDate.plusDays(i);
            series.getData().add(new XYChart.Data<String, Number>(date.format(formatter), selectedUser.getLogsAtDate(date)));
        }
        chrtMainChart.getData().add(series);
        chrtMainChart.setCreateSymbols(false);

    }
    public void viewTotalGraph(){
        lblUserTitleName.setText("Total Logs");
        lblUserTitleLogin.setText("");
        btnMoreInfo.setVisible(false);
        if(userCollection == null) return;
        chrtMainChart.getData().clear();
        
        //adds data to the graph
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        for(int i =0; i<=earliestDate.until(latestDate, ChronoUnit.DAYS); i++){
            LocalDate date = earliestDate.plusDays(i);
            int totalLogs = 0;
            for (User user : userCollection.userList) {
                totalLogs += user.getLogsAtDate(date);
            }
            series.getData().add(new XYChart.Data<String, Number>(date.format(formatter), totalLogs));
        }
        chrtMainChart.getData().add(series);
        chrtMainChart.setCreateSymbols(false);
    }
    public void calculateSummaryData(){
        setOverallEarliestDate();
        setOverallLatestDate();
        for(User user : userCollection.userList){
            user.calculateTimeInBetween();
        }
    }
    public void setOverallEarliestDate(){
        LocalDate date = listOfUsers.get(0).getEarliestDate();
        for (User user : listOfUsers) {
            if(user.getEarliestDate().isBefore(date)){
                date = user.getEarliestDate();
            }
        }
        earliestDate = date;
    }
    public void setOverallLatestDate(){
        LocalDate date = listOfUsers.get(0).getLatestDate();
        for (User user : listOfUsers) {
            if(user.getLatestDate().isAfter(date)){
                date = user.getLatestDate();
            }
        }
        latestDate = date;
    }
    public void uploadFile(){
        File selectedFile = Viewer.openFileSelecter();
        if(selectedFile == null) return;
        DocumentReader.reset();
        userCollection = DocumentReader.getUserCollection(selectedFile);
        start();
    }
    public void uploadMultipleFiles(){
        List<File> selectedFiles = Viewer.openMultipleFiles();
        if(selectedFiles == null) return;
        for(File file : selectedFiles){
            userCollection = DocumentReader.getUserCollection(file);
        }
        start();
    }
    //opens a new export window to configure export options
    public void exportData(){
        if(userCollection == null) return;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("export.fxml"));
            Parent root = loader.load();
            ExportController controller = loader.getController();
            controller.load(userCollection);
            
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setTitle("Export Data");
            Image icon = new Image("https://images.crunchbase.com/image/upload/c_pad,h_256,w_256,f_auto,q_auto:eco,dpr_1/mky0fkibqswnxfbvhk3i");
            newStage.getIcons().add(icon);
            newStage.setResizable(false);
            newStage.setScene(scene);
            newStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
