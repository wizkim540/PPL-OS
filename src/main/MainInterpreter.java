package main;

import java.util.Scanner;

import util.Interpreter;

public class MainInterpreter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String readFrom, writeTo;
        System.out.println("File to be read: ");
        readFrom = scanner.nextLine();
        System.out.println("File to be write: ");
        writeTo = scanner.nextLine();
        if(!readFrom.trim().equals("") && !writeTo.trim().equals("")) {
            Interpreter.interpretToFileInDecimal(readFrom, writeTo);
        }
        else System.out.println("2 arguments needed: address code file path and machine code file path (file will be created)");
        scanner.close();
    }
    
}
