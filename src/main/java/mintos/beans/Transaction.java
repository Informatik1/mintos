package mintos.beans;

import java.sql.Timestamp;


public class Transaction {

  private Long transactionId;
  private Timestamp date;
  private String details;
  private String detailsType;
  private String loanId;
  private Double turnover;
  private Double balance;
  private String currency;
  private Double exchangeRate;


  public Transaction() {}


  public Transaction(String detailType) {
    this.detailsType = detailType;
  }


  public Long getTransactionId() {
    return transactionId;
  }


  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }


  public Timestamp getDate() {
    return date;
  }


  public void setDate(Timestamp date) {
    this.date = date;
  }


  public String getDetails() {
    return details;
  }


  public void setDetails(String details) {
    this.details = details;
  }


  public String getDetailsType() {
    return detailsType;
  }


  public void setDetailsType(String detailsType) {
    this.detailsType = detailsType;
  }


  public String getLoanId() {
    return loanId;
  }


  public void setLoanId(String loanId) {
    this.loanId = loanId;
  }


  public Double getTurnover() {
    return turnover;
  }


  public void setTurnover(Double turnover) {
    this.turnover = turnover;
  }


  public Double getBalance() {
    return balance;
  }


  public void setBalance(Double balance) {
    this.balance = balance;
  }


  public String getCurrency() {
    return currency;
  }


  public void setCurrency(String currency) {
    this.currency = currency;
  }


  public void setExchangeRate(Double exchangeRate) {
    this.exchangeRate = exchangeRate;
  }


  public Double getExchangeRate() {
    return exchangeRate;
  }


  @Override
  public String toString() {
    return "Transaction [transactionId=" + transactionId + ", date=" + date + ", details=" + details + ", detailsType=" + detailsType + ", loanId=" +
        loanId + ", turnover=" + turnover + ", balance=" + balance + ", currency=" + currency + ", exchangeRate=" + exchangeRate + "]";
  }

}
