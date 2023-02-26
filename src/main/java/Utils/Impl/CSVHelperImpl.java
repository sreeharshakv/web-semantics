package Utils.Impl;

import Constants.AppConstants;
import Model.CrimeDO;
import Model.HousingPriceDO;
import Utils.CSVHelper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CSVHelperImpl implements CSVHelper {

    public HashMap<String, HousingPriceDO> getHousingInfo() {
        System.out.println("Reading Housing prices in LA City since 2010..");
        HashMap<String, HousingPriceDO> housingPriceDOMap = new HashMap<>();
        try (CSVReader reader = new CSVReaderBuilder((
                new InputStreamReader(
                        Objects.requireNonNull(HousingPriceDO.class
                                .getClassLoader()
                                .getResourceAsStream(AppConstants.HOUSING_DATA_FILE_NAME)))))
                .withSkipLines(1)
                .build()) {
            List<String[]> data = reader.readAll();
            for (String[] record : data) {
                HousingPriceDO housingPriceDO = new HousingPriceDO(record);
                housingPriceDOMap.put(housingPriceDO.getRegionName(), housingPriceDO);
            }
        } catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }

        return housingPriceDOMap;
    }

    public HashMap<String, CrimeDO> getCrimeInfo() {
        HashMap<String, HousingPriceDO> housingPriceDOMap = getHousingInfo();
        System.out.println("Reading Crime records of LA City since 2010..");
        HashMap<String, CrimeDO> crimeDOMap = new HashMap<>();
        try (CSVReader reader = new CSVReaderBuilder((
                new InputStreamReader(
                        Objects.requireNonNull(CrimeDO.class
                                .getClassLoader()
                                .getResourceAsStream(AppConstants.CRIME_DATA_FILE_NAME)))))
                .withSkipLines(1)
                .build()) {
            List<String[]> data = reader.readAll();
            for (String[] record : data) {
                if (!crimeDOMap.containsKey(record[0]) && !record[0].equalsIgnoreCase("DR_NO")) {
                    CrimeDO crimeDO = new CrimeDO(record);
                    if (addHousingInfoToCrimeDO(crimeDO, housingPriceDOMap.get(crimeDO.getAreaName()))) {
                        crimeDOMap.put(crimeDO.getDrNo(), crimeDO);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return crimeDOMap;
    }

    private boolean addHousingInfoToCrimeDO(CrimeDO crimeDO, HousingPriceDO housingPriceDO) {
        if (housingPriceDO != null) {
            Double hpi = housingPriceDO.getHPIonDate(crimeDO.getDateOccurred());
            if (hpi != null) {
                crimeDO.setHPI(hpi);
                return true;
            }
        }
        return false;
    }
}
