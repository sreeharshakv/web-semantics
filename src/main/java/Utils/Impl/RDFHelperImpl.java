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
    static HashMap<Integer, ClassNames> crimeMap;
    static HashMap<Integer, ClassNames> weaponMap;

    enum ClassNames {
        CrimeRecord, Area, Crime, Premise, Weapon, Status, Default,

        //crime subclasses
        PropertyCrime, PersonalCrime, OtherCrimes, Homicide, Rape, Robbery, Assault, AggravatedAssault, SimpleAssault, DomesticViolence, Burglary, MVT, BTFV, Theft,

        // weapon subclasses
        CombatWeapons, HandToHand, Ranged, Explosives, Firearms, Automatic, SemiAutomatic, Manual, SpecialWeapons;

        String getUri() {
            return crimeDS + this.name();
        }
    }

    enum PropertyNames {
        hasDRNo, reportedOn, occurredOn, inArea, areaCode, areaDescription, crimeOccurred, crimeCode, crimeDescription, hasPremise, premiseCode,
        premiseDescription, usingWeapon, weaponCode, weaponDescription, hasCurrentStatus, statusCode, statusDescription, atLocation, atCrossStreet, hadHousingPriceIndex;

        String getUri() {
            if (this.name().equalsIgnoreCase("hasHousingPriceIndex")) {
                return housingDS + this.name();
            }
            return crimeDS + this.name();
        }
    }

    static {
        crimeMap = new HashMap<>();
        List.of(110, 113).forEach(val -> crimeMap.put(val, ClassNames.Homicide));
        List.of(121, 122, 815, 820, 821).forEach(val -> crimeMap.put(val, ClassNames.Rape));
        List.of(210, 220).forEach(val -> crimeMap.put(val, ClassNames.Robbery));
        List.of(111, 230, 231, 235, 236, 435, 436).forEach(val -> crimeMap.put(val, ClassNames.AggravatedAssault));
        List.of(234, 622, 623, 624, 625).forEach(val -> crimeMap.put(val, ClassNames.SimpleAssault));
        List.of(236, 250, 251, 761, 626, 627, 647, 763, 928, 930, 624).forEach(val -> crimeMap.put(val, ClassNames.DomesticViolence));
        List.of(220, 310, 320).forEach(val -> crimeMap.put(val, ClassNames.Burglary));
        List.of(510, 520, 433, 331).forEach(val -> crimeMap.put(val, ClassNames.MVT));
        List.of(330, 331, 410, 420, 421).forEach(val -> crimeMap.put(val, ClassNames.BTFV));
        List.of(510,520, 522, 430,349, 350, 351, 352, 446, 353, 354, 450, 451, 452, 453, 341, 343, 345, 440, 441, 442, 443, 444, 445, 470, 471, 472, 473, 474, 475, 480, 485, 487, 491, 347).forEach(val -> crimeMap.put(val, ClassNames.Theft));
        List.of(237, 431, 432, 434, 437, 438, 439, 486, 521, 926).forEach(val -> crimeMap.put(val, ClassNames.OtherCrimes));

        weaponMap = new HashMap<>();
        List.of( 204, 207, 512, 308, 200, 201, 202, 203, 205, 206,207, 208, 209,210, 211, 212, 213, 214, 215, 216,217, 218, 219, 220, 221, 223, 300, 301, 302, 303, 304, 305, 308, 309, 312, 400).forEach(val -> crimeMap.put(val, ClassNames.HandToHand));
        List.of(306, 310, 502, 506).forEach(val -> crimeMap.put(val, ClassNames.Ranged));
        List.of(501, 505).forEach(val -> crimeMap.put(val, ClassNames.Explosives));
        List.of(108, 115).forEach(val -> crimeMap.put(val, ClassNames.Automatic));
        List.of(109, 110, 117, 118, 119, 120, 121, 122, 123, 124).forEach(val -> crimeMap.put(val, ClassNames.SemiAutomatic));
        List.of(101, 102, 103, 104, 105, 111, 113, 114, 116).forEach(val -> crimeMap.put(val, ClassNames.Manual));
        List.of(112, 125,307, 504, 503, 500 , 106, 107, 511).forEach(val -> crimeMap.put(val, ClassNames.SpecialWeapons));

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

        OntClass propertyCrime = model.createClass(ClassNames.PropertyCrime.getUri());
        propertyCrime.addComment("Crimes against Property", "EN");
        propertyCrime.addSuperClass(crimeInfo);

        OntClass burglary = model.createClass(ClassNames.Burglary.getUri());
        burglary.addComment("entry into property illegally with intent to commit a crime, especially theft.", "EN");
        burglary.addSuperClass(propertyCrime);

        OntClass mvt = model.createClass(ClassNames.MVT.getUri());
        mvt.addComment("Motor Vehicle Theft", "EN");
        mvt.addSuperClass(propertyCrime);

        OntClass btfv = model.createClass(ClassNames.BTFV.getUri());
        btfv.addComment("Burglary or theft from motor vehicle (breaking into a vehicle)", "EN");
        btfv.addSuperClass(propertyCrime);

        OntClass other = model.createClass(ClassNames.OtherCrimes.getUri());
        other.addComment("Other property crimes", "EN");
        other.addSuperClass(propertyCrime);

        propertyCrime.addSubClass(burglary);
        propertyCrime.addSubClass(mvt);
        propertyCrime.addSubClass(btfv);
        propertyCrime.addSubClass(other);

        OntClass personalCrime = model.createClass(ClassNames.PersonalCrime.getUri());
        personalCrime.addComment("Crimes against People", "EN");
        personalCrime.addSuperClass(crimeInfo);

        OntClass homicide = model.createClass(ClassNames.Homicide.getUri());
        homicide.addComment("The killing of one human being by another", "EN");
        homicide.addSuperClass(personalCrime);

        OntClass rape = model.createClass(ClassNames.Rape.getUri());
        rape.addSuperClass(personalCrime);

        OntClass robbery = model.createClass(ClassNames.Robbery.getUri());
        robbery.addSuperClass(personalCrime);

        OntClass assault = model.createClass(ClassNames.Assault.getUri());
        assault.addComment("Simple and aggravated Assaults, including domestic violence", "EN");
        assault.addSuperClass(personalCrime);

        OntClass theft = model.createClass(ClassNames.Theft.getUri());
        theft.addSuperClass(personalCrime);

        OntClass simpleAssault = model.createClass(ClassNames.SimpleAssault.getUri());
        simpleAssault.addSuperClass(assault);

        OntClass aggAssault = model.createClass(ClassNames.AggravatedAssault.getUri());
        aggAssault.addComment("Any crime that involves the attempt to murder, rob, kill, rape, or assault with a deadly or dangerous weapon", "EN");
        aggAssault.addSuperClass(assault);

        OntClass domesticViolence = model.createClass(ClassNames.DomesticViolence.getUri());
        domesticViolence.addSuperClass(assault);

        assault.addSubClass(simpleAssault);
        assault.addSubClass(aggAssault);
        assault.addSubClass(domesticViolence);

        personalCrime.addSubClass(homicide);
        personalCrime.addSubClass(theft);
        personalCrime.addSubClass(rape);
        personalCrime.addSubClass(robbery);
        personalCrime.addSubClass(assault);

        crimeInfo.addSubClass(propertyCrime);
        crimeInfo.addSubClass(personalCrime);

        OntClass premiseInfo = model.createClass(ClassNames.Premise.getUri());
        premiseInfo.addComment("The type of structure, vehicle, or location where the crime took place", "EN");

        OntClass weaponInfo = model.createClass(ClassNames.Weapon.getUri());
        weaponInfo.addComment("The type of weapon used in the crime", "EN");

        OntClass combat = model.createClass(ClassNames.CombatWeapons.getUri());
        combat.addComment("Combat Weapons, both hand to hand and ranged weapons", "EN");
        combat.addSuperClass(weaponInfo);

        OntClass handToHand = model.createClass(ClassNames.HandToHand.getUri());
        handToHand.addComment("A melee weapon, hand weapon or close combat weapon is any handheld weapon used in hand-to-hand combat", "EN");
        handToHand.addSuperClass(combat);

        OntClass ranged = model.createClass(ClassNames.Ranged.getUri());
        ranged.addComment("Any weapon that can engage targets beyond hand-to-hand distance.", "EN");
        ranged.addSuperClass(combat);

        OntClass firearms = model.createClass(ClassNames.Firearms.getUri());
        firearms.addComment("Ranged weapon that inflicts damage on targets by launching one or more projectiles", "EN");
        firearms.addSuperClass(ranged);

        OntClass automatic = model.createClass(ClassNames.Automatic.getUri());
        automatic.addComment("An auto-loading firearm that continuously chambers and fires rounds when the trigger mechanism is actuated", "EN");
        automatic.addSuperClass(firearms);

        OntClass semiAutomatic = model.createClass(ClassNames.SemiAutomatic.getUri());
        semiAutomatic.addComment("A firearm where the shooter pulls the trigger, one bullet is fired and a new bullet is automatically loaded", "EN");
        semiAutomatic.addSuperClass(firearms);

        OntClass manual = model.createClass(ClassNames.Manual.getUri());
        manual.addComment("The type of weapon used in the crime", "EN");
        manual.addSuperClass(firearms);

        firearms.addSubClass(automatic);
        firearms.addSubClass(semiAutomatic);
        firearms.addSubClass(manual);

        ranged.addSubClass(firearms);

        combat.addSubClass(handToHand);
        combat.addSubClass(ranged);

        OntClass explosives = model.createClass(ClassNames.Explosives.getUri());
        explosives.addSuperClass(weaponInfo);

        OntClass specialWeapons = model.createClass(ClassNames.SpecialWeapons.getUri());
        specialWeapons.addComment("Special Category of weapons, such as vehicle, poison, etc.", "EN");
        specialWeapons.addSuperClass(weaponInfo);

        weaponInfo.addSubClass(combat);
        weaponInfo.addSubClass(explosives);
        weaponInfo.addSubClass(specialWeapons);

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

        ObjectProperty crimeDescription = model.createObjectProperty(PropertyNames.crimeDescription.getUri());
        crimeDescription.addComment("Defines the Crime Code provided", "EN");
        crimeDescription.addDomain(crimeInfo);
        crimeDescription.addRange(XSD.xstring);

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

        ObjectProperty location = model.createObjectProperty(PropertyNames.atLocation.getUri());
        location.addComment("Street address of crime incident rounded to the nearest hundred block to maintain anonymity.", "EN");
        location.addDomain(crimeRecord);
        location.addRange(XSD.xstring);

        ObjectProperty crossStreet = model.createObjectProperty(PropertyNames.atCrossStreet.getUri());
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

                if (value.getCrimeCodes().get(0) != 0  && !value.getCrimeDesc().equals("")) {
                    if (!instanceHashMap.containsKey(crimeDS + value.getCrimeCodes().get(0))) {
                        switch (crimeMap.getOrDefault(value.getCrimeCodes().get(0), ClassNames.Default)) {
                            case Homicide ->
                                    crimeInstance = createInstanceIfAbsent(homicide, crimeDS + value.getCrimeCodes().get(0));
                            case Rape ->
                                    crimeInstance = createInstanceIfAbsent(rape, crimeDS + value.getCrimeCodes().get(0));
                            case Robbery ->
                                    crimeInstance = createInstanceIfAbsent(robbery, crimeDS + value.getCrimeCodes().get(0));
                            case AggravatedAssault ->
                                    crimeInstance = createInstanceIfAbsent(aggAssault, crimeDS + value.getCrimeCodes().get(0));
                            case SimpleAssault ->
                                    crimeInstance = createInstanceIfAbsent(simpleAssault, crimeDS + value.getCrimeCodes().get(0));
                            case DomesticViolence ->
                                    crimeInstance = createInstanceIfAbsent(domesticViolence, crimeDS + value.getCrimeCodes().get(0));
                            case Burglary ->
                                    crimeInstance = createInstanceIfAbsent(burglary, crimeDS + value.getCrimeCodes().get(0));
                            case MVT ->
                                    crimeInstance = createInstanceIfAbsent(mvt, crimeDS + value.getCrimeCodes().get(0));
                            case BTFV ->
                                    crimeInstance = createInstanceIfAbsent(btfv, crimeDS + value.getCrimeCodes().get(0));
                            case Theft ->
                                    crimeInstance = createInstanceIfAbsent(theft, crimeDS + value.getCrimeCodes().get(0));
                            default ->
                                    crimeInstance = createInstanceIfAbsent(crimeInfo, crimeDS + value.getCrimeCodes().get(0));
                        }
                        model.add(crimeInstance, crimeCode, model.createTypedLiteral(value.getCrimeCodes().get(0)));
                        model.add(crimeInstance, crimeDescription, value.getCrimeDesc());
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
                        switch (weaponMap.getOrDefault(value.getWeapon(), ClassNames.Default)) {
                            case HandToHand ->
                                    weaponInstance = createInstanceIfAbsent(handToHand, crimeDS + value.getWeapon());
                            case Ranged ->
                                    weaponInstance = createInstanceIfAbsent(ranged, crimeDS + value.getWeapon());
                            case Explosives ->
                                    weaponInstance = createInstanceIfAbsent(explosives, crimeDS + value.getWeapon());
                            case Automatic ->
                                    weaponInstance = createInstanceIfAbsent(automatic, crimeDS + value.getWeapon());
                            case SemiAutomatic ->
                                    weaponInstance = createInstanceIfAbsent(semiAutomatic, crimeDS + value.getWeapon());
                            case Manual ->
                                    weaponInstance = createInstanceIfAbsent(manual, crimeDS + value.getWeapon());
                            case SpecialWeapons ->
                                    weaponInstance = createInstanceIfAbsent(specialWeapons, crimeDS + value.getWeapon());
                            default ->
                                    weaponInstance = createInstanceIfAbsent(weaponInfo, crimeDS + value.getWeapon());
                        }
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
