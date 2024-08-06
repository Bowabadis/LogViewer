package ReaderFiles;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class LogInfo {
    @XmlTransient
    private boolean in;
    @XmlElement(name = "type")
    private String logString;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime dateTime;
    @XmlTransient
    private Duration timeBetween;
    private String permission;
    @XmlTransient
    private boolean hasTimeBetween;
    public LogInfo(boolean in, LocalDateTime dateTime, String permission){
        this.in = in;
        setLogString();
        this.dateTime = dateTime;
        this.permission = permission;
    }
    public LogInfo(){
        //need a blank constructor for javax.xml
    }
    public LocalDateTime getLocalDateTime(){
        return dateTime;
    }
    public String getPermission(){
        return permission;
    }
    public boolean isLogin(){
        //true if its a login and false if its a logout
        return in;
    }
    public void setLogString(){
        logString = "OUT";
        if(in) logString = "IN";
    }
    public void setTimeBetween(Duration time){
        timeBetween = time;
    }
    //the time in between a log type IN and the next proceeding type OUT
    @XmlElement(name = "timeOnline")
    public String getTimeBetween(){
        if(timeBetween != null){
            hasTimeBetween = true;
            long s = timeBetween.getSeconds();
            return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
        }else{
            hasTimeBetween = false;
            return "";
        }
    }
    public boolean hasTimeBetween(){
        return hasTimeBetween;
    }
}
