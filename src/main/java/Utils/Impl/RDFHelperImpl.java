package Utils.Impl;

import Constants.AppConstants;
import Model.CrimeDO;
import Utils.RDFHelper;
import me.tongfei.progressbar.ProgressBar;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.XSD;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class RDFHelperImpl implements RDFHelper {

    static final String crimeDS = "https://data.lacity.org#";
    static final String housingDS = "https://www.zillow.com/research/data#";
    private static final Path ROOT_PATH = FileSystems.getDefault().getPath("").toAbsolutePath();
    OntModel model = ModelFactory.createOntologyModel();

    enum ClassNames {
        crimeRecord, area, crime, premise, weapon, status, location;

        String getUri() {
            return crimeDS + this.name();
        }
    }

    enum PropertyNames {
        hasDRNo, reportedOn, occurredOn, inArea, areaCode, areaDescription, crime, crimeCode, primaryCrimeDescription, premise, premiseCode,
        premiseDescription, usingWeapon, weaponCode, weaponDescription, currentStatus, statusCode, statusDescription, atLocation, location, crossStreet, hadHousingPriceIndex;

        String getUri() {
            if (this.name().equalsIgnoreCase("hasHousingPriceIndex")) {
                return housingDS + this.name();
            }
            return crimeDS + this.name();
        }
    }

    public void createModelFromCrimeDOMap(List<CrimeDO> crimeDOList) {
        initModel();
        populateModel(crimeDOList);
        exportModelToRDFFile();
    }

    private void initModel() {
        model.setNsPrefix("cds", crimeDS);
        model.setNsPrefix("hds", housingDS);

    }

    private void populateModel(List<CrimeDO> crimeDOList) {
        // Create classes
        OntClass crimeRecord = model.createClass(ClassNames.crimeRecord.getUri());
        crimeRecord.addComment("One incident of crime in the city of Los Angeles", "EN");

        // create Properties
        ObjectProperty hasDRNo = model.createObjectProperty(PropertyNames.hasDRNo.getUri());
        hasDRNo.addComment("Division of Records Number: Official file number made up of a 2 digit year, area ID, and 5 digits", "EN");
        hasDRNo.addDomain(crimeRecord);
        hasDRNo.addRange(XSD.xstring);

        ObjectProperty reportedOn = model.createObjectProperty(PropertyNames.reportedOn.getUri());
        reportedOn.addComment("The date on which the crime was reported", "EN");
        reportedOn.addDomain(crimeRecord);
        reportedOn.addRange(XSD.dateTimeStamp);

        ObjectProperty occurredOn = model.createObjectProperty(PropertyNames.occurredOn.getUri());
        occurredOn.addComment("The date on which the crime was committed", "EN");
        occurredOn.addDomain(crimeRecord);
        occurredOn.addRange(XSD.dateTimeStamp);

        ObjectProperty inArea = model.createObjectProperty(PropertyNames.inArea.getUri());
        inArea.addComment("These Geographic Areas are sequentially indicating the CPS", "EN");
        inArea.addDomain(crimeRecord);

        ObjectProperty areaCode = model.createObjectProperty(PropertyNames.areaCode.getUri());
        areaCode.addComment("These Geographic Areas are sequentially numbered from 1-21 indicating the CPS", "EN");
        areaCode.addDomain(crimeRecord);
        areaCode.addRange(XSD.positiveInteger);

        ObjectProperty areaDescription = model.createObjectProperty(PropertyNames.areaDescription.getUri());
        areaDescription.addComment("The 21 Geographic Areas or Patrol Divisions are also given a name designation that references a landmark or the surrounding community that it is responsible for.", "EN");
        areaDescription.addDomain(crimeRecord);
        areaDescription.addRange(XSD.xstring);

        ObjectProperty crime = model.createObjectProperty(PropertyNames.crime.getUri());
        crime.addComment("Indicates the type of crime committed", "EN");
        crime.addDomain(crimeRecord);

        ObjectProperty crimeCode = model.createObjectProperty(PropertyNames.crimeCode.getUri());
        crimeCode.addComment("Primary Crime Code", "EN");
        crimeCode.addDomain(crimeRecord);
        crimeCode.addRange(XSD.positiveInteger);

        ObjectProperty primaryCrimeDescription = model.createObjectProperty(PropertyNames.primaryCrimeDescription.getUri());
        primaryCrimeDescription.addComment("Defines the Crime Code provided", "EN");
        primaryCrimeDescription.addDomain(crimeRecord);
        primaryCrimeDescription.addRange(XSD.xstring);

        ObjectProperty premise = model.createObjectProperty(PropertyNames.premise.getUri());
        premise.addComment("The type of structure, vehicle, or location where the crime took place.", "EN");
        premise.addDomain(crimeRecord);

        ObjectProperty premiseCode = model.createObjectProperty(PropertyNames.premiseCode.getUri());
        premiseCode.addComment("Premise Code", "EN");
        premiseCode.addDomain(crimeRecord);
        premiseCode.addRange(XSD.positiveInteger);

        ObjectProperty premiseDescription = model.createObjectProperty(PropertyNames.premiseDescription.getUri());
        premiseDescription.addComment("Defines the Premise Code provided", "EN");
        premiseDescription.addDomain(crimeRecord);
        premiseDescription.addRange(XSD.xstring);

        ObjectProperty usingWeapon = model.createObjectProperty(PropertyNames.usingWeapon.getUri());
        usingWeapon.addComment("The type of weapon used in the crime.", "EN");
        usingWeapon.addDomain(crimeRecord);

        ObjectProperty weaponCode = model.createObjectProperty(PropertyNames.weaponCode.getUri());
        weaponCode.addComment("Weapon Code", "EN");
        weaponCode.addDomain(crimeRecord);
        weaponCode.addRange(XSD.positiveInteger);

        ObjectProperty weaponDescription = model.createObjectProperty(PropertyNames.weaponDescription.getUri());
        weaponDescription.addComment("Defines the Weapon Used Code provided", "EN");
        weaponDescription.addDomain(crimeRecord);
        weaponDescription.addRange(XSD.xstring);

        ObjectProperty currentStatus = model.createObjectProperty(PropertyNames.currentStatus.getUri());
        currentStatus.addComment("Status of the case.", "EN");
        currentStatus.addDomain(crimeRecord);

        ObjectProperty statusCode = model.createObjectProperty(PropertyNames.statusCode.getUri());
        statusCode.addComment("Status Code(IC is the default)", "EN");
        statusCode.addDomain(crimeRecord);
        statusCode.addRange(XSD.xstring);

        ObjectProperty statusDescription = model.createObjectProperty(PropertyNames.statusDescription.getUri());
        statusDescription.addComment("Defines the Status Code provided", "EN");
        statusDescription.addDomain(crimeRecord);
        statusDescription.addRange(XSD.xstring);

        ObjectProperty location = model.createObjectProperty(PropertyNames.location.getUri());
        location.addComment("Street address of crime incident rounded to the nearest hundred block to maintain anonymity.", "EN");
        location.addDomain(crimeRecord);
        location.addRange(XSD.xstring);

        ObjectProperty crossStreet = model.createObjectProperty(PropertyNames.crossStreet.getUri());
        crossStreet.addComment("Cross Street of rounded Address", "EN");
        crossStreet.addDomain(crimeRecord);
        crossStreet.addRange(XSD.xstring);

        ObjectProperty hadHousePriceIndex = model.createObjectProperty(PropertyNames.hadHousingPriceIndex.getUri());
        hadHousePriceIndex.addComment("The House Price Index of the Area, on the month of crime", "EN");
        hadHousePriceIndex.addDomain(crimeRecord);
        hadHousePriceIndex.addRange(XSD.xdouble);

        int l = crimeDOList.size();
        try (ProgressBar pb = new ProgressBar("Parsing records to model", l)) {
            for(CrimeDO value: crimeDOList) {
                Individual crimeRecordInstance = crimeRecord.createIndividual(crimeRecord.getURI() + value.getDrNo());

                model.add(crimeRecordInstance, hasDRNo, value.getDrNo());
                model.add(crimeRecordInstance, reportedOn, model.createTypedLiteral(value.getDateReported()));
                model.add(crimeRecordInstance, occurredOn, model.createTypedLiteral(value.getDateOccurred()));
                model.add(crimeRecordInstance, hadHousePriceIndex, model.createTypedLiteral(value.getHPI()));
                model.add(crimeRecordInstance, location, value.getLocation());
                model.add(crimeRecordInstance, crossStreet, value.getCrossStreet());
                model.add(crimeRecordInstance, areaCode, model.createTypedLiteral(value.getArea()));
                model.add(crimeRecordInstance, areaDescription, value.getAreaName());
                model.add(crimeRecordInstance, crimeCode, model.createTypedLiteral(value.getCrimeCodes().get(0)));
                model.add(crimeRecordInstance, primaryCrimeDescription, value.getPrimaryCrimeDesc());
                model.add(crimeRecordInstance, premiseCode, model.createTypedLiteral(value.getPremise()));
                model.add(crimeRecordInstance, premiseDescription, value.getPremiseDesc());
                model.add(crimeRecordInstance, weaponCode, model.createTypedLiteral(value.getWeapon()));
                model.add(crimeRecordInstance, weaponDescription, value.getWeaponDesc());
                model.add(crimeRecordInstance, statusCode, value.getStatus());
                model.add(crimeRecordInstance, statusDescription, value.getStatusDesc());

                pb.step();
            }
        }
    }

    public void exportModelToRDFFile() {
        System.out.println("Writing model to file. Please wait for confirmation to access the file..");
        String filePath = ROOT_PATH.resolve("deliverables").resolve(AppConstants.OUTPUT_FILE_NAME).toString();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath))) {
            model.write(out);
            System.out.println("Export process complete. Please find the output file in the deliverables folder.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
