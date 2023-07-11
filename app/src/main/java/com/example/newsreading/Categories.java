package com.example.newsreading;

import static java.util.Arrays.asList;

import android.content.res.Resources;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Categories {
    public ArrayList<String> getData(){
        ArrayList<String> data=new ArrayList<>();
        data.addAll(asList("tamil","headlines","disaster","horoscope","space","Medical","hollywood"));
        return data;
    }
}
