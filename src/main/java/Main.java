import Model.CrimeDO;
import Utils.CSVHelper;
import Utils.Impl.CSVHelperImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        CSVHelper csvHelper = new CSVHelperImpl();

        // Read both CSV Files and parse into composite model to convert into RDF
        HashMap<String, CrimeDO> crimeDOMap = csvHelper.getCrimeInfo();

        // Apache Jena to serialise to rdf
        Model model = ModelFactory.createDefaultModel();
        String csvNs = "http://example.org/csv/";
        String rdfNs = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        model.setNsPrefix("csv", csvNs);
        model.setNsPrefix("rdf", rdfNs);

        // save rdf to deliverables folder
    }
}