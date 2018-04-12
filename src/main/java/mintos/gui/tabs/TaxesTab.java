package mintos.gui.tabs;

import org.springframework.beans.factory.annotation.Required;

import javafx.scene.control.Tab;
import mintos.db.interfaces.IDbaProvider;


public class TaxesTab extends Tab {

  private IDbaProvider dbProvider;


  @Required
  public void setDbProvider(IDbaProvider dbProvider) {
    this.dbProvider = dbProvider;
  }


  public TaxesTab(String title) {
    super(title);
    setClosable(false);
  }
}
