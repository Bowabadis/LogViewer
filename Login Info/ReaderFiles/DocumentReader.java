package ReaderFiles;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class DocumentReader{

    static UserCollection userCollection = new UserCollection();
    static LocalDate currentDate;
    static LocalTime previousTime;
    static int line = 0; //used for debugging
    public static void readDocument(File file){
        previousTime = LocalTime.parse("00:00:01");
        try{
            FileInputStream fStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            String strLine;
            //reads log line by line
            while ((strLine = br.readLine()) != null){
                line++;
                login(strLine);
            }
            br.close();
        }catch(Exception e){
            System.err.println("Oopsies!! Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void reset(){
        userCollection = new UserCollection();
        currentDate = null;
        previousTime = null;
        line = 0;
    }

    static LocalDateTime formatDateTime(String date){
        date = date.substring(4);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");
        return LocalDateTime.parse(date, dateTimeFormatter);
    }
    static LocalTime formatTime(String time){
        if(time.length() < 8) time = "0"+time;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(time, dateTimeFormatter);
    }

    static void login(String input){
        //checks if its a login, logout, time, or neither
        if(!input.contains("IN:") && !input.contains("OUT:") && !input.contains("Time:")) return; //Could add "TIMESTAMP " in the future for more error protection
        
        //converts the time text into objects
        String lineTime = input.substring(0,input.indexOf(" ",1));
        lineTime = lineTime.replace(' ', '0');
        LocalTime loginTime = formatTime(lineTime);
        String lookFor = "Time: ";
            if(input.contains(lookFor)){
                int i = input.indexOf(lookFor) + lookFor.length();
                String time = input.substring(i, i + 24);
                currentDate = formatDateTime(time).toLocalDate();
                previousTime = loginTime;
            }
            
        //check if time has moved to next day
        if(previousTime.toSecondOfDay() > loginTime.toSecondOfDay() && !input.contains("Time:")){
            currentDate = currentDate.plusDays(1);
        }
        LocalDateTime localDateTime = loginTime.atDate(currentDate);

        //sets the type of access - either in or out
        int accessStringStart = input.indexOf(") ")+2, accessStringEnd = input.indexOf(" ", input.indexOf(") ")+3);
        String typeOfAccess = input.substring(accessStringStart, accessStringEnd);
        if(!typeOfAccess.equals("IN:") && !typeOfAccess.equals("OUT:")) return;
        boolean isLogin = typeOfAccess.equals("IN:");

        //gets the permission type
        int permissionEnd = input.indexOf(" ", accessStringEnd+1);
        String permission = input.substring(accessStringEnd+1, permissionEnd);

        //gets the name of the user
        String userName = input.substring(permissionEnd+1, input.indexOf(" ", permissionEnd+1));


        if(!userCollection.containsUser(userName)){
            //add new user
            userCollection.add(userName, new LogInfo(isLogin, localDateTime, permission));
        }else{
            //update user info
            userCollection.update(userName, new LogInfo(isLogin, localDateTime, permission));
        }
        previousTime = loginTime;

    }
    
    public static UserCollection getUserCollection(File file){
        readDocument(file);
        return userCollection;
    }

}