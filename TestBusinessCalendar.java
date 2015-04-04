
import java.io.IOException;
import java.util.Properties;


public class TestBusinessCalendar {

    public static void main(String[] arg) throws IOException, Exception {
        Properties prop = new Properties();
        prop.load(TestBusinessCalendar.class.getResourceAsStream("mycalendar.properties"));
        BusinessCalendar bc  = new BusinessCalendar(prop);
        
        System.err.printf("today %s \n", bc.isTodayHoliday());
       
        System.err.printf("today working %s \n", bc.isWorkingHourNow());
    }
}
