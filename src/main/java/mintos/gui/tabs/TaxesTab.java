package mintos.gui.tabs;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mintos.db.impl.mssql.DbaProvider_Mssql;
import mintos.db.interfaces.IDbaProvider;
import mintos.util.Utilities;


public class TaxesTab extends Tab {

  private IDbaProvider dbProvider;
  private static Log log = LogFactory.getLog(TaxesTab.class);


  @Required
  public void setDbProvider(IDbaProvider dbProvider) {
    this.dbProvider = dbProvider;
  }


  public TaxesTab(String title) {
    super(title);
    setClosable(false);
  }


  public void init(ReadOnlyDoubleProperty widthProperty) {

    VBox vbox = new VBox();
    Map<String, List<String>> months = dbProvider.getAllMonths();

    for(String year : months.keySet()) {
      GridPane grid = new GridPane();
      grid.setPadding(new Insets(5, 5, 5, 5));
      grid.setVgap(5);
      grid.setHgap(5);
      grid.add(new Label("Taxes " + year), 0, 0);
      grid.add(new Label("Income"), 0, 1);
      TextField totalIncomeTf = new TextField();
      totalIncomeTf
          .setText("" + dbProvider.getTransactionStatistics("EUR", year, null, Utilities.getTotalIncomeTypes()).getTurnoverSum());

      grid.add(totalIncomeTf, 1, 1);
      
      
      grid.add(new Label("Interest"), 0, 2);
      TextField totalInterestTf = new TextField();
      totalInterestTf
          .setText("" + dbProvider.getTransactionStatistics("EUR", year, null, Utilities.getTotalInterestTypes()).getTurnoverSum());

      grid.add(totalInterestTf, 1, 2);
      
      TextField totalOutcomeTf = new TextField();
         Double totalOutcome = dbProvider.getTransactionStatistics("EUR", year, null, Utilities.getTotalExpensesTypes()).getTurnoverSum();
      Double fees = dbProvider.getTransactionStatistics("EUR", year, null, Utilities.getTotalFeesTypes()).getTurnoverSum();
     log.debug(fees);
      totalOutcomeTf
          .setText("" + (totalOutcome - fees));
      
      
      grid.add(new Label("Outcome"), 0, 3);
      grid.add(totalOutcomeTf, 1, 3);

      vbox.getChildren().add(grid);
    }
    setContent(vbox);
  }
}
