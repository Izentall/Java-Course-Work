package com.example.demo;

import Service_3.Service3;

import java.io.FileNotFoundException;

public class ThirdService {

    public static void main(String[] args)
    {
        Service3 service3 = null;
        try {
            service3 = new Service3("1.json", -7, 7);
            service3.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
