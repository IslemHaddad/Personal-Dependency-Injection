package com.cditp;



@Component
public class MongoDb implements Database{

    @Override
    public void connect() {
        System.out.println("Connecting to MongoDb database!!");
        
    }
    
}
