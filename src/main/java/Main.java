import Model.HousingPriceDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        // Read CSV File and parse into model
        String fileName = "Zillow_Neighbourhood_2010_to_Present.csv";
        List<HousingPriceDO> housingPriceDOList = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder((
                new InputStreamReader(
                        Objects.requireNonNull(HousingPriceDO.class
                                .getClassLoader()
                                .getResourceAsStream(fileName)))))
                .withSkipLines(1)
                .build()) {
            List<String[]> data = reader.readAll();
            for (String[] record : data) {
                HousingPriceDO housingPriceDO = new HousingPriceDO(record);
                housingPriceDOList.add(housingPriceDO);
            }
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writeValueAsString(housingPriceDOList));
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }

        // Apache Jena to serialise to rdf
        Model model = ModelFactory.createDefaultModel();

        // save rdf to deliverables folder
    }
}