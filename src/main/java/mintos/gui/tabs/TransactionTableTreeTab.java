package mintos.gui.tabs;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import mintos.beans.ERootType;
import mintos.beans.Transaction;
import mintos.beans.TransactionStatistics;
import mintos.beans.TransactionTreeItem;
import mintos.db.interfaces.IDbaProvider;
import mintos.gui.LineChartMy;
import mintos.parser.TransactionXslxParser;
import mintos.util.Utilities;


public class TransactionTableTreeTab extends Tab {

  private static Log log = LogFactory.getLog(TransactionTableTreeTab.class);

  private String inputFolder;


  @Required
  public void setInputFolder(String inputFolder) {
    this.inputFolder = inputFolder;
  }

  Transaction rootTransaction = new Transaction("Root");
  private IDbaProvider dbProvider;
  private LineChartMy lineChartMy;

  private String defaultCurrency;
  private TransactionXslxParser parser;


  @Required
  public void setDefaultCurrency(String defaultCurrency) {
    this.defaultCurrency = defaultCurrency;
  }


  @Required
  public void setDbProvider(IDbaProvider dbProvider) {
    this.dbProvider = dbProvider;
  }


  @Required
  public void setLineChartMy(LineChartMy lineChartMy) {
    this.lineChartMy = lineChartMy;
  }


  @Required
  public void setParser(TransactionXslxParser parser) {
    this.parser = parser;
  }

  private TreeTableView<Transaction> treeTableView;

  ComboBox<String> currencyComboBox = new ComboBox<String>();
  Button loadBtn = new Button("Reload");
  Label totalInterestLbl = new Label("Total interest");
  TextField totalInterestTf = new TextField();

  Label totalOutcomeLbl = new Label("Total outcome");
  TextField totalOutcomeTf = new TextField();

  final TransactionTreeItem<Transaction> root = new TransactionTreeItem<Transaction>(rootTransaction, null);


  public TransactionTableTreeTab(String title) {
    super(title);
    setClosable(false);
  }


