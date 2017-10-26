package mintos.db.impl.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import mintos.beans.Transaction;
import mintos.beans.TransactionStatistics;
import mintos.db.interfaces.IDbaProvider;
import mintos.util.Utilities;


public class DbaProvider_Mssql implements IDbaProvider {

  private static Log log = LogFactory.getLog(DbaProvider_Mssql.class);

  private String userName;
  private String password;
  private String url;
  private String className;


  @Required
  public void setUserName(String userName) {
    this.userName = userName;
  }


  @Required
  public void setPassword(String password) {
    this.password = password;
  }


  @Required
  public void setUrl(String url) {
    this.url = url;
  }


  @Required
  public void setClassName(String className) {
    this.className = className;
  }

  Connection conn;
  private static final String SQL_LOG_MESSAGE = "Execution query: \"{0}\" . Arguments: \"{1}\"";


  public Connection getConnection() {
    try {
      Class.forName(className);
      conn = DriverManager.getConnection(url, userName, password);
    }
    catch(ClassNotFoundException e) {
      log.error(e.getMessage(), e);
    }
    catch(SQLException e) {
      log.error(e.getMessage(), e);
    }
    return conn;
  }


  public void closeConnection() {
    if(conn != null) {
      try {
        conn.close();
      }
      catch(SQLException e) {
        log.error(e.getMessage(), e);
      }
    }
  }


  private String convertToIn(Set<String> detailTypes) {
    StringBuilder sb = new StringBuilder();
    int index = 0;
    for(String type : detailTypes) {
      sb.append("?");
      if(index < (detailTypes.size() - 1)) {
        sb.append(", ");
      }
      index++;
    }
    return sb.toString();
  }


  public Map<String, Double> getMontStatisticsOrderTypes(String currencyId, Set<String> detailTypes) {
    Map<String, Double> map = new LinkedHashMap<String, Double>();
    try {
      Connection c = getConnection();
      int index = 1;
      String sql = " SELECT" + //
          "    { fn MONTHNAME(DATE) }  AS MonthName," + //
          "    YEAR(TRANSACTIONS.DATE) AS YEAR," + //
          "    SUM(TURNOVER)" + //
          " FROM" + //
          "    TRANSACTIONS" + //
          " WHERE" + //
          "    CURRENCY = ? " + //
          " AND DETAILS_TYPE IN (" + convertToIn(detailTypes) + ")" + //
          " GROUP BY" + //
          "    { fn MONTHNAME(TRANSACTIONS.DATE) }," + //
          "    MONTH(TRANSACTIONS.DATE)," + //
          "    YEAR(TRANSACTIONS.DATE)" + //
          " ORDER BY" + //
          "    YEAR(TRANSACTIONS.DATE)," + //
          "    MONTH(TRANSACTIONS.DATE)";// f

      PreparedStatement ps = c.prepareStatement(sql);

      ps.setString(index, currencyId);
      index++;

      if(!CollectionUtils.isEmpty(detailTypes)) {
        for(String type : detailTypes) {
          ps.setString(index, type);
          index++;
        }
      }

      log.debug(MessageFormat.format(SQL_LOG_MESSAGE, sql, "currencyId=" + currencyId + ", detailTypes=" + detailTypes));
      ResultSet rs = ps.executeQuery();
      while(rs.next()) {
        String month = rs.getString(1);
        // String year = rs.getString(2);
        Double interest = rs.getDouble(3);
        map.put(month, interest);
      }
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }

    return map;
  }


