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
    private static final String duURI = "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#";
    OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
    HashMap<String, Individual> instanceHashMap = new HashMap<>();

    enum ClassNames {
        CrimeRecord, Area, Crime, PropertyCrime, PersonalCrime, Premise, Weapon, Status;

        String getUri() {
            return crimeDS + this.name();
        }
    }

    enum PropertyNames {
        hasDRNo, reportedOn, occurredOn, inArea, areaCode, areaDescription, crimeOccurred, crimeCode, primaryCrimeDescription, hasPremise, premiseCode,
        premiseDescription, usingWeapon, weaponCode, weaponDescription, hasCurrentStatus, statusCode, statusDescription, atLocation, location, crossStreet, hadHousingPriceIndex;

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
        model.setNsPrefix("du", duURI);

    }

    private void populateModel(List<CrimeDO> crimeDOList) {
        // Create classes
        OntClass crimeRecord = model.createClass(ClassNames.CrimeRecord.getUri());
        crimeRecord.addComment("One incident of crime in the city of Los Angeles", "EN");

        OntClass areaInfo = model.createClass(ClassNames.Area.getUri());
        areaInfo.addComment("The LAPD has 21 Community Police Stations referred to as Geographic Areas within the department", "EN");

        OntClass crimeInfo = model.createClass(ClassNames.Crime.getUri());
        crimeInfo.addComment("Indicates the crime committed", "EN");

        OntClass PropertyCrime = model.createClass(ClassNames.PropertyCrime.getUri());
        PropertyCrime.addComment("Crimes against Property", "EN");
        PropertyCrime.addSuperClass(crimeInfo);

        OntClass PersonalCrime = model.createClass(ClassNames.PersonalCrime.getUri());
        PersonalCrime.addComment("Crimes against People", "EN");
        PersonalCrime.addSuperClass(crimeInfo);

        crimeInfo.addSubClass(PropertyCrime);
        crimeInfo.addSubClass(PersonalCrime);

        OntClass premiseInfo = model.createClass(ClassNames.Premise.getUri());
        premiseInfo.addComment("The type of structure, vehicle, or location where the crime took place", "EN");

        OntClass weaponInfo = model.createClass(ClassNames.Weapon.getUri());
        weaponInfo.addComment("The type of weapon used in the crime", "EN");

        OntClass statusInfo = model.createClass(ClassNames.Status.getUri());
        statusInfo.addComment("Status of the case", "EN");

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
        areaCode.addDomain(areaInfo);
        areaCode.addRange(XSD.positiveInteger);

        ObjectProperty areaDescription = model.createObjectProperty(PropertyNames.areaDescription.getUri());
        areaDescription.addComment("The 21 Geographic Areas or Patrol Divisions are also given a name designation that references a landmark or the surrounding community that it is responsible for.", "EN");
        areaDescription.addDomain(areaInfo);
        areaDescription.addRange(XSD.xstring);

        ObjectProperty crime = model.createObjectProperty(PropertyNames.crimeOccurred.getUri());
        crime.addComment("Indicates the type of crime committed", "EN");
        crime.addDomain(crimeRecord);

        ObjectProperty crimeCode = model.createObjectProperty(PropertyNames.crimeCode.getUri());
        crimeCode.addComment("Primary Crime Code", "EN");
        crimeCode.addDomain(crimeInfo);
        crimeCode.addRange(XSD.positiveInteger);

        ObjectProperty primaryCrimeDescription = model.createObjectProperty(PropertyNames.primaryCrimeDescription.getUri());
        primaryCrimeDescription.addComment("Defines the Crime Code provided", "EN");
        primaryCrimeDescription.addDomain(crimeInfo);
        primaryCrimeDescription.addRange(XSD.xstring);

        ObjectProperty premise = model.createObjectProperty(PropertyNames.hasPremise.getUri());
        premise.addComment("The type of structure, vehicle, or location where the crime took place.", "EN");
        premise.addDomain(crimeRecord);

        ObjectProperty premiseCode = model.createObjectProperty(PropertyNames.premiseCode.getUri());
        premiseCode.addComment("Premise Code", "EN");
        premiseCode.addDomain(premiseInfo);
        premiseCode.addRange(XSD.positiveInteger);

        ObjectProperty premiseDescription = model.createObjectProperty(PropertyNames.premiseDescription.getUri());
        premiseDescription.addComment("Defines the Premise Code provided", "EN");
        premiseDescription.addDomain(premiseInfo);
        premiseDescription.addRange(XSD.xstring);

        ObjectProperty usingWeapon = model.createObjectProperty(PropertyNames.usingWeapon.getUri());
        usingWeapon.addComment("The type of weapon used in the crime.", "EN");
        usingWeapon.addDomain(crimeRecord);

        ObjectProperty weaponCode = model.createObjectProperty(PropertyNames.weaponCode.getUri());
        weaponCode.addComment("Weapon Code", "EN");
        weaponCode.addDomain(weaponInfo);
        weaponCode.addRange(XSD.positiveInteger);

        ObjectProperty weaponDescription = model.createObjectProperty(PropertyNames.weaponDescription.getUri());
        weaponDescription.addComment("Defines the Weapon Used Code provided", "EN");
        weaponDescription.addDomain(weaponInfo);
        weaponDescription.addRange(XSD.xstring);

        ObjectProperty currentStatus = model.createObjectProperty(PropertyNames.hasCurrentStatus.getUri());
        currentStatus.addComment("Status of the case.", "EN");
        currentStatus.addDomain(crimeRecord);

        ObjectProperty statusCode = model.createObjectProperty(PropertyNames.statusCode.getUri());
        statusCode.addComment("Status Code(IC is the default)", "EN");
        statusCode.addDomain(statusInfo);
        statusCode.addRange(XSD.xstring);

        ObjectProperty statusDescription = model.createObjectProperty(PropertyNames.statusDescription.getUri());
        statusDescription.addComment("Defines the Status Code provided", "EN");
        statusDescription.addDomain(statusInfo);
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
            // todo: revert this after testing
            for(int i = 0; i<=100; i++) {
                CrimeDO value = crimeDOList.get(i);
                Individual crimeRecordInstance = crimeRecord.createIndividual(crimeDS + value.getDrNo());
                Individual areaInstance;
                Individual crimeInstance;
                Individual premiseInstance;
                Individual weaponInstance;
                Individual statusInstance;

                model.add(crimeRecordInstance, hasDRNo, value.getDrNo());
                model.add(crimeRecordInstance, reportedOn, model.createTypedLiteral(value.getDateReported()));
                model.add(crimeRecordInstance, occurredOn, model.createTypedLiteral(value.getDateOccurred()));
                model.add(crimeRecordInstance, hadHousePriceIndex, model.createTypedLiteral(value.getHPI()));
                if (!value.getLocation().equals("")) {
                    model.add(crimeRecordInstance, location, value.getLocation());
                }
                if (!value.getCrossStreet().equals("")) {
                    model.add(crimeRecordInstance, crossStreet, value.getCrossStreet());
                }

                if (value.getArea() != 0 && !value.getAreaName().equals("")) {
                    if (!instanceHashMap.containsKey(crimeDS + value.getArea())) {
                        areaInstance = createInstanceIfAbsent(areaInfo, crimeDS + value.getArea());
                        model.add(areaInstance, areaCode, model.createTypedLiteral(value.getArea()));
                        model.add(areaInstance, areaDescription, value.getAreaName());
                        instanceHashMap.replace(crimeDS + value.getArea(), areaInstance);
                    } else {
                        areaInstance = instanceHashMap.get(crimeDS + value.getArea());
                    }
                    model.add(crimeRecordInstance, inArea, areaInstance);
                }

                if (value.getCrimeCodes().get(0) != 0  && !value.getPrimaryCrimeDesc().equals("")) {
                    if (!instanceHashMap.containsKey(crimeDS + value.getCrimeCodes().get(0))) {
                        crimeInstance = createInstanceIfAbsent(crimeInfo, crimeDS + value.getCrimeCodes().get(0));
                        model.add(crimeInstance, crimeCode, model.createTypedLiteral(value.getCrimeCodes().get(0)));
                        model.add(crimeInstance, primaryCrimeDescription, value.getPrimaryCrimeDesc());
                        instanceHashMap.replace(crimeDS + value.getCrimeCodes().get(0), crimeInstance);
                    } else {
                        crimeInstance = instanceHashMap.get(crimeDS + value.getCrimeCodes().get(0));
                    }
                    model.add(crimeRecordInstance, crime, crimeInstance);
                }

                if (value.getPremise() != 0 && !value.getPremiseDesc().equals("")) {
                    if (!instanceHashMap.containsKey(crimeDS + value.getPremise())) {
                        premiseInstance = createInstanceIfAbsent(premiseInfo, crimeDS + value.getPremise());
                        model.add(premiseInstance, premiseCode, model.createTypedLiteral(value.getPremise()));
                        model.add(premiseInstance, premiseDescription, value.getPremiseDesc());
                        instanceHashMap.replace(crimeDS + value.getPremise(), premiseInstance);
                    } else {
                        premiseInstance = instanceHashMap.get(crimeDS + value.getPremise());
                    }
                    model.add(crimeRecordInstance, premise, premiseInstance);
                }

                if (value.getWeapon() != 0 && !value.getWeaponDesc().equals("")) {
                    if (!instanceHashMap.containsKey(crimeDS + value.getWeapon())) {
                        weaponInstance = createInstanceIfAbsent(weaponInfo, crimeDS + value.getWeapon());
                        model.add(weaponInstance, weaponCode, model.createTypedLiteral(value.getWeapon()));
                        model.add(weaponInstance, weaponDescription, value.getWeaponDesc());
                        instanceHashMap.replace(crimeDS + value.getWeapon(), weaponInstance);
                    } else {
                        weaponInstance = instanceHashMap.get(crimeDS + value.getWeapon());
                    }
                    model.add(crimeRecordInstance, usingWeapon, weaponInstance);
                }

                if (!value.getStatus().equals("") && !value.getStatusDesc().equals("")) {
                    if (!instanceHashMap.containsKey(crimeDS + value.getStatus())) {
                        statusInstance = createInstanceIfAbsent(statusInfo, crimeDS + value.getStatus());
                        model.add(statusInstance, statusCode, value.getStatus());
                        model.add(statusInstance, statusDescription, value.getStatusDesc());
                        instanceHashMap.replace(crimeDS + value.getStatus(), statusInstance);
                    } else {
                        statusInstance = instanceHashMap.get(crimeDS + value.getStatus());
                    }
                    model.add(crimeRecordInstance, currentStatus, statusInstance);
                }

                pb.step();
            }
        }
    }

    public Individual createInstanceIfAbsent(OntClass ontClass, String uri) {
        instanceHashMap.putIfAbsent(uri, ontClass.createIndividual(uri));
        return instanceHashMap.get(uri);
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