  public void init(ReadOnlyDoubleProperty width) {
    init();

    GridPane grid = new GridPane();
    grid.add(currencyComboBox, 0, 0);
    grid.add(loadBtn, 2, 0);

    grid.add(treeTableView, 0, 1, 4, 1);
    grid.add(totalInterestLbl, 0, 5);
    grid.add(totalInterestTf, 1, 5);

    grid.add(totalOutcomeLbl, 0, 6);
    grid.add(totalOutcomeTf, 1, 6);

    lineChartMy.reloadData(defaultCurrency, Utilities.getTotalInterestTypes());
    grid.add(lineChartMy.getLineChart(), 0, 7, 4, 1);
    grid.setVgap(4);
    grid.setHgap(10);
    grid.setPadding(new Insets(5, 5, 5, 5));

    treeTableView.prefWidthProperty().bind(width);

    totalInterestTf.setEditable(false);
    totalInterestTf
        .setText("" + dbProvider.getTransactionStatistics(defaultCurrency, null, null, Utilities.getTotalInterestTypes()).getTurnoverSum());

    totalOutcomeTf.setEditable(false);
    totalOutcomeTf
        .setText("" + dbProvider.getTransactionStatistics(defaultCurrency, null, null, Utilities.getTotalOutcomeTypes()).getTurnoverSum());

    currencyComboBox.setValue(defaultCurrency);

    List<String> currencies = new ArrayList<String>();
    currencies.add(defaultCurrency);
    List<String> loadedCurrencies = dbProvider.getAllCurrencies();
    if(!CollectionUtils.isEmpty(loadedCurrencies)) {
      currencies = loadedCurrencies;
    }
    ObservableList<String> options = FXCollections.observableArrayList(currencies);

    currencyComboBox.setItems(options);

    currencyComboBox.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        if(currencyComboBox.getValue() != null) {
          refreshTableTree();
          totalInterestTf.setText(
              "" + dbProvider.getTransactionStatistics(currencyComboBox.getValue(), null, null, Utilities.getTotalInterestTypes())
                  .getTurnoverSum());

          totalOutcomeTf.setText(
              "" + dbProvider.getTransactionStatistics(currencyComboBox.getValue(), null, null, Utilities.getTotalOutcomeTypes()).getTurnoverSum());

          lineChartMy.reloadData(currencyComboBox.getValue(), Utilities.getTotalInterestTypes());
        }
      }
    });

    loadBtn.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        try {

//          dbProvider.removeAllTransactions();
          removeTreeItems();

          parseAndSaveTransactions();

          List<String> currencies = new ArrayList<String>();
          currencies.add(defaultCurrency);
          List<String> loadedCurrencies = dbProvider.getAllCurrencies();
          if(!CollectionUtils.isEmpty(loadedCurrencies)) {
            currencies = loadedCurrencies;
          }
          ObservableList<String> options = FXCollections.observableArrayList(currencies);
          currencyComboBox.setItems(options);
          removeTreeItems();

        }
        catch(IOException e) {
          log.error("Error: " + e.getMessage(), e);
        }
      }
    });

    this.setContent(grid);
  }


  private void init() {

    TreeTableColumn<Transaction, String> empColumn = new TreeTableColumn<Transaction, String>("Detail Type");

    empColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Transaction, String>, ObservableValue<String>>() {

      @Override
      public ObservableValue<String> call(CellDataFeatures<Transaction, String> param) {
        return new SimpleObjectProperty<>(param.getValue().getValue().getDetailsType());
      }
    });

    TreeTableColumn<Transaction, Timestamp> dateColumn = new TreeTableColumn<Transaction, Timestamp>("Date");
    dateColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Transaction, Timestamp>, ObservableValue<Timestamp>>() {

      @Override
      public ObservableValue<Timestamp> call(CellDataFeatures<Transaction, Timestamp> param) {
        return new SimpleObjectProperty<>(param.getValue().getValue().getDate());
      }

    });

    TreeTableColumn<Transaction, Double> turnoverColumn = new TreeTableColumn<Transaction, Double>("Turnover");
    turnoverColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Transaction, Double>, ObservableValue<Double>>() {

      @Override
      public ObservableValue<Double> call(CellDataFeatures<Transaction, Double> param) {
        return new SimpleObjectProperty<>(param.getValue().getValue().getTurnover());
      }
    });
    root.setRootType(ERootType.ROOT);
    treeTableView = new TreeTableView<Transaction>(root);

    treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

    treeTableView.setMaxHeight(250);
    treeTableView.getColumns().setAll(empColumn, dateColumn, turnoverColumn);

    treeTableView.getStylesheets().add("border-table.css");

    EventHandler<TreeModificationEvent<Transaction>> branchExpandedEventHandler = new EventHandler<TreeModificationEvent<Transaction>>() {

      public void handle(TreeModificationEvent<Transaction> event) {
        TransactionTreeItem<Transaction> item = (TransactionTreeItem<Transaction>)event.getTreeItem();
        if(!item.isNodeLoaded()) {
          populateTreeItem(item, currencyComboBox.getValue());
        }
      }
    };

    EventType<TreeModificationEvent<Transaction>> eventType = TreeItem.branchExpandedEvent();
    root.addEventHandler(eventType, branchExpandedEventHandler);

  }


  private void populateTreeItem(TransactionTreeItem<Transaction> item, String currency) {
    item.setNodeLoaded(true);
    try {
      if(item.getRootType() == ERootType.ROOT) {
        Map<String, List<String>> map = dbProvider.getAllMonths();
        for(Entry<String, List<String>> entry : map.entrySet()) {
          Transaction year = new Transaction(entry.getKey());
          TransactionTreeItem<Transaction> tr = new TransactionTreeItem<>(year);
          tr.setRootType(ERootType.YEAR);
          item.getChildren().add(tr);

          for(String monthEntry : entry.getValue()) {
            Transaction month = new Transaction(monthEntry);
            TransactionTreeItem<Transaction> trMonth = new TransactionTreeItem<>(month);
            trMonth.setRootType(ERootType.MONTH);
            trMonth.setYear(entry.getKey());
            trMonth.setMonth(monthEntry);
            tr.getChildren().add(trMonth);
          }
        }
      }
      else if(item.getRootType() == ERootType.MONTH) {
        Transaction totalInterest = new Transaction("Total Interest");
        TransactionTreeItem<Transaction> totalInterestItem = new TransactionTreeItem<Transaction>(totalInterest);
        item.getChildren().add(totalInterestItem);

        for(String category : dbProvider.getAllCategories()) {
          Transaction categoryTransaction = new Transaction(category);
          TransactionTreeItem<Transaction> tr = new TransactionTreeItem<>(categoryTransaction);
          tr.setYear(item.getYear());
          tr.setMonth(item.getMonth());
          TransactionStatistics ts = dbProvider.getTransactionStatistics(currency, item.getYear(), item.getMonth(), Collections.singleton(category));
          if(ts.getTransactionsCount() == 0) {
            tr.setLeaf(true);
          }
          tr.getValue().setBalance(ts.getBalanceSum());
          tr.getValue().setTurnover(ts.getTurnoverSum());

          if(Utilities.getTotalInterestTypes().contains(category)) {
            totalInterest.setBalance((totalInterest.getBalance() != null ? totalInterest.getBalance() : 0) + ts.getBalanceSum());
            totalInterest.setTurnover((totalInterest.getTurnover() != null ? totalInterest.getTurnover() : 0) + ts.getTurnoverSum());
            tr.setRootType(ERootType.DETAIL_TYPE);
            totalInterestItem.getChildren().add(tr);
          }
          else {
            tr.setRootType(ERootType.DETAIL_TYPE);
            item.getChildren().add(tr);
          }
        }

      }
      else if(item.getRootType() == ERootType.DETAIL_TYPE) {
        for(Transaction e : dbProvider.getAllTransactions(currency, item.getYear(), item.getMonth(), item.getValue().getDetailsType())) {
          TransactionTreeItem<Transaction> tr = new TransactionTreeItem<>(e);
          tr.setLeaf(true);
          item.getChildren().add(tr);
        }
      }
    }
    catch(Exception e) {
      log.error("Error: " + e, e);
    }
  }


  public void refreshTableTree() {
    log.debug("Refreshing the transaction table tree. ");
    removeTreeItems();
  }


  public void removeTreeItems() {
    root.setExpanded(false);
    root.setNodeLoaded(false);
    root.getChildren().retainAll(Collections.emptyList());
  }


  public void parseAndSaveTransactions() throws IOException {
    for(String f : getValidFileNamesForParser()) {
      log.debug("Reading file with fileName=" + f);
      List<Transaction> list = parser.readXslx(f);
      log.debug("File contains " + list.size() + " transactions");
      dbProvider.insertTransactions(list);
    }
  }


  public List<String> getValidFileNamesForParser() {
    File root = new File(inputFolder);
    List<String> fileListName = new ArrayList<>();

    Collection<File> fileList = FileUtils
        .listFiles(root, FileFilterUtils.suffixFileFilter("account-statement.xlsx"), FileFilterUtils.directoryFileFilter());

    for(File f : fileList) {
      if(f.isFile()) {
        fileListName.add(f.getAbsolutePath());
      }
    }
    return fileListName;
  }

}
