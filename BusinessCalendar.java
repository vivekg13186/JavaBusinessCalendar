
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

public class BusinessCalendar {

    TimeZone tz;
    SimpleDateFormat formatter;
    ArrayList<Calendar> holidays = new ArrayList();
    ArrayList<WorkingHour> sundayWorkingHour;
    ArrayList<WorkingHour> mondayWorkingHour;
    ArrayList<WorkingHour> tuesdayWorkingHour;
    ArrayList<WorkingHour> weddayWorkingHour;
    ArrayList<WorkingHour> thursdayWorkingHour;
    ArrayList<WorkingHour> fridayWorkingHour;
    ArrayList<WorkingHour> satdayWorkingHour;

    public BusinessCalendar(Properties properties) throws Exception {
        loadBusinessCalendarFromPropertyFile(properties);
    }

    private class WorkingHour {

        public WorkingHour(double from, double to) {
            this.from = from;
            this.to = to;
        }

        public double from;
        public double to;
    }

    public Calendar stringToCalendar(String str) throws ParseException {
        Calendar cal = new GregorianCalendar();
        cal.setTime(formatter.parse(str));
        cal.setTimeZone(tz);
        return cal;
    }

    public Calendar today() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(tz);
        return cal;
    }

    private void loadHolidays(Properties prop) throws ParseException {
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith("holiday")) {
                Calendar c = stringToCalendar(prop.getProperty(key));
                holidays.add(c);
            }
        }
    }

    private ArrayList<WorkingHour> loadWorkingHour(String timeExpression) {
        if (timeExpression.isEmpty()) {
            return null;
        }
        String[] schedules = timeExpression.split(" \\& ");
        ArrayList<WorkingHour> whl = new ArrayList<>();

        for (String schedule : schedules) {
            String[] time = schedule.split("-");
            double fromTime = Double.parseDouble(time[0]);
            double toTime = Double.parseDouble(time[1]);
            whl.add(new WorkingHour(fromTime, toTime));
        }
        return whl;
    }

    private void loadWorkingHours(Properties prop) throws ParseException {
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith("weekday")) {
                ArrayList<WorkingHour> list = loadWorkingHour(prop.getProperty(key));
                switch (key) {
                    case "weekday.sunday":
                        sundayWorkingHour = list;
                        break;
                    case "weekday.monday":
                        mondayWorkingHour = list;
                        break;
                    case "weekday.tuesday":
                        tuesdayWorkingHour = list;
                        break;
                    case "weekday.wednesday":
                        weddayWorkingHour = list;
                        break;
                    case "weekday.thursday":
                        thursdayWorkingHour = list;
                        break;
                    case "weekday.friday":
                        fridayWorkingHour = list;
                        break;
                    case "weekday.saturday":
                        satdayWorkingHour = list;
                        break;
                }
            }
        }
    }

    private void loadBusinessCalendarFromPropertyFile(Properties prop) throws Exception {
        String timeZone = prop.getProperty("calendar.timezone");
        if (timeZone == null || timeZone.isEmpty()) {
            throw new Exception("calendar.timezone property is not defined");
        }
        tz = TimeZone.getTimeZone(timeZone);

        String dateFormat = prop.getProperty("day.format");
        if (dateFormat == null || dateFormat.isEmpty()) {
            throw new Exception("day.format property is not defined");
        }
        formatter = new SimpleDateFormat(dateFormat);

        loadHolidays(prop);

        loadWorkingHours(prop);
    }

    private boolean isSameDate(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private ArrayList<WorkingHour> getWorkingHour(int day_of_week) {
        switch (day_of_week) {
            case 1:
                return sundayWorkingHour;
            case 2:
                return mondayWorkingHour;
            case 3:
                return tuesdayWorkingHour;
            case 4:
                return weddayWorkingHour;
            case 5:
                return thursdayWorkingHour;
            case 6:
                return fridayWorkingHour;
            case 7:
                return satdayWorkingHour;
            default:
                return null;
        }
    }

    public boolean isHoliday(Calendar date) {
        for (Calendar c : holidays) {
            if (isSameDate(c, date)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTodayHoliday() {
        return isHoliday(today());
    }

    public boolean isWorkingHour(Calendar c) {
        ArrayList<WorkingHour> whl = getWorkingHour(c.get(Calendar.DAY_OF_WEEK));
        if (whl == null) {
            return false;
        }
        int matchHour = c.get(Calendar.HOUR_OF_DAY);
        int matchMin = c.get(Calendar.MINUTE);
        double matchTime = Double.parseDouble(matchHour + "." + matchMin);

        for (WorkingHour wh : whl) {
            if (matchTime >= wh.from && matchTime <= wh.to) {
                return true;
            }
        }
        return false;
    }

    public boolean isWorkingHourNow() {
        return isWorkingHour(today());
    }

}
