
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import ReaderFiles.LogInfo;
import ReaderFiles.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ExtraInfoHolderController implements Initializable {

    @FXML
    private AnchorPane aPaneExtraInfoContainer;

    @FXML
    private Button btnLastPage;

    @FXML
    private Button btnNextPage;

    @FXML
    private Label lblPageLabel;

    @FXML
    private Label lblInfoLabel;

    @FXML
    private ToggleButton togBtnSimplify;

    @FXML
    private VBox vExtraInfo;

    User selectedUser;
    int numberOfLogs;

    //can change or make variable in the future
    final int MAX_ITEMS_PER_PAGE = 1000;
    private int pageNum = 0, pageMin, pageMax;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    //called from extraInfoHolder.fxml
    public void nextPage(){
        if(((pageNum+1) * MAX_ITEMS_PER_PAGE) < numberOfLogs){
            pageNum++;
            btnLastPage.setVisible(true);
        }else{
            btnNextPage.setVisible(false);
        }
        updateLabel();
    }
    //called from extraInfoHolder.fxml
    public void lastPage(){
        if(pageNum > 0){
            pageNum--;
            btnNextPage.setVisible(true);
        }else{
            btnLastPage.setVisible(false);
        }
        updateLabel();
    }
    //page results text display
    public void updateLabel(){
        pageMin = (pageNum * MAX_ITEMS_PER_PAGE)+1;
        pageMax = ((pageNum+1)*MAX_ITEMS_PER_PAGE);
        
        if(pageNum == 0){
            btnLastPage.setVisible(false);
        }
        if(pageMax > numberOfLogs){
            btnNextPage.setVisible(false);
        }
        
        if(pageMax > numberOfLogs) pageMax = numberOfLogs;
        lblPageLabel.setText("Showing "+(pageMin) + " - "+ (pageMax));

        generateLogsFromRange();
    }

    public void setUser(User u){
        selectedUser = u;
        pageNum = 0;
        numberOfLogs = selectedUser.getLogs().size();
        
        updateLabel();
        
    }
    //loads either all the logs or only the simplified OUT logs (has time between only if there is an OUT log following an IN log)
    public void generateLogsFromRange(){
        String infoString = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss - MMM dd");
        for (int i = pageMin-1; i<pageMax; i++) {
            
                LogInfo currLog = selectedUser.getLogs().get(i);
                
                String inString = "Out";
                String timeOnline = "";
                if(!currLog.getTimeBetween().toString().equals("")) timeOnline = "Time Online: "+timeOnline + currLog.getTimeBetween() + "\n";
                if(currLog.isLogin()) inString = "In";
                if(!togBtnSimplify.isSelected()){
                    infoString += inString + ": "+ currLog.getLocalDateTime().format(formatter) + "\n" + timeOnline + "\n";
                }else{
                    if(currLog.hasTimeBetween()){
                        infoString += inString + ": "+ currLog.getLocalDateTime().format(formatter) + "\n" + timeOnline + "\n";
                    }
                }
            
        }

        lblInfoLabel.setText(infoString);
    }
    
}
