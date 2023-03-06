## About The Project

Visit data.gov to select 2 datasets of interest and write a program to convert these
datasets into semantic data in .rdf or .owl format

## Datasets

### We used 2 open datasets - placed in resources folder.

- CSV from Zillow, containing the monthly house price index of different neighbourhoods in LA, from 2010 to present - [link](https://files.zillowstatic.com/research/public_csvs/zhvi/Metro_zhvi_uc_sfrcondo_tier_0.33_0.67_sm_sa_month.csv?t=1677524930)
- CSV from data.lacity.org, containing crime records of LA, from 2010 to present - [link 1](https://data.lacity.org/Public-Safety/Crime-Data-from-2010-to-2019/63jg-8b9z), [link 2](https://data.lacity.org/Public-Safety/Crime-Data-from-2020-to-Present/2nrs-mtv8)

## Technologies
- Java 19
- OpenCSV
- Apache Jena

## Getting Started

### Step 1: Use below maven command to create JAR File with dependencies

`mvn clean compile assembly:single`

### Step 2: Run the JAR File in terminal

`java -Xmx8192m -jar target/web-semantics-1.0-SNAPSHOT-jar-with-dependencies.jar`

### The output RDF File **Crimes&RealEstate.rdf** should be generated in deliverables folder