  @Override
  public Map<String, List<String>> getAllMonths() {
    Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
    try {
      Connection c = getConnection();

      String sql = " SELECT" + //
          " { fn MONTHNAME(TRANSACTIONS.DATE) } AS MonthName," + //
          " YEAR(TRANSACTIONS.DATE)             AS YEAR" + //
          " ,SUM(TRANSACTIONS.BALANCE)             AS Balance" + //
          " FROM" + //
          " TRANSACTIONS" + //
          " WHERE" + //
          " (" + //
          "     YEAR(TRANSACTIONS.DATE) >= '2017')" + //
          " GROUP BY" + //
          " { fn MONTHNAME(TRANSACTIONS.DATE) }," + //
          " MONTH(TRANSACTIONS.DATE)," + //
          " YEAR(TRANSACTIONS.DATE)" + //
          " ORDER BY" + //
          " YEAR(TRANSACTIONS.DATE)," + //
          " MONTH(TRANSACTIONS.DATE)";
      PreparedStatement ps = c.prepareStatement(sql);

      log.debug(MessageFormat.format(SQL_LOG_MESSAGE, sql, "NONE"));
      ResultSet rs = ps.executeQuery();

      while(rs.next()) {
        String month = rs.getString(1);
        String year = rs.getString(2);
        addMonthToMap(map, year, month);
      }
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
    return map;
  }


  private void addMonthToMap(Map<String, List<String>> map, String year, String month) {
    List<String> list = map.get(year);

    if(list == null) {
      list = new ArrayList<>();
    }
    list.add(month);
    map.put(year, list);
  }


  @Override
  public List<String> getAllCategories() {
    List<String> list = new ArrayList<>();
    try {
      Connection c = getConnection();
      String sql = "SELECT DISTINCT DETAILS_TYPE FROM TRANSACTIONS";
      PreparedStatement ps = c.prepareStatement(sql);

      log.debug(MessageFormat.format(SQL_LOG_MESSAGE, sql, "NONE"));
      ResultSet rs = ps.executeQuery();
      while(rs.next()) {
        list.add(rs.getString(1));
      }
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
    return list;
  }


  @Override
  public List<String> getAllCurrencies() {
    List<String> list = new ArrayList<String>();
    try {
      Connection c = getConnection();
      String sql = "SELECT DISTINCT CURRENCY FROM TRANSACTIONS";
      PreparedStatement ps = c.prepareStatement(sql);

      log.debug(MessageFormat.format(SQL_LOG_MESSAGE, sql, "NONE"));
      ResultSet rs = ps.executeQuery();
      while(rs.next()) {
        list.add(rs.getString(1));
      }
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
    return list;
  }


  @Override
  public TransactionStatistics getTransactionStatistics(String currencyId, String year, String month, Set<String> detailTypes) {
    try {
      Connection c = getConnection();
      int index = 1;

      String sql = "SELECT count(TRANSACTION_ID)," + "    sum(TURNOVER), sum(BALANCE)  FROM TRANSACTIONS WHERE CURRENCY = ?";
      if(year != null) {
        sql += " AND YEAR(TRANSACTIONS.DATE) = ?";
      }
      if(month != null) {
        sql += " AND MONTH(TRANSACTIONS.DATE) = ?";
      }
      if(!CollectionUtils.isEmpty(detailTypes)) {
        sql += "  AND DETAILS_TYPE in (" + convertToIn(detailTypes) + ") ";//
      }

      log.debug(
          MessageFormat
              .format(SQL_LOG_MESSAGE, sql, "currencyId=" + currencyId + ", year=" + year + ", month=" + month + ", detailTypes=" + detailTypes));

      PreparedStatement ps = c.prepareStatement(sql);

      ps.setString(index, currencyId);
      index++;

      if(year != null) {
        ps.setString(index, year);
        index++;
      }
      if(month != null) {
        ps.setInt(index, Utilities.getMonth(month));
        index++;
      }
      if(!CollectionUtils.isEmpty(detailTypes)) {
        for(String type : detailTypes) {
          ps.setString(index, type);
          index++;
        }
      }

      ResultSet rs = ps.executeQuery();
      rs.next();
      TransactionStatistics ts = new TransactionStatistics(rs.getInt(1), rs.getDouble(2), rs.getDouble(3));

      return ts;
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
    return null;
  }


  public List<Transaction> getAllTransactions(String currencyId, String year, String month, String detailType) {
    List<Transaction> list = new ArrayList<>();
    try {
      Connection c = getConnection();
      int index = 1;

      String sql = "SELECT TRANSACTION_ID, DATE, DETAILS, DETAILS_TYPE, LOAN_ID, TURNOVER, BALANCE, CURRENCY FROM TRANSACTIONS WHERE CURRENCY = ?";
      if(year != null) {
        sql += " AND  YEAR(TRANSACTIONS.DATE) = ?";
      }
      if(month != null) {
        sql += " AND  MONTH(TRANSACTIONS.DATE) = ?";
      }
      if(detailType != null) {
        sql += " AND DETAILS_TYPE = ?";
      }

      log.debug(
          MessageFormat
              .format(SQL_LOG_MESSAGE, sql, "currencyId=" + currencyId + ", year=" + year + ", month=" + month + ", detailType=" + detailType));

      PreparedStatement ps = c.prepareStatement(sql);

      ps.setString(index, currencyId);
      index++;

      if(year != null) {
        ps.setString(index, year);
        index++;
      }
      if(month != null) {
        ps.setInt(index, Utilities.getMonth(month));
        index++;
      }
      if(detailType != null) {
        ps.setString(index, detailType);
        index++;
      }

      ResultSet rs = ps.executeQuery();
      while(rs.next()) {
        Transaction tr = new Transaction();
        tr.setTransactionId(rs.getLong(1));
        tr.setDate(rs.getTimestamp(2));
        tr.setDetails(rs.getString(3));
        tr.setDetailsType(rs.getString(3));
        tr.setLoanId(rs.getString(5));
        tr.setTurnover(rs.getDouble(6));
        tr.setBalance(rs.getDouble(7));
        list.add(tr);
      }
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
    return list;
  }


  @Override
  public void addInvestment(Date date, Double investment) {
    try {
      log.debug("Inserting investment");
      Connection c = getConnection();
      PreparedStatement statement = c.prepareStatement("INSERT INTO INVESTMENT (DATE, INVESTMENT) " + "VALUES (?,?)");

      statement.setTimestamp(1, new Timestamp(date.getTime()));
      statement.setDouble(2, investment);
      statement.execute(); // Execute every 1000 items.
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
  }


  @Override
  public double getInvestment(int year, int month) {
    double investment = 0;
    try {
      Connection c = getConnection();

      String sql = "SELECT SUM(INVESTMENT) FROM INVESTMENT WHERE YEAR(INVESTMENT.DATE) = ?  AND  MONTH(INVESTMENT.DATE) = ?";

      log.debug(MessageFormat.format(SQL_LOG_MESSAGE, sql, "year=" + year + ", month=" + month));

      PreparedStatement ps = c.prepareStatement(sql);
      ps.setInt(1, year);
      ps.setInt(2, month);

      ResultSet rs = ps.executeQuery();
      rs.next();
      investment = rs.getDouble(1);
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
    return investment;
  }


  public void insertTransactions(List<Transaction> list) {
    try {
      log.debug("Inserting transactions");
      Connection c = getConnection();
      PreparedStatement statement1 = c.prepareStatement(
          "INSERT INTO TRANSACTIONS (TRANSACTION_ID, DATE, DETAILS, DETAILS_TYPE, LOAN_ID, TURNOVER, BALANCE, CURRENCY) " +
              "VALUES (?,?,?,?,?,?,?,?)");
      PreparedStatement statement2 = c.prepareStatement(
          "INSERT INTO TRANSACTIONS (TRANSACTION_ID, DATE, DETAILS, DETAILS_TYPE, LOAN_ID, TURNOVER, BALANCE, CURRENCY, EXCHANGE_RATE) " +
              "VALUES (?,?,?,?,?,?,?,?, ?)");

      int i = 0;
      int j = 1;

      for(Transaction entity : list) {
        try {
          if(entity.getExchangeRate() != null) {
            statement2.setLong(1, entity.getTransactionId());
            statement2.setTimestamp(2, entity.getDate());
            statement2.setString(3, entity.getDetails());
            statement2.setString(4, entity.getDetailsType());
            statement2.setString(5, entity.getLoanId());
            statement2.setDouble(6, entity.getTurnover());
            statement2.setDouble(7, entity.getBalance());
            statement2.setString(8, entity.getCurrency());
            statement2.setDouble(9, entity.getExchangeRate());
            statement2.addBatch();
          }
          else {
            statement1.setLong(1, entity.getTransactionId());
            statement1.setTimestamp(2, entity.getDate());
            statement1.setString(3, entity.getDetails());
            statement1.setString(4, entity.getDetailsType());
            statement1.setString(5, entity.getLoanId());
            statement1.setDouble(6, entity.getTurnover());
            statement1.setDouble(7, entity.getBalance());
            statement1.setString(8, entity.getCurrency());
            statement1.addBatch();
          }
        }
        catch(Exception e) {
          log.error("Error: " + e, e);
        }
        i++;

        if(i % 1000 == 0 || i == list.size()) {
          statement1.executeBatch(); // Execute every 1000 items.
          statement2.executeBatch(); // Execute every 1000 items.
          log.debug("Batch i=" + j);

          j++;
        }
      }
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }
  }


  public void removeAllTransactions() {
    try {
      Connection c = getConnection();
      String sql = "DELETE FROM TRANSACTIONS";
      PreparedStatement ps = c.prepareStatement(sql);

      log.debug(MessageFormat.format(SQL_LOG_MESSAGE, sql, "NONE"));
      ps.execute();
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      closeConnection();
    }

  }
}
