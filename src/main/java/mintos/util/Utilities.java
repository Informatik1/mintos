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

  private static Set<String> totalInterest = new HashSet<String>();
  private static Set<String> totalIncome = new HashSet<String>();
  private static Set<String> totalOutcome = new HashSet<String>();
  private static Set<String> exp = new HashSet<String>();
  private static Set<String> fees = new HashSet<String>();


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

  public static Set<String> getTotalIncomeTypes() {
    if(CollectionUtils.isEmpty(totalIncome)) {
      totalIncome.add("Investment principal repayment");
      
      totalIncome.add("Interest income");
      totalIncome.add("Delayed interest income");      
      totalIncome.add("Interest income on rebuy");
      totalIncome.add("Delayed interest income on rebuy");
      totalIncome.add("Late payment fee income");
      totalIncome.add("Discount/premium for secondary market transaction");


      totalIncome.add("Reversed late payment fee income");
      totalIncome.add("Investment principal rebuy Rebuy");
      totalIncome.add("Investment principal repayment");
      
//      totalInterest.add("Refer a friend bonus");
//      totalInterest.add("Affiliate bonus");
    }
    return totalIncome;
  }

  public static Set<String> getTotalInterestTypes() {
    if(CollectionUtils.isEmpty(totalInterest)) {
      totalInterest.add("Interest income");
      totalInterest.add("Delayed interest income");      
      totalInterest.add("Interest income on rebuy");
      totalInterest.add("Delayed interest income on rebuy");
      totalInterest.add("Late payment fee income");
      totalInterest.add("Bad debt");      
      totalInterest.add("Discount/premium for secondary market transaction");
      totalInterest.add("FX commission");
      totalInterest.add("Secondary market fee");
      totalInterest.add("Reversed late payment fee income");
      
//      totalInterest.add("Cashback bonus");
//      totalInterest.add("Refer a friend bonus");
//      totalInterest.add("Affiliate bonus");
    }
    return totalInterest;
  }


  public static Set<String> getTotalOutcomeTypes() {
    if(CollectionUtils.isEmpty(totalOutcome)) {
      totalOutcome.add("Investment principal rebuy Rebuy");
      totalOutcome.add("Investment principal repayment");
      totalOutcome.add("Withdraw application");

    }
    return totalOutcome;
  }


  public static Set<String> getTotalExpensesTypes() {
    if(CollectionUtils.isEmpty(exp)) {
      exp.add("Investment principal rebuy Rebuy");
      exp.add("Investment principal repayment");
    }
    return exp;
  }


  public static Set<String> getTotalFeesTypes() {
    if(CollectionUtils.isEmpty(fees)) {
      fees.add("Secondary market fee");
      fees.add("FX commission");
      fees.add("Bad debt");
    }
    return fees;
  }


  public static Date asDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

}
