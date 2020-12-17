package com.webserver.database;

class Pair<U, V> {

  private U firstElement;
  private V secondElement;

  public Pair(U first, V second) {

    this.firstElement = first;
    this.secondElement = second;
  }

  public U getFirstElement() {
    return firstElement;
  }

  public V getSecondElement() {
    return secondElement;
  }

}
