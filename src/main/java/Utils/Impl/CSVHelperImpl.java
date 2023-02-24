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
        HashMap<String, HousingPriceDO> housingPriceDOList = new HashMap<>();
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
                housingPriceDOList.put(housingPriceDO.getRegionName(), housingPriceDO);
            }
        } catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }

        return housingPriceDOList;
    }

    public HashMap<String, CrimeDO> getCrimeInfo() {
        HashMap<String, HousingPriceDO> housingPriceDOList = getHousingInfo();
        HashMap<String, CrimeDO> crimeDOList = new HashMap<>();
        try (CSVReader reader = new CSVReaderBuilder((
                new InputStreamReader(
                        Objects.requireNonNull(CrimeDO.class
                                .getClassLoader()
                                .getResourceAsStream(AppConstants.CRIME_DATA_FILE_NAME)))))
                .withSkipLines(1)
                .build()) {
            List<String[]> data = reader.readAll();
            for (String[] record : data) {
                if (!crimeDOList.containsKey(record[0]) && !record[0].equalsIgnoreCase("DR_NO")) {
                    CrimeDO crimeDO = new CrimeDO(record);
                    addHousingInfoToCrimeDO(crimeDO, housingPriceDOList.get(crimeDO.getAreaName()));
                    crimeDOList.put(crimeDO.getDrNo(), crimeDO);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return crimeDOList;
    }

    private void addHousingInfoToCrimeDO(CrimeDO crimeDO, HousingPriceDO housingPriceDO) {
        if (housingPriceDO == null)
            return;
        Double hpi = housingPriceDO.getHPIonDate(crimeDO.getDateOccurred());
        if (hpi != null) {
            crimeDO.setHPI(hpi);
        } else System.out.println("HPI not found for " + crimeDO.getDrNo());
    }
}
