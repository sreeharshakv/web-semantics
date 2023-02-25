## About The Project

Visit data.gov to select 2 datasets of interest and write a program to convert these
datasets into semantic data in .rdf or .owl format

## Datasets

### We used 2 open datasets - placed in resources folder.

- CSV from Zillow, containing the monthly house price index of different neighbourhoods in LA, from 2010 to present
- CSV from data.lacity.org, containing crime records of LA, from 2010 to present.

## Technologies
- Java 19
- OpenCSV
- Apache Jena

## Getting Started

### Use below maven command to create JAR File with dependencies

`mvn clean compile assembly:single`

### Run the JAR File in terminal

`java -Xmx16384m -jar target/web-semantics-1.0-SNAPSHOT-jar-with-dependencies.jar`

### the output RDF File **Crimes&RealEstate.rdf** should be generated in deliverables folder

