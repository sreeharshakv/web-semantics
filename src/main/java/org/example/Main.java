package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        CsvToRdfConverter converter = new CsvToRdfConverter();
        try {
            converter.convert("src/main/java/org/example/City_zhvi_uc_sfrcondo_tier_0.33_0.67_sm_sa_month.csv", "output.rdf");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}