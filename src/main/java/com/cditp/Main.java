package com.cditp;


@Component
public class Main {


    @Inject
    private static Dog dog;

    @Inject
    private static Connection connection;

    public static void main(String[] args){
        Container.startApplication(Main.class);
        dog.doAction();

        connection.getDatabase().connect();
    }
}
