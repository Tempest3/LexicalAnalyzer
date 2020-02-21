// LexicalAnalyzer.java
// CSCI 530: Project 1
// By Konrad Wiley

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LexicalAnalyzer{
    private ArrayList<Lexeme> tokenList;

    public LexicalAnalyzer(){
        tokenList = new ArrayList<Lexeme>();
    }

    public static void main(String[] args){
        LexicalAnalyzer la = new LexicalAnalyzer();

        ArrayList<FileCharacter> charList = la.getInput();
        la.getAllTokens(charList, false); //<true> enables print
        la.printAllTokens();
    }

    // prompts user for input file and calls readFile()
    // returns all characters from input file
    private ArrayList<FileCharacter> getInput(){
        Scanner kb = new Scanner(System.in);        
        ArrayList<FileCharacter> charList = new ArrayList<FileCharacter>();
        boolean success = false;
        while(!success){
            System.out.println("Enter filename: ");
            String inputString = kb.nextLine();
            try{
                charList = readFile(inputString);
                success = true;
            }
            catch(IOException e){
                System.out.println("Unable to open file " + inputString);
                success = false;
            }
        }
        kb.close();
        return charList;
    }

    // reads all characters from a file and returns
    // an ArrayList<FileCharacter> containing all characters
    // and line numbers/positions
    private ArrayList<FileCharacter> readFile(String inFile) throws IOException{
        ArrayList<FileCharacter> charList = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
 		
        int c=0;
        int lineNum = 1;
        int linePos = 0;
		while ((c=reader.read()) != -1) {            
            charList.add(new FileCharacter((char)c,lineNum, linePos));
            if(c=='\n'){
                lineNum++;
                linePos=0;
            }
            else
                ++linePos;
		} 
		reader.close(); 
		return charList;
    }

    // loops over <charList> calls processToken()
    // on each token
    private void getAllTokens(ArrayList<FileCharacter> charList, boolean print){
        while(charList.size() > 0){
            //process tokens
            this.processToken(charList, print);
        }
    }

    // reads a single token from <charList>
    // and adds it to the class token list
    // prints all tokens/lexemes with line
    // numbers/positions if <print> is true
    private void processToken(ArrayList<FileCharacter> charList, boolean print){
        FileCharacter fc = charList.remove(0);
        Character c = fc.getChar();
        String value = "";
        Lexeme lex;
        String outputString = "Line " + fc.getLine() + ":" + fc.getPos() + " ";
        //ident or keyword
        if(Character.isLetter(c)){                
            while((Character.isLetter(c) || Character.isDigit(c)) && charList.size() > 0){
                value += c.toString();
                c = charList.get(0).getChar();
                if(Character.isLetter(c) || Character.isDigit(c))
                    charList.remove(0);
            }
            switch(value){
                case "int":                    
                case "double":
                case "String":
                    lex = new Lexeme(TokenType.KEYWORD, value);
                    outputString += "keyword: " + value;
                    break;
                default:
                    lex = new Lexeme(TokenType.IDENT, value);
                    outputString += "identifier: " + value;
            }
            this.tokenList.add(lex);
        }
        //int or double
        else if(Character.isDigit(c)){
            int decimalsFound = 0;
            while(decimalsFound <= 1 && (Character.isDigit(c) || c == '.')&& charList.size() > 0){
                value += c.toString();
                c=charList.get(0).getChar();
                //track number of decimals read
                if(c == '.')
                    ++decimalsFound;
                //remove if valid char
                if(decimalsFound <= 1 && (Character.isDigit(c) || c == '.'))
                    charList.remove(0);
            }
            if(decimalsFound == 0){            
                lex = new Lexeme(TokenType.INT_CONSTANT, value);
                outputString += "int constant: " + value;
            }
            else{
                lex = new Lexeme(TokenType.DOUBLE_CONSTANT, value);
                outputString += "double constant: " + value;
            }
            this.tokenList.add(lex);
        }
        //string
        else if(c == '\"'){
            while(charList.size() > 0){
                value += c.toString();
                c= charList.remove(0).getChar();
                if(c == '\"'){
                    value += c.toString();
                    break;
                }
            }
            //check if string finished
            if(c != '\"'){
                outputString += "error: " + value + " missing final quotation mark";
            }else{
                lex = new Lexeme(TokenType.STRING_CONSTANT, value);                
                this.tokenList.add(lex);
                outputString += "string constant: " + value;
            }
        }
        //whitespace
        else if(Character.isWhitespace(c)){
            return;
        }
        //operator or unknown token
        else{
            switch(c){
                case '=':
                case '(':
                case ')':
                case '+':
                case '-':
                case '*':
                case '/':
                case ',':
                case ';':
                    lex = new Lexeme(TokenType.OPERATOR, c.toString());
                    this.tokenList.add(lex);
                    outputString += "operator: " + c.toString();
                    break;
                default:
                    outputString += "error: " + c.toString() + " not recognized";
            }            
        }
        if(print)
            System.out.println(outputString);
    }
    
    //prints all tokens in object's <tokenList>
    private void printAllTokens(){
        for(Lexeme lex : tokenList){
            System.out.println("Next token is: "+ lex.getType()
            + " Next lexeme is: "+ lex.toString());
        }
    }

    public enum TokenType{
        INT_CONSTANT, DOUBLE_CONSTANT, STRING_CONSTANT, IDENT, KEYWORD, OPERATOR
    }

    private class Lexeme{
        private TokenType type;
        private String value;
        
        public Lexeme(TokenType t, String v){
            type = t;
            value = v;
        }
        public String toString(){
            return value;
        }
        public String getType(){
            return type.name();
        }
    }

    private class FileCharacter{
        private Character c;
        private int lineNum, position;

        public FileCharacter(Character input, int fcLineNum, int fcPosition){
            c = (char)input;
            lineNum = fcLineNum;
            position = fcPosition;
        }
        public Character getChar(){ return c; }
        public int getLine(){ return lineNum; }
        public int getPos(){ return position; }
    }
}