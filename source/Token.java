package com.hamidnagizadeh;

public class Token {
  public int row;
  public int col;
  public int blockNumber;
  public String name;
  public String type;

  Token(int row, int col, int blockNumber, String name, String type) {
    this.row = row;
    this.col = col;
    this.blockNumber = blockNumber;
    this.name = name;
    this.type = type;
  }
}

