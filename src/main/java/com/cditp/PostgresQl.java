package com.cditp;


@Component
public class PostgresQl implements Database{

    @Override
    public void connect() {
        System.out.println("Connecting to a PostgresQL database!!!");        
    }
    
}
