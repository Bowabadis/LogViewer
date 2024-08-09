package ReaderFiles;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
//Sotres an Array List of all the user objects
public class UserCollection {


    @XmlElement(name = "user")
    public ArrayList<User> userList = new ArrayList<>();

    public void add(String name, LogInfo info){
        userList.add(new User(name));
        update(name, info);
    }
    
    public void update(String name, LogInfo info){
        //add new access info to the user object
        userList.get(getUserNameIndex(name)).newAccess(info);
    }

    public boolean containsUser(String name){
        for (User user : userList) {
            if(user.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public int getUserNameIndex(String name){
        for(int i = 0; i<userList.size(); i++){
            if(userList.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }

    //Display Methods
    public void printUsers(){
        for (User user : userList) {
            System.out.println("User Name: "+user.getName()+" Ins: "+user.getIns() + " Outs: "+user.getOuts());
        }
    }

}
