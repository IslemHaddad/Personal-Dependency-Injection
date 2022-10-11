package com.cditp;


@Component
public class Eat implements Actions{

    @Override
    public void doAction() {
        System.out.println("Eating !"); 
    }

    
}
