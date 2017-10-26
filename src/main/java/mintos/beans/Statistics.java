package mintos.beans;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import mintos.util.Utilities;


public class Statistics {

  private SimpleIntegerProperty month;
  private SimpleIntegerProperty year;
  private NumberBinding invested;
  private SimpleDoubleProperty incommingPayment;
  private SimpleDoubleProperty expectedRate;
  private NumberBinding expectedInvestment;
  private SimpleDoubleProperty realMoney;
  private NumberBinding expectedMoney;
  private SimpleDoubleProperty realIncome;
  private NumberBinding profit;
  private NumberBinding investedHiden;


  public Statistics(
      NumberBinding invested, double incommingPayment, double expectedRate, double expectedInvestment, double realMoney, double expectedMoney,
      double realIncome, double profit, int month, int year) {
    super();
    this.invested = invested;
    this.incommingPayment = new SimpleDoubleProperty(incommingPayment);
    this.expectedRate = new SimpleDoubleProperty(expectedRate);
    this.expectedInvestment = Bindings.add(new SimpleDoubleProperty(expectedInvestment), 0);
    this.realMoney = new SimpleDoubleProperty(realMoney);
    this.expectedMoney = Bindings.add(new SimpleDoubleProperty(expectedMoney), 0);
    this.realIncome = new SimpleDoubleProperty(realIncome);
    this.month = new SimpleIntegerProperty(month);
    this.year = new SimpleIntegerProperty(year);
    this.profit = Bindings.add(new SimpleDoubleProperty(profit), 0);
  }


  public double getInvested() {
    return invested.doubleValue();
  }


  public NumberBinding getInvestedBinding() {
    return invested;
  }


  public double getIncommingPayment() {
    return incommingPayment.doubleValue();
  }


  public SimpleDoubleProperty getIncommingPaymentProperty() {
    return incommingPayment;
  }


  public void setIncommingPayment(double value) {
    this.incommingPayment.set(value);
  }


  public double getExpectedRate() {
    return expectedRate.get();
  }


  public SimpleDoubleProperty getExpectedRateProperty() {
    return expectedRate;
  }


  public void setExpectedRate(double expectedRate) {
    this.expectedRate.set(expectedRate);
  }


  public double getExpectedInvestment() {
    return expectedInvestment.doubleValue();
  }


  public NumberBinding getExpectedInvestmentBinding() {
    return expectedInvestment;
  }


  public void setExpectedInvestmentBinding(NumberBinding expectedInvestment) {
    this.expectedInvestment = expectedInvestment;
  }


  public void setExpectedInvestment(double value) {
    this.expectedInvestment = Bindings.add(new SimpleDoubleProperty(value), 0);
  }


  public double getRealMoney() {
    return realMoney.get();
  }


  public void setRealMoney(double realMoney) {
    this.realMoney.set(realMoney);
  }


  public double getExpectedMoney() {
    return expectedMoney.doubleValue();
  }


  public void setExpectedMoneyBinding(NumberBinding expectedMoney) {
    this.expectedMoney = expectedMoney;
  }


  public void setRealIncome(double realIncome) {
    this.realIncome.set(realIncome);
  }


  public double getRealIncome() {
    return realIncome.get();
  }


  public int getMonth() {
    return month.get();
  }


  public void setMonth(int month) {
    this.month.set(month);
  }


  public int getYear() {
    return year.get();
  }


  public void setYear(int year) {
    this.year.set(year);
  }


  public String getDate() {
    return Utilities.getMonthForInt(month.get() - 1) + " " + year.get();
  }


  public double getProfit() {
    return profit.doubleValue();
  }


  public void setProfitBinding(NumberBinding profit) {
    this.profit = profit;
  }


  public NumberBinding getInvestedHiden() {
    return investedHiden;
  }


  public void setInvestedHiden(NumberBinding investedHiden) {
    this.investedHiden = investedHiden;
  }


  @Override
  public String toString() {
    return "Statistics [month=" + month + ", year=" + year + ", invested=" + invested + ", incommingPayment=" + incommingPayment + ", expectedRate=" +
        expectedRate + ", expectedInvestment=" + expectedInvestment + ", realMoney=" + realMoney + ", expectedMoney=" + expectedMoney +
        ", realIncome=" + realIncome + "]";
  }

}
