# JavaBusinessCalendar
Business Calendar implementation in Java

This api is based on JBPM Business Calendar.

Usage 

###BusinessCalendar bc =  BusinessCalendar(properties);
properties location to calendar property file see example property file.

###bc.isHoliday(date);
Calendar date - returns if give date is holiday or not.


###bc.isTodayHoliday();
returns if give today is holiday or not.

###bc.isWorkingHour(date); 
Calendar date - returns if give time is working hour or not.


###bc.isWorkingHourNow();
returns if current time is working hour or not.


###bc.today();
returns current date with busnienss calendar timezone.


###bc.stringToCalendar("1/5/2005");
converts string to calendar (format as per the property file).
