package mintos.gui;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import mintos.db.interfaces.IDbaProvider;


public class LineChartMy {

  private IDbaProvider dbProvider;


  @Required
  public void setDbProvider(IDbaProvider dbProvider) {
    this.dbProvider = dbProvider;
  }

  final CategoryAxis xAxis = new CategoryAxis();
  final NumberAxis yAxis = new NumberAxis();
  final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
  XYChart.Series<String, Number> interest = null;
  XYChart.Series<String, Number> cumulativeInterest = null;


  public LineChartMy() {

    lineChart.getStylesheets().add("chart.css");

    xAxis.setLabel("Month");
    lineChart.setTitle("Interest, 2017");
    interest = new XYChart.Series<String, Number>();
    cumulativeInterest = new XYChart.Series<String, Number>();
  }


  public void reloadData(String currencyId, Set<String> detailTypes) {
    yAxis.setLabel(currencyId);
    lineChart.getData().clear();

    interest = new XYChart.Series<String, Number>();
    cumulativeInterest = new XYChart.Series<String, Number>();

    double cumulativeInterestValue = 0;

    interest.setName("Interest " + currencyId);
    cumulativeInterest.setName("Cumulative Interest " + currencyId);
    Map<String, Double> interestData = dbProvider.getMontStatisticsOrderTypes(currencyId, detailTypes);
    for(Entry<String, Double> entry : interestData.entrySet()) {
      interest.getData().add(new XYChart.Data<String, Number>(entry.getKey(), entry.getValue()));
      cumulativeInterestValue += entry.getValue();
      cumulativeInterest.getData().add(new XYChart.Data<String, Number>(entry.getKey(), cumulativeInterestValue));
    }

    lineChart.getData().addAll(interest, cumulativeInterest);

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


  public LineChart<String, Number> getLineChart() {
    return lineChart;
  }
}
