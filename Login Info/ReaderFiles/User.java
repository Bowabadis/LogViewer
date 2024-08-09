package ReaderFiles;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    
    @XmlElement(name =  "name")
    private String name;
    @XmlTransient
    private int maxLogs;
    @XmlElement(name =  "logAmount")
    private int logAttempts = 0;
    @XmlTransient
    private int ins = 0, outs = 0;
    @XmlElement(name = "info")
    private ArrayList<LogInfo> logs = new ArrayList<LogInfo>();

    public User(String name){
        this.name = name;
    }
    public User(){
        //need a blank constructor for javax.xml
    }

    public void newAccess(LogInfo info){
        if(info.isLogin()){
            ins++;
        }else{
            outs++;
        }
        logAttempts++;
        logs.add(info);
    }
    
    public String getName(){

        return name;
    }
    public ArrayList<LogInfo> getLogs(){
        return logs;
    }


    public int getIns(){
        return ins;
    }
    public int getOuts(){
        return outs;
    }
    public int getLogAttempts(){
        return logAttempts;
    }
    //returns earliest date of all the logs
    public LocalDate getEarliestDate(){
        LocalDate date = null;
        if(logs.size()>0) {
            date = logs.get(0).getLocalDateTime().toLocalDate();
        }
        for (LogInfo info : logs) {
            if(info.getLocalDateTime().toLocalDate().isBefore(date)){
                date = info.getLocalDateTime().toLocalDate();
            }
        }
        return date;
    }
    //returns latest date of all the logs
    public LocalDate getLatestDate(){
        LocalDate date = null;
        if(logs.size()>0) {
            date = logs.get(0).getLocalDateTime().toLocalDate();
        }
        for (LogInfo info : logs) {
            if(info.getLocalDateTime().toLocalDate().isAfter(date)){
                date = info.getLocalDateTime().toLocalDate();
            }
        }
        return date;
    }
    //days between earliest date and latest date -> to generate a data range for the graph
    public int getDaysBetween(){
        return (int) getEarliestDate().until(getLatestDate(), ChronoUnit.DAYS);
    }
    public int getMaxLogs(){
        int max = 0;
        int currentLogs = 0;
        LocalDate currentDate = getEarliestDate();
        for(int i = 0; i<getDaysBetween(); i++){
            for (LogInfo inInfo : logs) {
                if(inInfo.getLocalDateTime().toLocalDate().isEqual(currentDate)){
                    //matches current date
                    currentLogs++;
                }
            }
            currentDate.plusDays(1);
            if(currentLogs > max) max=currentLogs;
            currentLogs = 0;
        }
        return max;
    }
    //returns nubmer of times the user logged in a given day
    public int getLogsAtDate(LocalDate date){
        int i = 0;
        for (LogInfo inInfo : logs) {
            if(inInfo.getLocalDateTime().toLocalDate().isEqual(date)){
                //matches current date
                i++;
            }
        }
        return i;
    }
    
    public void calculateTimeInBetween(){
        LocalDateTime lastIn = null;
        for(LogInfo info : logs){
                if(info.isLogin()){
                    //IN type access
                    lastIn = info.getLocalDateTime();
                }else if(lastIn != null){
                    //OUT type access
                    info.setTimeBetween(Duration.between(lastIn, info.getLocalDateTime()));
                    lastIn = null;
                }
        }
    }
}
