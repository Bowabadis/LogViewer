package ReaderFiles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

//converts LocalDateTime objects to string for the XML marshaller
public class DateAdapter extends XmlAdapter<String, LocalDateTime> {

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return LocalDateTime.parse(v, dateFormat);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return v.format(dateFormat);
    }
}