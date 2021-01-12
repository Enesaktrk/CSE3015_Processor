package com.company;

import java.io.*;
import java.util.Scanner;

public class Main {

    private final String[] instructionSet = {"AND", "ADD", "ANDI", "ADDI", "LD", "ST", "CMP" ,"JUMP", "JE", "JA", "JB", "JBE", "JAE"};
    private final int immBit = 7 ;
    private final int addressBit = 10 ;

    public static void main(String[] args) {

        Main main = new Main();
        main.readAndWriteFile("input.txt");

    }
    // read the file which name is come with parameter from main and call parser() function with its data
    private void readAndWriteFile(String fileName){

        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            FileWriter fileWriter = new FileWriter("output.txt");
            FileWriter fileWriter2 = new FileWriter("outputMem.txt");
            fileWriter2.append("v2.0 raw\n");
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                fileWriter.append(parser(data)+"\n");
                fileWriter2.append(parser(data)+"\n");
            }
            myReader.close();
            fileWriter.close();
            fileWriter2.close();
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String parser(String data){

        String[] parseOpcode = data.split(" ") ;
        String[] parseReg = parseOpcode[1].split(",") ;

        StringBuilder sb = new StringBuilder() ;

        sb.append(opCodeToBinary(parseOpcode[0]));   //opcode

        // imm value
        switch (parseOpcode[0]) {
            case "AND", "ADD" -> {
                sb.append(registerToBinary(parseReg[0]));    // dest
                sb.append(registerToBinary(parseReg[1]));    // src1
                sb.append("0000");
                sb.append(registerToBinary(parseReg[2]));    // src2
            }
            case "ANDI", "ADDI" -> {
                sb.append(registerToBinary(parseReg[0]));    // dest
                sb.append(registerToBinary(parseReg[1]));    // src1
                sb.append(immToBinart(parseReg[2]));         // imm value
            }
            case "CMP" -> {
                sb.append("0000000");
                sb.append(registerToBinary(parseReg[0]));    // op1
                sb.append(registerToBinary(parseReg[1]));    // op2
            }
            case "LD", "ST" -> {
                sb.append(registerToBinary(parseReg[0]));    // src1
                sb.append(addressToBinary(parseReg[1]));
            }
            case "JUMP" -> {
                sb.append("000");
                sb.append(addressToBinary(parseReg[0]));
            }
            case "JE" -> {
                sb.append("001");
                sb.append(addressToBinary(parseReg[0]));
            }
            case "JA" -> {
                sb.append("010");
                sb.append(addressToBinary(parseReg[0]));
            }
            case "JB" -> {
                sb.append("011");
                sb.append(addressToBinary(parseReg[0]));
            }
            case "JBE" -> {
                sb.append("100");
                sb.append(addressToBinary(parseReg[0]));
            }
            case "JAE" ->{
                sb.append("101");
                sb.append(addressToBinary(parseReg[0]));
            }

        }

        System.out.println(parseOpcode[0]);
        System.out.println(sb.toString());
        System.out.println(convertToHex(sb.toString()));
        return convertToHex(sb.toString()) ;


    }

    private String opCodeToBinary(String value){

        for (int i=0 ; i<instructionSet.length ; i++){
            if(instructionSet[i].equals(value)) {
                if (Integer.toBinaryString(i).length() == 1)
                    return "00" + Integer.toBinaryString(i);
                else if (Integer.toBinaryString(i).length() == 2)
                    return "0" + Integer.toBinaryString(i);
                else if (Integer.toBinaryString(i).length() == 3)
                    return Integer.toBinaryString(i) ;
                else
                    return "111" ;
            }
        }
        return "Opcode Not Found" ;

    }

