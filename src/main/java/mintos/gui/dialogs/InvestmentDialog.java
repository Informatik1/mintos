package mintos.gui.dialogs;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import mintos.util.Utilities;


public class InvestmentDialog extends Dialog<Pair<LocalDate, String>> {

  private Date date;
  private Double investment;


  public InvestmentDialog() {
    this.setTitle("Investment");
    ButtonType addButton = new ButtonType("Add", ButtonData.OK_DONE);
    this.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    DatePicker dateField = new DatePicker();
    dateField.setPromptText("");

    TextField paymentField = new TextField();
    paymentField.textProperty().addListener(new ChangeListener<String>() {

      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if(newValue.matches("|[-\\+]?|[-\\+]?\\d+\\.?|[-\\+]?\\d+\\.?\\d+")) {
          try {
            Double.parseDouble(newValue);
          }
          catch(Exception e) {}
        }
        else {
          paymentField.setText(oldValue);
        }
      }
    });
    grid.add(new Label("Date of investment:"), 0, 0);
    grid.add(dateField, 1, 0);
    grid.add(new Label("Add investment:"), 0, 1);
    grid.add(paymentField, 1, 1);

    Node addButtonNode = this.getDialogPane().lookupButton(addButton);
    addButtonNode.setDisable(true);
    dateField.valueProperty().addListener((ov, oldValue, newValue) -> {
      if(paymentField.getText() != null && !paymentField.getText().trim().isEmpty()) {
        addButtonNode.setDisable(false);
      }
    });

    paymentField.textProperty().addListener((observable, oldValue, newValue) -> {
      if(dateField.getValue() != null) {
        addButtonNode.setDisable(newValue.trim().isEmpty());
      }
    });

    this.getDialogPane().setContent(grid);

    Platform.runLater(() -> dateField.requestFocus());

    this.setResultConverter(dialogButton -> {
      if(dialogButton == addButton) {
        return new Pair<>(dateField.getValue(), paymentField.getText());
      }
      return null;
    });

    Optional<Pair<LocalDate, String>> result = this.showAndWait();

    result.ifPresent(pairSelected -> {
      date = Utilities.asDate(pairSelected.getKey());
      investment = Double.parseDouble(pairSelected.getValue());
    });
  }


  public Double getInvestment() {
    return investment;
  }


  public Date getDate() {
    return date;
  }
}
