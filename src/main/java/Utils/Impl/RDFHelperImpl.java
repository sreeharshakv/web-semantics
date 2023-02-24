package Utils.Impl;

import Constants.AppConstants;
import Model.CrimeDO;
import Utils.RDFHelper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RDFHelperImpl implements RDFHelper {

    enum PropertyNames {
        hasDRNo, reportedOn, occurredOn, inArea, areaCode, areaDescription, crime, crimeCode, primaryCrimeDescription, premise, premiseCode, premiseDescription, usingWeapon, weaponCode, weaponDescription, currentStatus, statusCode, statusDescription, atLocation, location, crossStreet, housingPriceIndex
    }

    String crimeDS = "https://data.lacity.org#";
    String housingDS = "https://www.zillow.com/research/data#";
    private static final Path ROOT_PATH = FileSystems.getDefault().getPath("").toAbsolutePath();

    public void createModelFromCrimeDOMap(List<CrimeDO> crimeDOList) {

        Model model = ModelFactory.createDefaultModel();
        initModel(model);
        populateModel(model, crimeDOList);
        exportModelToRDFFile(model);
    }

    private void initModel(Model model) {
        model.setNsPrefix("cds", crimeDS);
        model.setNsPrefix("hds", housingDS);

    }

    private void populateModel(Model model, List<CrimeDO> crimeDOList) {
        // create Resources and corresponding properties
        AtomicInteger i = new AtomicInteger(1);
        int l = crimeDOList.size();
        crimeDOList.forEach( value -> {
            System.out.println(i + "/" + l + " records parsed!");
            i.getAndIncrement();
            Resource crimeRecord = model.createResource(crimeDS + value.getDrNo());
            Property hasDRNo = model.createProperty(crimeDS + PropertyNames.hasDRNo);
            model.add(model.createStatement(crimeRecord, hasDRNo, model.createLiteral(value.getDrNo())));
            Property reportedOn = model.createProperty(crimeDS + PropertyNames.reportedOn);
            model.add(model.createStatement(crimeRecord, reportedOn, model.createLiteral(value.getDateReported().toString())));
            Property occurredOn = model.createProperty(crimeDS + PropertyNames.occurredOn);
            model.add(model.createStatement(crimeRecord, occurredOn, model.createLiteral(value.getDateOccurred().toString())));
            Property inArea = model.createProperty(crimeDS + PropertyNames.inArea);
            Property areaCode = model.createProperty(crimeDS + PropertyNames.areaCode);
            Property areaDescription = model.createProperty(crimeDS + PropertyNames.areaDescription);
            Resource areaResource = model.createResource(crimeDS + value.getArea())
                    .addProperty(areaCode, value.getArea())
                    .addProperty(areaDescription, value.getAreaName());
            model.add(model.createStatement(crimeRecord, inArea, areaResource));
            Property crime = model.createProperty(crimeDS + PropertyNames.crime);
            Property crimeCode = model.createProperty(crimeDS + PropertyNames.crimeCode);
            Property primaryCrimeDescription = model.createProperty(crimeDS + PropertyNames.primaryCrimeDescription);
            Resource crimeResource = model.createResource(crimeDS + value.getCrimeCodes()[0])
                    .addProperty(crimeCode, value.getCrimeCodes()[0])
                    .addProperty(primaryCrimeDescription, value.getPrimaryCrimeDesc());
            model.add(model.createStatement(crimeRecord, crime, crimeResource));
            Property premise = model.createProperty(crimeDS + PropertyNames.premise);
            Property premiseCode = model.createProperty(crimeDS + PropertyNames.premiseCode);
            Property premiseDescription = model.createProperty(crimeDS + PropertyNames.premiseDescription);
            Resource premiseResource = model.createResource(crimeDS + value.getPremise())
                    .addProperty(premiseCode, value.getPremise())
                    .addProperty(premiseDescription, value.getPremiseDesc());
            model.add(model.createStatement(crimeRecord, premise, premiseResource));
            Property usingWeapon = model.createProperty(crimeDS + PropertyNames.usingWeapon);
            Property weaponCode = model.createProperty(crimeDS + PropertyNames.weaponCode);
            Property weaponDescription = model.createProperty(crimeDS + PropertyNames.weaponDescription);
            Resource weaponResource = model.createResource(crimeDS + value.getWeapon())
                    .addProperty(weaponCode, value.getWeapon())
                    .addProperty(weaponDescription, value.getWeaponDesc());
            model.add(model.createStatement(crimeRecord, usingWeapon, weaponResource));
            Property currentStatus = model.createProperty(crimeDS + PropertyNames.currentStatus);
            Property statusCode = model.createProperty(crimeDS + PropertyNames.statusCode);
            Property statusDescription = model.createProperty(crimeDS + PropertyNames.statusDescription);
            Resource statusResource = model.createResource(crimeDS + value.getStatus())
                    .addProperty(statusCode, value.getStatus())
                    .addProperty(statusDescription, value.getStatusDesc());
            model.add(model.createStatement(crimeRecord, currentStatus, statusResource));
            Property atLocation = model.createProperty(crimeDS + PropertyNames.atLocation);
            Property location = model.createProperty(crimeDS + PropertyNames.location);
            Property crossStreet = model.createProperty(crimeDS + PropertyNames.crossStreet);
            Resource locResource = model.createResource()
                    .addProperty(location, value.getLocation())
                    .addProperty(crossStreet, value.getCrossStreet());
            model.add(model.createStatement(crimeRecord, atLocation, locResource));
            Property housingPriceIndex = model.createProperty(housingDS + PropertyNames.housingPriceIndex);
            model.add(model.createStatement(crimeRecord, housingPriceIndex, model.createLiteral(value.getHPI().toString())));
        });
    }

    public void exportModelToRDFFile(Model model) {
        String filePath = ROOT_PATH.resolve("deliverables").resolve(AppConstants.OUTPUT_RDF_FILE_NAME).toString();
        try (OutputStream rdfStream = new FileOutputStream(filePath)) {
            model.write(rdfStream, "RDF/XML");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Export process complete.");
        }

    }
}
