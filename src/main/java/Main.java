import Utils.CSVHelper;
import Utils.Impl.CSVHelperImpl;
import Utils.Impl.RDFHelperImpl;
import Utils.RDFHelper;

public class Main {

    public static void main(String[] args) {

        CSVHelper csvHelper = new CSVHelperImpl();
        RDFHelper rdfHelper = new RDFHelperImpl();

        // Read both CSV Files and parse into composite model, convert to RDF, and save to resources folder.
        rdfHelper.createModelFromCrimeDOMap(csvHelper.getCrimeInfo().values().stream().toList());
    }
}