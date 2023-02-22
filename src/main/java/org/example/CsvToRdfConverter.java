package org.example;

import org.apache.jena.rdf.model.*;

import java.io.*;

public class CsvToRdfConverter {

    public void convert(String csvFilePath, String rdfFilePath) throws IOException {
        // Create an empty RDF model
        Model model = ModelFactory.createDefaultModel();

        // Define namespaces for the RDF properties
        String csvNs = "http://example.org/csv/";
        String rdfNs = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        model.setNsPrefix("csv", csvNs);
        model.setNsPrefix("rdf", rdfNs);

        // Read the CSV file and create RDF statements
        try (BufferedReader csvReader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = csvReader.readLine()) != null) {
                String[] values = line.split(",");
                Resource subject = model.createResource(csvNs + values[0]);
                Property predicate = model.createProperty(csvNs + "hasValue");
                Literal object = model.createLiteral(values[1]);
                Statement statement = model.createStatement(subject, predicate, object);
                model.add(statement);
            }
        }

        // Write the RDF model to a file
        try (OutputStream rdfStream = new FileOutputStream(rdfFilePath)) {
            model.write(rdfStream, "RDF/XML");
        }

        System.out.println("Conversion completed successfully!");
    }
}
