package mintos.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import mintos.beans.Transaction;


public class TransactionXslxParser implements InitializingBean {

  private String dateFormat;
  private SimpleDateFormat sdf;

  @Required
  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }


  @Override
  public void afterPropertiesSet() throws Exception {
    sdf = new SimpleDateFormat(dateFormat);
  }


  public List<Transaction> readXslx(String inputName) throws IOException {
   File input = new File(inputName);
    if(!input.exists()) {
      throw new FileNotFoundException("File does not exists");
    }
    FileInputStream fis = null;
    XSSFWorkbook myWorkBook = null;
    List<Transaction> list = null;
    try {
      fis = new FileInputStream(input);
      myWorkBook = new XSSFWorkbook(fis);
      list = parseTransacionSheet(myWorkBook.getSheetAt(0));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
      if(fis != null) {
        fis.close();
      }
      if(myWorkBook != null) {
        myWorkBook.close();
      }
    }
    return list;
  }


  private List<Transaction> parseTransacionSheet(XSSFSheet hssfSheet) throws ParseException {
    Iterator<Row> rowIterator = hssfSheet.iterator();
    List<Transaction> transactionList = new ArrayList<Transaction>();
    Row row = rowIterator.next();

    while(rowIterator.hasNext()) {
      Transaction transaction = new Transaction();

      row = rowIterator.next();

      for(int i = 0; i < 6; i++) {
        Cell cell = row.getCell(i);
        if(cell == null)
          continue;
        switch(i) {
          case 0 : {
            String value = cell.getStringCellValue();
            transaction.setTransactionId(Long.parseLong(value));
            break;
          }
          case 1 : {
            String value = cell.getStringCellValue();
            Date date = new Date(sdf.parse(value).getTime());
            transaction.setDate(new Timestamp(date.getTime()));
            break;
          }

          case 2 : {
            String value = cell.getStringCellValue();
            int indexStart = value.indexOf("Loan ID");
            if(indexStart != -1) {
              String detailTypeParsed = value.substring(0, indexStart);
              if(detailTypeParsed.trim().startsWith("Discount/premium for secondary market transaction")) {
                transaction.setDetailsType("Discount/premium for secondary market transaction");
              }
              else if(detailTypeParsed.trim().startsWith("Delayed interest income on rebuy")) {
                transaction.setDetailsType("Delayed interest income on rebuy");
              }
              else if(detailTypeParsed.trim().startsWith("Investment principal rebuy Rebuy")) {
                transaction.setDetailsType("Investment principal rebuy Rebuy");
              }
              else if(detailTypeParsed.trim().startsWith("Interest income on rebuy")) {
                transaction.setDetailsType("Interest income on rebuy");
              }
              else {
                transaction.setDetailsType(detailTypeParsed.trim());
              }
              String loandIdParsed = value.substring(indexStart, value.length());
              transaction.setLoanId(loandIdParsed.trim().replace("Loan ID: ", "").trim());
            }
            transaction.setDetails(value);

            if(value.startsWith("FX commission")) {
              transaction.setDetailsType("FX commission");
            }
            else if(value.equalsIgnoreCase("Incoming client payment")) {
              transaction.setDetailsType(value);
            }
            else if(value.startsWith("Outgoing currency exchange transaction")) {
              transaction.setDetailsType("Outgoing currency exchange transaction");
            }
            else if(value.startsWith("Incoming currency exchange transaction")) {
              transaction.setDetailsType("Incoming currency exchange transaction");
            }
            else if(value.startsWith("Affiliate bonus")) {
              transaction.setDetailsType("Affiliate bonus");
            }
            else if(value.startsWith("Refer a friend bonus")) {
              transaction.setDetailsType("Refer a friend bonus");
            }

            if(value.contains("Exchange Rate")) {
              String[] rate = value.split("Exchange Rate: ");
              transaction.setExchangeRate(Double.valueOf(rate[1]));
            }
            break;
          }

          case 3 : {
            Double value = cell.getNumericCellValue();
            transaction.setTurnover(value);
            break;
          }

          case 4 : {
            Double value = cell.getNumericCellValue();
            transaction.setBalance(value);
            break;
          }
          case 5 : {
            String value = cell.getStringCellValue();
            transaction.setCurrency(value);
            break;
          }
        }
      }
      if(transaction.getTransactionId() != null)
        transactionList.add(transaction);
    }

    return transactionList;
  }

}
