package com.cditp;


@Component
public class Drink implements Actions{

    @Override
    public void doAction() {
        System.out.println("Drinking !");        
    }

  
}
