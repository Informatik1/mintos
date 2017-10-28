package mintos;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mintos.gui.tabs.StatisticsTab;
import mintos.gui.tabs.TotalStatisticsTab;
import mintos.gui.tabs.TransactionTableTreeTab;


public class MintosStarter extends Application implements InitializingBean {

  final JFXPanel fxPanel = new JFXPanel();

  private TransactionTableTreeTab transactionTableTreeTab;
  private TotalStatisticsTab totalStatisticsTab;
  private StatisticsTab statisticsTab;


  @Required
  public void setTransactionTableTreeTab(TransactionTableTreeTab transactionTableTreeTab) {
    this.transactionTableTreeTab = transactionTableTreeTab;
  }


  @Required
  public void setTotalStatisticsTab(TotalStatisticsTab totalStatisticsTab) {
    this.totalStatisticsTab = totalStatisticsTab;
  }


  @Required
  public void setStatisticsTab(StatisticsTab statisticsTab) {
    this.statisticsTab = statisticsTab;
  }


  @Override
  public void afterPropertiesSet() throws Exception {

  }


  @Override
  public void start(Stage stage) {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    transactionTableTreeTab = context.getBean(TransactionTableTreeTab.class);
    totalStatisticsTab = context.getBean(TotalStatisticsTab.class);
    statisticsTab = context.getBean(StatisticsTab.class);

    StackPane root = new StackPane();
    root.autosize();
    final Scene scene = new Scene(root);

    TabPane tp = new TabPane();

    transactionTableTreeTab.init(scene.widthProperty());
    statisticsTab.init(scene.widthProperty());
    totalStatisticsTab.init(scene.widthProperty());

    tp.getTabs().add(transactionTableTreeTab);
    tp.getTabs().add(statisticsTab);
    tp.getTabs().add(totalStatisticsTab);

    root.getChildren().add(tp);

    stage.setTitle("Mintos");
    stage.setWidth(900);
    stage.setHeight(800);
    stage.setScene(scene);

    stage.show();
    context.close();
  }
}
