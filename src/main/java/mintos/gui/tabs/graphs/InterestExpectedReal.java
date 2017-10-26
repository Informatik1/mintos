package mintos.gui.tabs.graphs;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import mintos.beans.Statistics;
import mintos.util.Utilities;


public class InterestExpectedReal {

  final CategoryAxis xAxis = new CategoryAxis();
  final NumberAxis yAxis = new NumberAxis();
  final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

  XYChart.Series<String, Number> expectedIncome = null;
  XYChart.Series<String, Number> realIconme = null;


  public InterestExpectedReal() {

    lineChart.getStylesheets().add("chart.css");

    xAxis.setLabel("Month");
    lineChart.setTitle("Income");
    expectedIncome = new XYChart.Series<String, Number>();
    realIconme = new XYChart.Series<String, Number>();
  }


  public void reloadData(String currencyId, ObservableList<Statistics> data) {
    yAxis.setLabel(currencyId);
    lineChart.getData().clear();

    expectedIncome = new XYChart.Series<String, Number>();
    realIconme = new XYChart.Series<String, Number>();

    expectedIncome.setName("Expected income [" + currencyId+"]");
    realIconme.setName("Real income [" + currencyId+"]");

    Calendar actualDate = new GregorianCalendar();

    for(Statistics entry : data) {
      String[] s = entry.getDate().split(" ");
      int month = Utilities.getMonth(s[0]);
      int year = Integer.parseInt(s[1]);
      if(year < actualDate.get(Calendar.YEAR) || (year == actualDate.get(Calendar.YEAR) && (month - 1) <= actualDate.get(Calendar.MONTH))) {

        expectedIncome.getData().add(new XYChart.Data<String, Number>(entry.getDate(), entry.getExpectedMoney()));
        realIconme.getData().add(new XYChart.Data<String, Number>(entry.getDate(), entry.getRealIncome()));
      }
    }
    //
    lineChart.getData().addAll(expectedIncome, realIconme);

    for(XYChart.Series<String, Number> s : lineChart.getData()) {
      for(XYChart.Data<String, Number> d : s.getData()) {
        Tooltip tp = new Tooltip();
        tp.setText(d.getXValue().toString() + " : " + d.getYValue());
        Tooltip.install(d.getNode(), tp);
        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
      }
    }

  }


  public LineChart<String, Number> getLineChart(ObservableList<Statistics> data) {
    return lineChart;
  }
}
