package mintos.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Utilities {

  private static Log log = LogFactory.getLog(Utilities.class);

  private static Set<String> totalInterest = new HashSet<>();
  private static Set<String> totalOutcome = new HashSet<>();


  public static String getMonthForInt(int num) {
    String month = "wrong";
    DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
    String[] months = dfs.getMonths();
    if(num >= 0 && num <= 11) {
      month = months[num];
    }
    return month;
  }


  public static int getMonth(String monthName) {
    java.util.Date date = null;
    try {
      date = new SimpleDateFormat("MMMM", Locale.US).parse(monthName);
    }
    catch(ParseException e) {
      log.error("Error: " + e, e);
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return (cal.get(Calendar.MONTH) + 1);
  }


  public static Set<String> getTotalInterestTypes() {
    if(CollectionUtils.isEmpty(totalInterest)) {
      totalInterest.add("Interest income");
      totalInterest.add("Delayed interest income");
      totalInterest.add("Late payment fee income");
      totalInterest.add("Interest income on rebuy");
      totalInterest.add("Delayed interest income on rebuy");

      totalInterest.add("Discount/premium for secondary market transaction");
      totalInterest.add("Secondary market fee");
      totalInterest.add("FX commission");
    }
    return totalInterest;
  }


  public static Set<String> getTotalOutcomeTypes() {
    if(CollectionUtils.isEmpty(totalOutcome)) {
      totalOutcome.add("Investment principal rebuy Rebuy");
      totalOutcome.add("Investment principal repayment");

    }
    return totalOutcome;
  }


  public static Date asDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

}
