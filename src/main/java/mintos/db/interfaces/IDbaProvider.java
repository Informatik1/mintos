package mintos.db.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mintos.beans.Transaction;
import mintos.beans.TransactionStatistics;


public interface IDbaProvider {

  public List<String> getAllCategories();


  public List<Transaction> getAllTransactions(String currencyId, String year, String month, String detailType);


  public TransactionStatistics getTransactionStatistics(String currencyId, String year, String month, Set<String> detailTypes);


  public void insertTransactions(List<Transaction> list);


  public Map<String, List<String>> getAllMonths();


  public List<String> getAllCurrencies();


  public void removeAllTransactions();


  public Map<String, Double> getMontStatisticsOrderTypes(String currencyId, Set<String> detailTypes);


  public void addInvestment(Date date, Double investment);


  public double getInvestment(int year, int month);

}
