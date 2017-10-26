package mintos.beans;

public class TransactionStatistics {

  private int transactionsCount;
  private double turnoverSum;
  private double balanceSum;


  public TransactionStatistics(int transactionsCount, double turnoverSum, double balanceSum) {
    super();
    this.transactionsCount = transactionsCount;
    this.turnoverSum = turnoverSum;
    this.balanceSum = balanceSum;
  }


  public int getTransactionsCount() {
    return transactionsCount;
  }


  public void setTransactionsCount(int transactionsCount) {
    this.transactionsCount = transactionsCount;
  }


  public double getTurnoverSum() {
    return turnoverSum;
  }


  public void setTurnoverSum(double turnoverSum) {
    this.turnoverSum = turnoverSum;
  }


  public double getBalanceSum() {
    return balanceSum;
  }


  public void setBalanceSum(double balanceSum) {
    this.balanceSum = balanceSum;
  }

}
