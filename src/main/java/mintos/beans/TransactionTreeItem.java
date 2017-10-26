package mintos.beans;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;


public class TransactionTreeItem<T> extends TreeItem<T> {

  private boolean leaf = false;
  private String year;
  private String month;
  private ERootType rootType;
  private boolean nodeLoaded = false;


  public TransactionTreeItem(T transaction, Node object) {
    super(transaction, object);
  }


  public boolean isNodeLoaded() {
    return nodeLoaded;
  }


  public void setNodeLoaded(boolean nodeLoaded) {
    this.nodeLoaded = nodeLoaded;
  }


  public TransactionTreeItem(T e) {
    super(e);
  }


  public void setLeaf(boolean leaf) {
    this.leaf = leaf;
  }


  public ERootType getRootType() {
    return rootType;
  }


  public void setRootType(ERootType rootType) {
    this.rootType = rootType;
  }


  public String getYear() {
    return year;
  }


  public void setYear(String year) {
    this.year = year;
  }


  public String getMonth() {
    return month;
  }


  public void setMonth(String month) {
    this.month = month;
  }


  @Override
  public boolean isLeaf() {
    return leaf;
  }
}
