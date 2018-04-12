package mintos.gui.tabs;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import mintos.beans.Statistics;
import mintos.beans.TransactionStatistics;
import mintos.db.interfaces.IDbaProvider;
import mintos.gui.dialogs.InvestmentDialog;
import mintos.gui.tabs.graphs.InterestExpectedReal;
import mintos.util.Utilities;


public class TotalStatisticsTab extends Tab {

  private final TableView<Statistics> table = new TableView<Statistics>();
  private InterestExpectedReal interestExpectedReal;

  private IDbaProvider dbProvider;


  @Required
  public void setDbProvider(IDbaProvider dbProvider) {
    this.dbProvider = dbProvider;
  }


  @Required
  public void setInterestExpectedReal(InterestExpectedReal interestExpectedReal) {
    this.interestExpectedReal = interestExpectedReal;
  }

  ObservableList<Statistics> data = FXCollections.observableArrayList();

  Label lblEur = new Label("Exchange rate EUR");
  Label lblPln = new Label("Exchange rate PLN");
  Label lblGel = new Label("Exchange rate GEL");
  Label lblMonths = new Label("Months");

  TextField rateEur = new TextField("26,06");
  TextField ratePln = new TextField("6,14");
  TextField rateGel = new TextField("9,02");
  TextField months = new TextField("120");
  Button computeTrend = new Button("Compute trend");
  Button investment = new Button("Add investment");


  public TotalStatisticsTab(String title) {
    super(title);
    setClosable(false);
  }


