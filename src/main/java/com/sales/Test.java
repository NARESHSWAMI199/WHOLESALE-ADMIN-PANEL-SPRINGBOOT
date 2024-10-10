package com.sales;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {


    public static void main(String[] args) {

        int []steps = {11,12};
        int totalSteps = 0;
        for(int i = 0; i< steps.length; i++){
            totalSteps += steps[i];
        }
        List<JSONObject> updatedList = new ArrayList<>();

        String[] arr = {
                "PacketCount for 101_English_" +
                        ":@:PacketCount for 104_Bengali_" +
                        ":@:PacketCount for 304_Biology_English" +
                        ":@:PacketCount for 304_Biology_Hindi" +
                        ":@:PacketCount for 306_Chemistry_English" +
                        ":@:PacketCount for 306_Chemistry_Hindi" +
                        ":@:PacketCount for 307_Environmental_Studies_Assamese" +
                        ":@:PacketCount for 307_Environmental_Studies_English" +
                        ":@:PacketCount for 307_Environmental_Studies_Hindi" +
                        ":@:PacketCount for 501_General_Test_English" +
                        ":@:PacketCount for 501_General_Test_Hindi" +
                        ":@:QP_count_for_101_English_:@:QP_count_for_104_Bengali_:@:QP_count_for_304_Biology_English:@:QP_count_for_304_Biology_Hindi:@:QP_count_for_306_Chemistry_English:@:QP_count_for_306_Chemistry_Hindi:@:QP_count_for_307_Environmental_Studies_Assamese:@:QP_count_for_307_Environmental_Studies_English:@:QP_count_for_307_Environmental_Studies_Hindi:@:QP_count_for_501_General_Test_English:@:QP_count_for_501_General_Test_Hindi:@:QP_count_for_501_General_Test_Urdu:@:contact",
        };
        List<String> data = Arrays.asList(arr);
        String newHeaders = "";

        /** total columns */
        int totalRows = 2;
        /** headers */
        String sheetHeaders1[] = String.valueOf(data.get(0)).split(":@:");

        String sheetHeaders [] = new String[totalSteps];
        for (int i=0; i < totalSteps; i++){
            sheetHeaders [i] =sheetHeaders1[i];
        }
        /** totalColumnsOfSheet */
        int totalColumnsOfSheetOfEachRow = ((sheetHeaders.length) / totalRows);

        System.out.println("dataSize : "+ sheetHeaders.length);

        int nextIndex = 0;
        int stepIndex = 0;
        while (nextIndex < sheetHeaders.length) {
            //System.out.println(newHeaders + " : " +nextIndex);
            System.out.println(nextIndex);
            newHeaders += sheetHeaders[nextIndex] + ":@:";
            if ((nextIndex + steps[stepIndex]) > sheetHeaders.length - 1) {
                nextIndex = (nextIndex - totalSteps) + 1;
            } else {
                nextIndex += steps[stepIndex];
            }
            if (nextIndex == sheetHeaders.length - 1) {
                newHeaders += sheetHeaders[nextIndex];
                break;
            }
            if (stepIndex == steps.length-1){
                stepIndex = 0;
            }else {
                stepIndex++;
            }
        }
        JSONObject headers = new JSONObject();
        headers.put("header", newHeaders);
        updatedList.add(headers);


        for (int i = 1; i < data.size(); i++) {
            String newValues = "";
            int index = 0;
            String sheetValues[] = String.valueOf(data.get(i)).split(":@:");
            JSONObject value = new JSONObject();
            while (index < sheetValues.length) {
                //     System.out.println(newValues + " : " +index);
                newValues += sheetValues[index] + ":@:";
                if (index + totalColumnsOfSheetOfEachRow > sheetValues.length - 1) {
                    index = (index - ((totalColumnsOfSheetOfEachRow * (totalRows - 1)))) + 1;
                } else {
                    index += totalColumnsOfSheetOfEachRow;
                }
                if (index == sheetValues.length - 1) {
                    newValues += sheetValues[index];
                    break;
                }
            }
            value.put("value", newValues);
            updatedList.add(value);
        }
        System.out.println(updatedList);
    }

}