    private String registerToBinary(String value){

        // we have just 8 register so we need just 3 bit for represent the registers R0->000 R1->001 .. R7->111
        String[] values = value.split("R");
        int valueInt = Integer.parseInt(values[1]) ;

        if (Integer.toBinaryString(valueInt).length() == 1)
            return "00"+Integer.toBinaryString(valueInt);
        else if (Integer.toBinaryString(valueInt).length() == 2)
            return "0"+Integer.toBinaryString(valueInt);
        else if (Integer.toBinaryString(valueInt).length() == 3)
            return Integer.toBinaryString(valueInt);
        else
            return "Register fault";

    }
    // this will change
    private String addressToBinary(String value){

        StringBuilder sb = new StringBuilder();
        //2's complement
        if(value.startsWith("-")){
            // negative number
            String[] splitSign = value.split("-");
            int number = Integer.parseInt(splitSign[1])-1 ;
            if (number > 511 || number<0)
                return "Negative number can not smaller than -64";
            for (int i=0 ; i<addressBit-Integer.toBinaryString(number).length() ; i++)
                sb.append("0");
            sb.append(Integer.toBinaryString(number));
            StringBuilder tempSb = new StringBuilder();
            for (int i=0 ; i<sb.toString().length() ; i++)
                if (sb.toString().charAt(i)=='0')  tempSb.append("1");
                else if(sb.toString().charAt(i)=='1')   tempSb.append("0");
            return tempSb.toString();
        }else{
            // positive number
            if (Integer.parseInt(value)>511)
                return "Positive number can not bigger than 63";
            for (int i=0 ; i<addressBit-Integer.toBinaryString(Integer.parseInt(value)).length() ; i++)
                sb.append("0");
            sb.append(Integer.toBinaryString(Integer.parseInt(value)));
            return sb.toString();
        }


    }

    private String immToBinart(String value){

        StringBuilder sb = new StringBuilder();
        //2's complement
        if(value.startsWith("-")){
            // negative number
            String[] splitSign = value.split("-");
            int number = Integer.parseInt(splitSign[1])-1 ;
            if (number > 63 || number<0)
                return "Negative number can not smaller than -64";
            for (int i=0 ; i<immBit-Integer.toBinaryString(number).length() ; i++)
                sb.append("0");
            sb.append(Integer.toBinaryString(number));
            StringBuilder tempSb = new StringBuilder();
            for (int i=0 ; i<sb.toString().length() ; i++)
                if (sb.toString().charAt(i)=='0')  tempSb.append("1");
                else if(sb.toString().charAt(i)=='1')   tempSb.append("0");
            return tempSb.toString();
        }else{
            // positive number
            if (Integer.parseInt(value)>63)
                return "Positive number can not bigger than 63";
            for (int i=0 ; i<immBit-Integer.toBinaryString(Integer.parseInt(value)).length() ; i++)
                sb.append("0");
            sb.append(Integer.toBinaryString(Integer.parseInt(value)));
            return sb.toString();
        }

    }

    private String convertToHex(String binaryValue){

        return Integer.toHexString(convertToDecimal(binaryValue.substring(0,4))).toUpperCase() +
               Integer.toHexString(convertToDecimal(binaryValue.substring(4,8))).toUpperCase() +
               Integer.toHexString(convertToDecimal(binaryValue.substring(8,12))).toUpperCase() +
               Integer.toHexString(convertToDecimal(binaryValue.substring(12,16))).toUpperCase() ;

    }

    private int convertToDecimal(String binaryFour){

        switch (binaryFour){
            case "0000" -> {return 0 ;}
            case "0001" -> {return 1 ;}
            case "0010" -> {return 2 ;}
            case "0011" -> {return 3 ;}
            case "0100" -> {return 4 ;}
            case "0101" -> {return 5 ;}
            case "0110" -> {return 6 ;}
            case "0111" -> {return 7 ;}
            case "1000" -> {return 8 ;}
            case "1001" -> {return 9 ;}
            case "1010" -> {return 10 ;}
            case "1011" -> {return 11 ;}
            case "1100" -> {return 12 ;}
            case "1101" -> {return 13 ;}
            case "1110" -> {return 14 ;}
            case "1111" -> {return 15 ;}
    }

        return 0;
    }

}
