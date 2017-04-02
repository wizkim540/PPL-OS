package main;

import java.util.Scanner;

import util.Interpreter;

public class MainInterpreter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose number to do:");
        System.out.println("1. Interpret from assembly code to address code");
        System.out.println("2. Interpret from address code to machine code in hexadecimal");
        System.out.println("3. Interpret from address code to machine code in decimal");
        System.out.print("Number choosen: ");
        int input = scanner.nextInt();
        scanner.nextLine();
        String readFrom, writeTo;
        System.out.println("File to be read: ");
        readFrom = scanner.nextLine();
        System.out.println("File to be write: ");
        writeTo = scanner.nextLine();
        if(!readFrom.trim().equals("") && !writeTo.trim().equals("") && !readFrom.equals(writeTo)) {
            switch(input)
            {
                case 1:
                    Interpreter.assemblyToAddressCode(readFrom, writeTo);
                    break;
                case 2:
                    Interpreter.interpretToFileInHex(readFrom, writeTo);
                    break;
                case 3:
                    Interpreter.interpretToFileInDecimal(readFrom, writeTo);
                    break;
                default: System.out.println("Do nothing"); break;
            }
        }
        else System.out.println("2 arguments needed: address code file path and machine code file path (file will be created)");
        scanner.close();
    }
    
}
