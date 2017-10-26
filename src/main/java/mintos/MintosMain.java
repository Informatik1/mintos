package mintos;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.application.Application;


public class MintosMain {

  private static Log log = LogFactory.getLog(MintosMain.class);


  public static void main(String[] args) throws IOException {
    Application.launch(MintosStarter.class);
  }

}