  public void init(ReadOnlyDoubleProperty width) {
    init();

    GridPane grid = new GridPane();
    grid.setVgap(5);
    grid.setHgap(6);
    // grid2.setSpacing(5);
    grid.setPadding(new Insets(5, 5, 5, 5));
    grid.add(investment, 0, 0);
    grid.add(lblEur, 3, 0);
    grid.add(rateEur, 4, 0);
    grid.add(lblPln, 3, 1);
    grid.add(ratePln, 4, 1);
    grid.add(lblGel, 3, 2);
    grid.add(rateGel, 4, 2);
    grid.add(lblMonths, 0, 2);
    grid.add(months, 1, 2);
    grid.add(computeTrend, 2, 2);
    grid.add(table, 0, 4, 5, 1);
    grid.add(interestExpectedReal.getLineChart(data), 0, 5, 5, 1);

    table.setPadding(new Insets(10, 10, 10, 10));
    table.setPrefHeight(650);
    table.prefWidthProperty().bind(width);
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    this.setContent(grid);

    computeTrend.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        reloadTrend(3, 2017, Utilities.getTotalInterestTypes(), rateEur.getText(), ratePln.getText(), rateGel.getText(), months.getText());

        interestExpectedReal.reloadData("CZK", data);
      }
    });
    investment.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        InvestmentDialog dialog = new InvestmentDialog();
        dbProvider.addInvestment(dialog.getDate(), dialog.getInvestment());
      }
    });
  }


  private void init() {
    TableColumn<Statistics, String> date = new TableColumn<Statistics, String>("Date");
    date.setCellValueFactory(new PropertyValueFactory<Statistics, String>("date"));

    // ********************

    TableColumn<Statistics, Double> invested = new TableColumn<Statistics, Double>("Invested");
    invested.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("invested"));
    invested.setCellFactory(getCellFactory());
    invested.setEditable(false);

    // ********************

    TableColumn<Statistics, Double> incommingPayment = new TableColumn<Statistics, Double>("Incomming payment");
    incommingPayment.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("incommingPayment"));
    incommingPayment.setCellFactory(getCellFactory());
    incommingPayment.setOnEditCommit(new EventHandler<CellEditEvent<Statistics, Double>>() {

      @Override
      public void handle(CellEditEvent<Statistics, Double> t) {
        t.getTableView().getItems().get(t.getTablePosition().getRow()).setIncommingPayment(t.getNewValue());
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(0).setVisible(true);
      }
    });

    // ********************
    TableColumn<Statistics, Double> expectedRate = new TableColumn<Statistics, Double>("Expected rate");
    expectedRate.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("expectedRate"));
    expectedRate.setCellFactory(getCellFactory());
    expectedRate.setOnEditCommit(new EventHandler<CellEditEvent<Statistics, Double>>() {

      @Override
      public void handle(CellEditEvent<Statistics, Double> t) {
        t.getTableView().getItems().get(t.getTablePosition().getRow()).setExpectedRate(t.getNewValue());
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(0).setVisible(true);
      }
    });
    // ********************
    TableColumn<Statistics, Double> expectedInvestment = new TableColumn<Statistics, Double>("Expected investment");
    expectedInvestment.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("expectedInvestment"));
    expectedInvestment.setCellFactory(getCellFactory());
    expectedInvestment.setEditable(false);

    // ********************
    TableColumn<Statistics, Double> realMoney = new TableColumn<Statistics, Double>("Real money");
    realMoney.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("realMoney"));
    realMoney.setCellFactory(getCellFactory());
    realMoney.setEditable(false);
    // ***************

    TableColumn<Statistics, Double> expectedMoney = new TableColumn<Statistics, Double>("Expected money");

    expectedMoney.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("expectedMoney"));
    expectedMoney.setCellFactory(getCellFactory());
    expectedMoney.setEditable(false);
    // ***************

    TableColumn<Statistics, Double> realIncome = new TableColumn<Statistics, Double>("Real income");

    realIncome.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("realIncome"));
    realIncome.setCellFactory(getCellFactory());
    realIncome.setEditable(false);

    // ***************

    TableColumn<Statistics, Double> profit = new TableColumn<Statistics, Double>("Profit");

    profit.setCellValueFactory(new PropertyValueFactory<Statistics, Double>("profit"));
    profit.setCellFactory(getCellFactory());
    profit.setEditable(false);

    table.getColumns().addAll(date, invested, incommingPayment, expectedRate, expectedInvestment, realMoney, expectedMoney, realIncome, profit);

    table.setEditable(true);
    table.setItems(data);
  }


  private Callback<TableColumn<Statistics, Double>, TableCell<Statistics, Double>> getCellFactory() {
    return TextFieldTableCell.forTableColumn(new StringConverter<Double>() {

      private double original;


      @Override
      public String toString(Double object) {
        DecimalFormat twoPlaces = new DecimalFormat("###,###,##0.00");
        return twoPlaces.format(object);
      }


      @Override
      public Double fromString(String string) {
        double d = 0;
        try {
          d = Double.parseDouble(string);
          original = d;
          return d;
        }
        catch(Exception e) {
          Alert alert = new Alert(AlertType.ERROR);
          alert.showAndWait();
          d = original;
        }
        return d;
      }
    });

  }


  public void reloadTrend(int month, int year, Set<String> cumulative, String rateEur, String ratePln, String rateGel, String months) {
    data.clear();
    Statistics previous = null;
    int countOfMonths = Integer.parseInt(months);

    for(int i = 0; i < countOfMonths; i++) {
      double inc = 0;
      inc = dbProvider.getInvestment(year, month);
//if(inc == 0){
//  inc = 20000;
//}
      NumberBinding previousInvested = Bindings.add(new SimpleDoubleProperty(0), 0);
      double previousRealMoney = 0;

      if(previous != null) {
        previousInvested = previous.getExpectedInvestmentBinding();
        previousRealMoney = previous.getRealMoney();
      }
      Statistics st = new Statistics(previousInvested, inc, 12., 0., 0., 0., 0., 0., month, year);

      NumberBinding expectedInvestment = Bindings.add(
          Bindings.multiply(st.getInvestedBinding(), Bindings.divide(Bindings.divide(st.getExpectedRateProperty(), 100.), 12.)),
          st.getIncommingPaymentProperty());
      if(previous != null) {
        expectedInvestment = Bindings.add(previous.getExpectedInvestmentBinding(), expectedInvestment);
      }
      st.setExpectedInvestmentBinding(expectedInvestment);

      NumberBinding expectedMoney = Bindings
          .subtract(Bindings.subtract(st.getExpectedInvestmentBinding(), st.getInvestedBinding()), st.getIncommingPaymentProperty());
      st.setExpectedMoneyBinding(expectedMoney);

      Calendar d = new GregorianCalendar();
      if(year < d.get(Calendar.YEAR) || (year == d.get(Calendar.YEAR) && (month - 1) <= d.get(Calendar.MONTH))) {

        Set<String> income = new HashSet<String>(cumulative);
        income.add("Incoming currency exchange transaction");
        income.add("Refer a friend bonus");
        income.add("Outgoing currency exchange transaction");
        income.add("Affiliate bonus");
        income.add("Incoming client payment");
        income.add("Withdraw application");

        TransactionStatistics stat2 = dbProvider.getTransactionStatistics("EUR", year + "", Utilities.getMonthForInt(month - 1), income);
        TransactionStatistics stat3 = dbProvider.getTransactionStatistics("PLN", year + "", Utilities.getMonthForInt(month - 1), income);
        TransactionStatistics stat4 = dbProvider.getTransactionStatistics("GEL", year + "", Utilities.getMonthForInt(month - 1), income);
        TransactionStatistics stat5 = dbProvider.getTransactionStatistics("CZK", year + "", Utilities.getMonthForInt(month - 1), income);

        st.setRealMoney(
            previousRealMoney + stat2.getTurnoverSum() * Double.parseDouble(rateEur.replace(",", ".")) +
                stat3.getTurnoverSum() * Double.parseDouble(ratePln.replace(",", ".")) +
                stat4.getTurnoverSum() * Double.parseDouble(rateGel.replace(",", ".")) + stat5.getTurnoverSum());

        double realIncome = st.getRealMoney() - st.getIncommingPayment();
        if(previous != null) {
          realIncome = realIncome - previous.getRealMoney();
        }
        st.setRealIncome(realIncome);

        NumberBinding invested = Bindings.add(st.getIncommingPaymentProperty(), 0);
        if(previous != null) {
          invested = Bindings.add(invested, previous.getInvestedHiden());
        }
        st.setInvestedHiden(invested);

        st.setProfitBinding(Bindings.subtract(st.getRealMoney(), st.getInvestedHiden()));

      }
      month++;
      if((month) % 13 == 0) {
        month = month % 13 + 1;
        year++;
      }

      data.add(st);
      previous = st;

    }

  }

}
