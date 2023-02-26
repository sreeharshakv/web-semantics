package Utils.Impl;

import Constants.AppConstants;
import Model.CrimeDO;
import Utils.RDFHelper;
import me.tongfei.progressbar.ProgressBar;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
        OntClass areaInfo = model.createClass(ClassNames.area.getUri());
        OntClass crimeInfo = model.createClass(ClassNames.crime.getUri());
        OntClass premiseInfo = model.createClass(ClassNames.premise.getUri());
        OntClass weaponInfo = model.createClass(ClassNames.weapon.getUri());
        OntClass statusInfo = model.createClass(ClassNames.status.getUri());

        // create Properties
        ObjectProperty hasDRNo = model.createObjectProperty(PropertyNames.hasDRNo.getUri());
        ObjectProperty reportedOn = model.createObjectProperty(PropertyNames.reportedOn.getUri());
        ObjectProperty occurredOn = model.createObjectProperty(PropertyNames.occurredOn.getUri());
        ObjectProperty inArea = model.createObjectProperty(PropertyNames.inArea.getUri());
        ObjectProperty areaCode = model.createObjectProperty(PropertyNames.areaCode.getUri());
        ObjectProperty areaDescription = model.createObjectProperty(PropertyNames.areaDescription.getUri());
        ObjectProperty crime = model.createObjectProperty(PropertyNames.crime.getUri());
        ObjectProperty crimeCode = model.createObjectProperty(PropertyNames.crimeCode.getUri());
        ObjectProperty primaryCrimeDescription = model.createObjectProperty(PropertyNames.primaryCrimeDescription.getUri());
        ObjectProperty premise = model.createObjectProperty(PropertyNames.premise.getUri());
        ObjectProperty premiseCode = model.createObjectProperty(PropertyNames.premiseCode.getUri());
        ObjectProperty premiseDescription = model.createObjectProperty(PropertyNames.premiseDescription.getUri());
        ObjectProperty usingWeapon = model.createObjectProperty(PropertyNames.usingWeapon.getUri());
        ObjectProperty weaponCode = model.createObjectProperty(PropertyNames.weaponCode.getUri());
        ObjectProperty weaponDescription = model.createObjectProperty(PropertyNames.weaponDescription.getUri());
        ObjectProperty currentStatus = model.createObjectProperty(PropertyNames.currentStatus.getUri());
        ObjectProperty statusCode = model.createObjectProperty(PropertyNames.statusCode.getUri());
        ObjectProperty statusDescription = model.createObjectProperty(PropertyNames.statusDescription.getUri());
        ObjectProperty atLocation = model.createObjectProperty(PropertyNames.atLocation.getUri());
        ObjectProperty location = model.createObjectProperty(PropertyNames.location.getUri());
        ObjectProperty crossStreet = model.createObjectProperty(PropertyNames.crossStreet.getUri());
        ObjectProperty hadHousePriceIndex = model.createObjectProperty(PropertyNames.hadHousingPriceIndex.getUri());

        int l = crimeDOList.size();
        try (ProgressBar pb = new ProgressBar("Parsing records to model", l)) {
            for(CrimeDO value: crimeDOList) {
                Individual crimeRecordInstance = crimeRecord.createIndividual(crimeDS + value.getDrNo());
                Individual areaInstance = areaInfo.createIndividual(crimeDS + value.getArea());
                Individual crimeInstance = crimeInfo.createIndividual(crimeDS + value.getCrimeCodes()[0]);
                Individual premiseInstance = premiseInfo.createIndividual(crimeDS + value.getPremise());
                Individual weaponInstance = weaponInfo.createIndividual(crimeDS + value.getWeapon());
                Individual statusInstance = statusInfo.createIndividual(crimeDS + value.getStatus());
                Individual locationInstance = crimeRecord.createIndividual(crimeDS + value.getLocation().replaceAll(" ", ""));

                model.add(crimeRecordInstance, hasDRNo, value.getDrNo());
                model.add(crimeRecordInstance, reportedOn, model.createTypedLiteral(value.getDateReported()));
                model.add(crimeRecordInstance, occurredOn, model.createTypedLiteral(value.getDateOccurred()));
                model.add(crimeRecordInstance, hadHousePriceIndex, model.createTypedLiteral(value.getHPI()));

                model.add(areaInstance, areaCode, value.getArea());
                model.add(areaInstance, areaDescription, value.getAreaName());

                model.add(crimeInstance, crimeCode, value.getCrimeCodes()[0]);
                model.add(crimeInstance, primaryCrimeDescription, value.getPrimaryCrimeDesc());

                model.add(premiseInstance, premiseCode, value.getPremise());
                model.add(premiseInstance, premiseDescription, value.getPremiseDesc());

                model.add(weaponInstance, weaponCode, value.getWeapon());
                model.add(weaponInstance, weaponDescription, value.getWeaponDesc());

                model.add(statusInstance, statusCode, value.getStatus());
                model.add(statusInstance, statusDescription, value.getStatusDesc());

                model.add(locationInstance, location, value.getLocation());
                model.add(locationInstance, crossStreet, value.getCrossStreet());

                model.add(crimeRecordInstance, inArea, areaInstance);
                model.add(crimeRecordInstance, crime, crimeInstance);
                model.add(crimeRecordInstance, premise, premiseInstance);
                model.add(crimeRecordInstance, usingWeapon, weaponInstance);
                model.add(crimeRecordInstance, currentStatus, statusInstance);
                model.add(crimeRecordInstance, atLocation, locationInstance);

                pb.step();
            }
        }
    }

    public void exportModelToRDFFile() {
        System.out.println("Writing model to file. Don't touch the file until confirmation");
        String filePath = ROOT_PATH.resolve("deliverables").resolve(AppConstants.OUTPUT_FILE_NAME).toString();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath))) {
            model.write(out);
            System.out.println("Export process complete. Please find the output file in the deliverables folder.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
