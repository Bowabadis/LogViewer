
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class UserController implements Initializable {

    @FXML
    private Label lblUserLogin;

    @FXML
    private Label lblUserName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    //sets proper name and login number during creation
    public void setUserInfo(String name, int logins){
        lblUserName.setText(name);
        lblUserLogin.setText(Integer.toString(logins));
    }
    
}
