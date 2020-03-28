package com.example.project_master;

import java.util.ArrayList;

public class SplitResponse {

    public SplitResponse() {
    }



    private String splitFirstLast(String response) {
        String newResponse=response.substring(1,response.length()-1);
        return newResponse;

    }


    private String splitFirst(String response) {
        String newResponse=response.substring(1);
        return newResponse;

    }

    private ArrayList<String> splitDoubleArraylist(String response) {
        String newResponse= splitFirstLast(response);
        String[] tab= newResponse.split("]");
        ArrayList<String> responseSplit= new ArrayList<>();
        for (String string : tab) {
            responseSplit.add(splitFirst(string));

        }
        return responseSplit;

    }


    public ArrayList<ArrayList<String>> splitDoubleListData(String response) {
        ArrayList<String> newResponse= splitDoubleArraylist(response);
        ArrayList<ArrayList<String>> data= new ArrayList<>();
        for (String string: newResponse){
            String[] tab= string.split(",");

            ArrayList<String>dataObject= new ArrayList<>();
            for (String myData : tab){
                dataObject.add(myData);
            }
            data.add(dataObject);
        }
        return data;
    }



    public ArrayList<String> splitSingleListData(String response){
        String newResponse= splitFirstLast(response);
        String[] tab= newResponse.split(",");
        ArrayList<String>listData= new ArrayList<>();
        for (String data: tab) {
            listData.add(data);
        }
        return listData;

    }
}
