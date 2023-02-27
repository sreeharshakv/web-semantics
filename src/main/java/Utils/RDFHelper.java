package Utils;
import Model.CrimeDO;

import java.util.List;

public interface RDFHelper {

    void createModelFromCrimeDOMap(List<CrimeDO> crimeDOList);
}
