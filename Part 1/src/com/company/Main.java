package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private final String[] instructionSet = {"AND", "ADD", "ANDI", "ADDI", "CMP", "LD", "ST", "JUMP", "JE", "JA", "JB", "JBE", "JAE"};
    private ArrayList<String> binaryValue ;

    public static void main(String[] args) {

        Main main = new Main();
        main.readFile("input.txt");

    }

    private String opCodeToBinary(String value){

        for (int i=0 ; i<instructionSet.length ; i++){
            if(instructionSet[i].equals(value)) {
                if (Integer.toBinaryString(i).length() == 1)
                    return "000" + Integer.toBinaryString(i);
                else if (Integer.toBinaryString(i).length() == 2)
                    return "00" + Integer.toBinaryString(i);
                else if (Integer.toBinaryString(i).length() == 3)
                    return "0"+Integer.toBinaryString(i) ;
                else
                    return Integer.toBinaryString(i) ;
            }
        }
        return "Register Not Found" ;

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
    private String addressToBinary(String value, int addressBit){

        ArrayList<String> binaryAddress = new ArrayList<>() ;
        StringBuilder sb = new StringBuilder() ;
        for (int i=0 ; i<addressBit-Integer.toBinaryString(Integer.parseInt(value)).length() ; i++){
            binaryAddress.add("0");
        }
        binaryAddress.add(Integer.toBinaryString(Integer.parseInt(value)));
        for (String s : binaryAddress)  sb.append(s);
        return sb.toString();

    }

    private String immToBinart(String value,int immBit){

        StringBuilder sb = new StringBuilder();
        //2's complement
        if(value.startsWith("-")){
            // negative number
            String[] splitSign = value.split("-");
            int number = Integer.parseInt(splitSign[1])-1 ;
            for (int i=0 ; i<immBit-Integer.toBinaryString(number).length() ; i++)
                sb.append("0");
            sb.append(Integer.toBinaryString(number));
            StringBuilder tempSb = new StringBuilder();
            for (int i=0 ; i<sb.toString().length() ; i++)
                if (sb.toString().charAt(i)=='0')  tempSb.append("1");
                else if(sb.toString().charAt(i)=='1')   tempSb.append("0");
                else System.out.println("line 83 error check");
            return tempSb.toString();
        }else{
            // positive number
            for (int i=0 ; i<immBit-Integer.toBinaryString(Integer.parseInt(value)).length() ; i++)
                sb.append("0");
            sb.append(Integer.toBinaryString(Integer.parseInt(value)));
            return sb.toString();
        }

    }

    // read the file which name is come with parameter from main and call parser() function with its data
    private void readFile(String fileName){

        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                parser(data);
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private void parser(String data){

        String[] parseOpcode = data.split(" ") ;
        String[] parseReg = parseOpcode[1].split(",") ;

        StringBuilder sb = new StringBuilder() ;

        sb.append(opCodeToBinary(parseOpcode[0]));   //opcode

        // imm value
        switch (parseOpcode[0]) {
            case "AND", "ADD" -> {
                sb.append(registerToBinary(parseReg[0]));    // dest
                sb.append(registerToBinary(parseReg[1]));    // src1
                sb.append("000");
                sb.append(registerToBinary(parseReg[2]));    // src2
            }
            case "ANDI", "ADDI" -> {
                sb.append(registerToBinary(parseReg[0]));    // dest
                sb.append(registerToBinary(parseReg[1]));    // src1
                sb.append(immToBinart(parseReg[2],6));         // imm value
            }
            case "CMP" -> {
                sb.append("000000");
                sb.append(registerToBinary(parseReg[0]));    // op1
                sb.append(registerToBinary(parseReg[1]));    // op2
            }
            case "LD", "ST" -> {
                sb.append(registerToBinary(parseReg[0]));    // src1
                sb.append(addressToBinary(parseReg[1], 9));
            }
            case "JUMP", "JE", "JA", "JB", "JBE", "JAE" ->
                sb.append(addressToBinary(parseReg[0], 12));
        }

        System.out.println(parseOpcode[0]);
        System.out.println(sb.toString());

    }

}
