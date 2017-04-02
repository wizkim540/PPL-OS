package util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Interpreter {

    private static String[] memoryHex = null;
    private static String inProcessCode = null;
    
	/**
	 * Interpret address code from its path and write the machine code to machine code file (in hexadecimal).
	 * Machine code file is created.
	 * @param addressCodeFilePathName - path to address code file
	 * @param machineCodeFilePathName - path to (new) machine code file
	 * @return true - success, false - not success
	 */
	public static boolean interpretToFileInHex(String addressCodeFilePathName, String machineCodeFilePathName) {
		BufferedReader readFile = null;
		BufferedWriter writeFile = null;
        
        try {
			readFile = new BufferedReader(new FileReader(addressCodeFilePathName));
			writeFile = new BufferedWriter(new FileWriter(machineCodeFilePathName));
			
			while((inProcessCode = readFile.readLine()) != null) {
			    if(inProcessCode.trim().equals("")) continue;
			    writeFile.append("0x" + interpret(inProcessCode));
                writeFile.newLine();
            }
		} catch (FileNotFoundException e) {
			System.out.println("Error - Address Code file not found");
			System.out.println(addressCodeFilePathName);
		} catch (IOException e) {
			System.out.println("Error - Machine Code file can not be made");
			System.out.println(machineCodeFilePathName);
		} finally {
		    try {
    		    readFile.close();
    		} catch (IOException e) {
		        System.out.println("Failed to do close() on BufferedReader");
		    }

		    try {
                writeFile.close();
            } catch (IOException e) {
                System.out.println("Failed to do close() on BufferedWriter");
            }
		}
		return true;
	}
	
	/**
     * Interpret address code from its path and write the machine code to machine code file (in decimal).
     * Machine code file is created.
     * @param addressCodeFilePathName - path to address code file
     * @param machineCodeFilePathName - path to (new) machine code file
     * @return true - success, false - not success
     */
    public static boolean interpretToFileInDecimal(String addressCodeFilePathName, String machineCodeFilePathName) {
        BufferedReader readFile = null;
        BufferedWriter writeFile = null;
        
        try {
            readFile = new BufferedReader(new FileReader(addressCodeFilePathName));
            writeFile = new BufferedWriter(new FileWriter(machineCodeFilePathName));
            
            while((inProcessCode = readFile.readLine()) != null) {
                if(inProcessCode.trim().equals("")) continue;
                writeFile.append("" + Integer.parseInt(interpret(inProcessCode), 16));
                writeFile.newLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error - Address Code file not found");
            System.out.println(addressCodeFilePathName);
        } catch (IOException e) {
            System.out.println("Error - Machine Code file can not be made");
            System.out.println(machineCodeFilePathName);
        } finally {
            try {
                readFile.close();
            } catch (IOException e) {
                System.out.println("Failed to do close() on BufferedReader");
            }

            try {
                writeFile.close();
            } catch (IOException e) {
                System.out.println("Failed to do close() on BufferedWriter");
            }
        }
        return true;
    }
    
    public static boolean assemblyToAddressCode(String assemblyCodeFilePath, String addressCodeFilePath) {
        BufferedReader readFile = null;
        BufferedWriter writeFile = null;
        ArrayList<String> label = null;
        ArrayList<Integer> lineOfLabel = null;
        int lineNumber = 0;
        
        try {
            readFile = new BufferedReader(new FileReader(assemblyCodeFilePath));
            writeFile = new BufferedWriter(new FileWriter(addressCodeFilePath));
            label = new ArrayList<String>();
            lineOfLabel = new ArrayList<Integer>();
            
            while((inProcessCode = readFile.readLine()) != null) {
                if(inProcessCode.trim().equals("")) continue;
                inProcessCode = inProcessCode.trim();
                if(inProcessCode.charAt(inProcessCode.indexOf(' ')-1) == ':') {
                    label.add(inProcessCode.substring(0, inProcessCode.indexOf(' ')-1));
                    lineOfLabel.add(new Integer(lineNumber));
                }
                lineNumber++;
            }
            
            readFile.close();
            readFile = new BufferedReader(new FileReader(assemblyCodeFilePath));
            String str;
            while((inProcessCode = readFile.readLine()) != null) {
                if(inProcessCode.trim().equals("")) continue;
                inProcessCode = inProcessCode.trim();
                if(inProcessCode.indexOf(' ') >= 0 && inProcessCode.charAt(inProcessCode.indexOf(' ')-1) == ':') {
                    inProcessCode = inProcessCode.substring(inProcessCode.indexOf(' ')+1);
                }
                if(inProcessCode.indexOf(' ') >= 0) {
                    switch(inProcessCode.substring(0, inProcessCode.indexOf(' ')).toUpperCase())
                    {
                        case "JMP":
                        case "JLT":
                        case "JGT":
                        case "JE":
                        case "JNE":
                            str = inProcessCode.substring(inProcessCode.lastIndexOf(' ')+1);
                            if(label.contains(str)) {
                                inProcessCode = inProcessCode.replace(str, 
                                        Integer.toString(lineOfLabel.get(label.indexOf(str))*4));
                            }
                        default: break;
                    }
                }
                writeFile.append(inProcessCode);
                writeFile.newLine();
                writeFile.flush();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error - Assembly Code file not found");
            System.out.println(assemblyCodeFilePath);
        } catch (IOException e) {
            System.out.println("Error - Address Code file can not be made / fail to do close()");
            System.out.println(addressCodeFilePath);
        } finally {
            try {
                readFile.close();
            } catch (IOException e) {
                System.out.println("Failed to do close() on BufferedReader");
            }

            try {
                writeFile.close();
            } catch (IOException e) {
                System.out.println("Failed to do close() on BufferedWriter");
            }
        }
        return true;
    }
    
	/**
	 * Interpret an address code
	 * @param addressCode - String of address code
	 * @return String of machine code in hex
	 */
	private static String interpret(String addressCode) {
		String[] instruction = addressCode.split("[ ,]+");
		StringBuffer machineCodeHex = new StringBuffer(8);
		
		switch(instruction.length) {
			case 1:
			    if(instruction[0].equals("HALT") || instruction[0].equals("halt")) machineCodeHex.append("7F000000");
				break;
			case 2:
				switch(instruction[0]) {
				    case "JMP":
				    case "jmp":
				        machineCodeHex.append("15");
				        machineCodeHex.append(addressToHex(instruction[1], 6));
				        break;
				    case "JMPR":
				    case "jmpr":
				        machineCodeHex.append("16");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append("00000");
				        break;
				    default: break;
				}
				break;
			case 3:
			    switch(instruction[0]) {
			        case "MOVR":
			        case "movr":
			            machineCodeHex.append("02");
			            machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append("0000");
			            break;
			        case "MOVI":
			        case "movi":
			            machineCodeHex.append("03");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(decToHexWithNHex(instruction[2], 5));
			            break;
			        case "MOV":
			        case "mov":
			            machineCodeHex.append("04");
			            memoryHex = memoryToHex(instruction[1], 1, 4);
			            machineCodeHex.append(memoryHex[0]);
			            machineCodeHex.append(registerToHex(instruction[2], 1));
			            machineCodeHex.append(memoryHex[1]);
			            break;
			        case "MOVM":
			        case "movm":
			            machineCodeHex.append("05");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
			            memoryHex = memoryToHex(instruction[2], 1, 4);
                        machineCodeHex.append(memoryHex[0]);
                        machineCodeHex.append(memoryHex[1]);
                        break;
			        case "MOVB":
			        case "movb":
                        machineCodeHex.append("06");
                        memoryHex = memoryToHex(instruction[1], 1, 4);
                        machineCodeHex.append(memoryHex[0]);
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(memoryHex[1]);
                        break;
			        case "MOVMB":
			        case "movmb":
                        machineCodeHex.append("07");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        memoryHex = memoryToHex(instruction[2], 1, 4);
                        machineCodeHex.append(memoryHex[0]);
                        machineCodeHex.append(memoryHex[1]);
                        break;
			        case "MOVH":
			        case "movh":
                        machineCodeHex.append("08");
                        memoryHex = memoryToHex(instruction[1], 1, 4);
                        machineCodeHex.append(memoryHex[0]);
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(memoryHex[1]);
                        break;
			        case "MOVL":
			        case "movl":
                        machineCodeHex.append("0A");
                        memoryHex = memoryToHex(instruction[1], 1, 4);
                        machineCodeHex.append(memoryHex[0]);
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(memoryHex[1]);
                        break;
			        case "MOVML":
			        case "movml":
                        machineCodeHex.append("0B");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        memoryHex = memoryToHex(instruction[2], 1, 4);
                        machineCodeHex.append(memoryHex[0]);
                        machineCodeHex.append(memoryHex[1]);
                        break;
			        case "MOVMHW":
			        case "movmhw":
                        machineCodeHex.append("1D");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        memoryHex = memoryToHex(instruction[2], 1, 4);
                        machineCodeHex.append(memoryHex[0]);
                        machineCodeHex.append(memoryHex[1]);
                        break;
                    default: break;
			    }
				break;
			case 4:
			    switch(instruction[0]) {
    			    case "ADD":
    			    case "add":
                        machineCodeHex.append("00");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(registerToHex(instruction[3], 1));
                        machineCodeHex.append("000");
                        break;
    			    case "ADDI":
    			    case "addi":
                        machineCodeHex.append("01");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(decToHexWithNHex(instruction[3], 4));
                        break;
    			    case "JE":
    			    case "je":
                        machineCodeHex.append("17");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(decToHexWithNHex(instruction[3], 4));
                        break;
    			    case "JNE":
    			    case "jne":
                        machineCodeHex.append("18");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(decToHexWithNHex(instruction[3], 4));
                        break;
    			    case "JLT":
    			    case "jlt":
                        machineCodeHex.append("19");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(decToHexWithNHex(instruction[3], 4));
                        break;
    			    case "JGT":
    			    case "jgt":
                        machineCodeHex.append("1A");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(decToHexWithNHex(instruction[3], 4));
                        break;
    			    case "SUB":
    			    case "sub":
                        machineCodeHex.append("1B");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(registerToHex(instruction[3], 1));
                        machineCodeHex.append("000");
                        break;
    			    case "SUBI":
    			    case "subi":
                        machineCodeHex.append("1C");
                        machineCodeHex.append(registerToHex(instruction[1], 1));
                        machineCodeHex.append(registerToHex(instruction[2], 1));
                        machineCodeHex.append(decToHexWithNHex(instruction[3], 4));
                        break;
                    default: break;
			    }
			    break;
			default: break;
		}
		
		instruction = null;
		return machineCodeHex.toString();
	}
	
	/**
	 * Convert from address number location in decimal as String 
	 * to a hexadecimal as String in totalHex size
	 * @param addressNumber - decimal of the address location
	 * @param totalHex - number of hexadecimal of the returned value
	 * @return String contains hex value of address location
	 */
	private static String addressToHex(String addressOperand, int totalHex) {
	    return decToHexWithNHex(addressOperand, totalHex);
	}
	
	/**
	 * Convert from registerOperand with 
	 * to a hexadecimal as String in totalHex size
	 * @param registerOperand - with pattern started by "r" and followed by the number of register chosen
	 * @param totalHex - number of hexadecimal of the returned value
	 * @return String contains hex value of register number
	 */
	private static String registerToHex(String registerOperand, int totalHex) {
	    if(registerOperand.charAt(0) == 'r' || registerOperand.charAt(0) == 'R')
	        return decToHexWithNHex(registerOperand.substring(1), totalHex);
	    else return null;
	}
	
	/**
	 * Convert from memoryOperand to hexadecimal of register and offset
	 * @param memoryOperand - has a pattern start with "[", followed by registerOperand, "+", an offset, and a "]"
	 * @param registerTotalHex - number of hexadecimal of the register returned value
	 * @param immediateTotalHex - number of hexadecimal of the offset returned value
	 * @return String[] with 2 values, first one is register number in hexadecimal and the second one is offset value in hexadecimal
	 */
	private static String[] memoryToHex(String memoryOperand, int registerTotalHex, int immediateTotalHex) {
	    if(memoryOperand.charAt(0) == '[' && memoryOperand.charAt(memoryOperand.length()-1) == ']') {
	        //If true do these
	        int plusOperatorLoc = memoryOperand.indexOf('+');
	        String registerHex = registerToHex(memoryOperand.substring(1, plusOperatorLoc), 
	                                           registerTotalHex);
	        String immediateHex = decToHexWithNHex(memoryOperand.substring(plusOperatorLoc+1, 
	                                               memoryOperand.length()-1), immediateTotalHex);
	        String[] returnedHexes = new String[2];
	        returnedHexes[0] = registerHex;
	        returnedHexes[1] = immediateHex;
	        return returnedHexes;
	    }
	    return null;
	}
	
	/**
	 * Convert a decimal to hexadecimal with an assigned digit
	 * @param decimalNumber - number to be converted in decimal
	 * @param totalHex - number of hexadecimal(digit) in the returned value
	 * @return String with totalHex digit(s) of decimal value in hexadecimal
	 */
	private static String decToHexWithNHex(String decimalNumber, int totalHex) {
	    StringBuffer returnedHex = new StringBuffer(totalHex);
        String hexValue = Integer.toHexString(Integer.parseInt(decimalNumber));
        for(int i=hexValue.length()-1, j=totalHex-1; j>=0; i--, j--) {
            if(i>=0) returnedHex.append(hexValue.charAt(i));
            else returnedHex.append('0');
        }
        return returnedHex.reverse().toString();
	}
}
