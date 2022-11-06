package com.hamidnagizadeh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

// This is a lexical analyzer for java programming language ,It's my final project for compiler course.
// Hamid Naghizadeh
// 2020/01/15

public class Main {

    private static String[] keywords = new String[] {
      "abstract" ,"assert	" , "boolean","break",
      "byte","case","catch","char",
      "class" ,	"const" ,	"continue" ,	"default",
      "do" ,"double",	"else",	"enum",
      "extends","final","finally","float",
      "for","goto",	"if",	"implements",
      "import",	"instanceof",	"int","interface",
      "long",	"native","new",	"package",
      "private",	"protected"	,"public","return",
      "short","static",	"strictfp",	"super",
      "switch","synchronized"	,"this","throw",
      "throws","transient	" ,"try","void",
      "volatile",	"while","true","false",
      "null"
    };

    private static String[] delimiters = new String[] {
      "{" , "}" , ";" , "," ,"[" , "]" , "(" , ")" , "."
    };
    private static String[] operators = new String[] {
      "=" , "+" , "-" , "*" , "/" , "%"
    };

    private static ArrayList<Character> characters;

    public static void main(String[] args) {

      // i'm adding tokens as a class instance to an ArrayList and when all tokens are processed, write them all in file
        ArrayList<Token> tokens=new ArrayList<>();
        characters = new ArrayList<>();
        String tokenText = "";
        Stack<Integer> blockStack=new Stack();
        blockStack.push(1);


      BufferedReader r= null;
        try {
            r = new BufferedReader(new FileReader("C:\\Users\\..."));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        /* Java Scanner Library doesn't support scanning file, character by character so i used BufferReader Library and i saved
        every character as an item in ArrayList. */

        int ch;
        while(true){
            try {
                if ((ch = r.read()) == -1) break;
                characters.add((char)ch);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int i = 0;
        int state = 0;
        int row = 1, col = 1;
        int block = 1;
        char c = characters.get(0);

        while (i<characters.size()){
                switch (state){
                    case 0 :
                        if (c ==' ') {
                            state = 0;
                            c =nextChar(++i);
                            col++;
                        }
                        else if (c =='\n'){

                            state = 0;
                            c = nextChar(++i);
                            col = 1;
                            row++;
                           break;
                        }
                        else if (c == '\t'){
                            state = 0;
                            c = nextChar(++i);
                            col+=4;
                        }

                        else if (isDelimiter(String.valueOf(c))){
                            if(c=='{'){
                              block +=1;
                                blockStack.push(block);
                            }
                            if (c=='}'){
                                blockStack.pop();

                            }
                            col++;
                            state=0;
                            if (c=='{'){
                              tokens.add(new Token(row,col,blockStack.peek()-1,String.valueOf(c),"Delimiter"));
                            }
                            else {
                              tokens.add(new Token(row,col,blockStack.peek(),String.valueOf(c),"Delimiter"));
                            }
                            c=nextChar(++i);

                        }
                        else if (Character.isLetter(c)) {
                            state=1;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);

                        }
                        else if (Character.isDigit(c)){

                            state=3;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                            break;

                        }
                        else if (c=='<'){
                            state=8;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                          break;
                        }
                        else if(c=='/'){
                          state=24;
                          col++;
                          tokenText +=c;
                          c=nextChar(++i);
                          break;
                        }
                        else if (c=='*'){
                          state=28;
                          col++;
                          tokenText +=c;
                          c=nextChar(++i);
                          break;
                        }
                        else if (isOperator(String.valueOf(c))){

                            state=11;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                            break;
                        }
                        else if (c=='>'){
                            state=12;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                            break;
                        }
                        else if (c=='!'){
                            state=15;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                            break;
                        }
                        else if (c == '&') {
                            state=16;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                            break;

                        }
                        else if (c=='|') {
                            state=17;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                            break;

                        }

                        else {
                          if ((int) c == 13){
                            c=nextChar(++i);
                            break;
                          }
                          col++;
                          state = 0;
                          tokens.add(new Token(row,col,blockStack.peek(), String.valueOf(c),"Unexpected Token "));
                          c=nextChar(++i);
                          break;
                        }


                    case 1 :
                        if (Character.isLetter(c) || Character.isDigit(c) || c=='_') {
                            state = 1;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                            break;
                        }
                        else if (isDelimiter(String.valueOf(c))) {
                            state = 2;
                            break;
                        }
                        else if (isOperator(String.valueOf(c))){
                          state = 2;
                          break;
                        }
                        else {
                            state = 2;
                            c =nextChar(++i);
                        }
                        break;

                    case 2:
                        state=0;
                        if (isKeyword(tokenText)){
                            tokens.add(new Token(row,col,blockStack.peek(), tokenText,"keyword"));
                            tokenText ="";
                        }
                        else {

                          if (tokenText.equals("")){
                            break;
                          }
                          tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Identifier"));
                          tokenText ="";
                        }
                        break;

                    case 3 :

                        if(c=='.'){
                            state=4;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                            break;
                        }
                        else if(Character.isDigit(c)){
                            state=3;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                            break;
                        }
                        else {
                            state=7;
                        }
                        break;

                    case 4:

                        if (Character.isDigit(c)){
                            state=5;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                        }
                        break;

                    case 5:
                        if (Character.isDigit(c)){
                            state =5;
                            col++;
                            tokenText +=c;
                            c =nextChar(++i);
                        }
                        else {
                            state=6;

                        }
                        break;

                    case 6:
                        state=0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Real Number"));
                        tokenText ="";
                        break;

                    case 7:
                        state=0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Integer Number"));
                        tokenText ="";
                        break;

                    case 8:
                      if (c=='=') {
                        state = 9;
                        col++;
                        tokenText += c;
                        c = nextChar(++i);
                      }
                      else {
                        state = 10;
                      }
                        break;

                    case 9:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                        tokenText ="";
                        break;

                    case 10:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                        tokenText ="";
                        break;

                    case 11:
                      if (c=='='){
                        col++;
                        tokenText +=c;
                        c=nextChar(++i);
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                        tokenText ="";
                        state = 0;
                      }
                      else {
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                        tokenText ="";
                        state = 0;
                      }

                        break;

                    case 12:
                        if (c=='='){
                            state=13;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                        }
                        else {
                            state = 14;
                        }
                        break;

                    case 13:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                        tokenText ="";
                        break;

                    case 14:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                        tokenText ="";
                        break;

                    case 15:
                        if (c=='='){
                            state=18;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                        }
                        else {
                            state = 19;
                        }
                        break;

                    case 16:
                        if (c=='&'){
                            state=20;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                        }
                        else {
                            state = 21;
                        }
                        break;

                    case 17:
                        if (c=='|'){
                            state=22;
                            col++;
                            tokenText +=c;
                            c=nextChar(++i);
                        }
                        else {
                            state =23;
                        }
                        break;

                    case  18:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Operator"));
                        tokenText ="";
                        break;

                    case 19:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Logical Operator"));
                        tokenText ="";
                        break;

                    case 20:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Logical Operator"));
                        tokenText ="";
                        break;

                    case 21:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Unexpected Token"));
                        tokenText ="";
                        break;

                    case 22:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Logical Operator"));
                        tokenText ="";
                        break;

                    case 23:
                        state = 0;
                        tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Unexpected Token"));
                        tokenText ="";
                        break;

                    case 24:
                      if (c=='/'){
                        state=25;
                        col++;
                        tokenText +=c;
                        c=nextChar(++i);
                      }
                      else if(c=='*') {
                        state=26;
                        col++;
                        tokenText +=c;
                        c=nextChar(++i);
                      }
                      else if(c=='=') {
                        state=31;
                        col++;
                        tokenText +=c;
                        c=nextChar(++i);
                      }
                      else {
                        state=27;
                      }
                      break;

                  case 25:
                    state = 0;
                    tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Single-Line Comment"));
                    tokenText ="";
                    break;

                  case 26:
                    state = 0;
                    tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Multi-Line Comment Start"));
                    tokenText ="";
                    break;

                  case 27:
                    state = 0;
                    tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                    tokenText ="";
                    break;

                  case 28:
                    if (c=='/'){
                      state=29;
                      col++;
                      tokenText +=c;
                      c=nextChar(++i);
                    }
                    else if(c=='=') {
                      state=32;
                      col++;
                      tokenText +=c;
                      c=nextChar(++i);
                    }
                    else {
                      state=30;
                    }
                    break;

                  case 29:
                    state = 0;
                    tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Multi-Line Comment End"));
                    tokenText ="";
                    break;

                  case 30:
                    state = 0;
                    tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                    tokenText ="";
                    break;

                  case 31:
                    state = 0;
                    tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                    tokenText ="";
                    break;

                  case 32:
                    state = 0;
                    tokens.add(new Token(row,col,blockStack.peek(), tokenText,"Arithmetic Operator"));
                    tokenText ="";
                    break;
                }
            }


        PrintWriter writer = null;
        try {
            writer = new PrintWriter("C:\\Users\\...", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }


      writer.print( "Hello There, list of the tokens:\n" );
      for (Token token : tokens) {
        writer.print(token.row + "  |  " + token.col + "  |  " + token.blockNumber + "  |  " + token.name + "  |  " + token.type + "\n");
      }
      writer.close();

    }


        private static boolean isKeyword(String string){
            return Arrays.asList(keywords).contains(string);
        }

        private static boolean isDelimiter(String c){
            return Arrays.asList(delimiters).contains(c);
        }

        private static boolean isOperator(String c){
            return Arrays.asList(operators).contains(c);
        }

        private static char nextChar(int i){
           if (i>=characters.size()){
             return ' ';
           }
           return characters.get(i);
        }



    }

