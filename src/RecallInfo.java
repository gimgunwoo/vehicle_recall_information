package com.example.brian.vehiclerecallinfo;

/**
 * Created by brian on 07/08/15.
 */
public class RecallInfo {
    private String make;
    private String year;
    private String name;
    private String part;
    private String desc;

    public RecallInfo(){}

    public RecallInfo(String m, String y, String n, String p, String d){
        make = m;
        year = y;
        name = n;
        part = p;
        desc = d;
    }

    public String getMake(){
        return make;
    }

    public String getYear(){
        return year;
    }

    public String getName(){
        return name;
    }

    public String getPart(){
        return part;
    }

    public String getDesc(){
        return desc;
    }

    public void setMake(String m){
        make = m;
    }

    public void setYear(String y){
        year = y;
    }

    public void setName(String n){
        name = n;
    }

    public void setPart(String p){
        part = p;
    }

    public void setDesc(String d){
        desc = d;
    }

    public String toString(){
        return "[" + make + "," + year + "," + name + "," + part + "," + desc + "]";
    }

}
