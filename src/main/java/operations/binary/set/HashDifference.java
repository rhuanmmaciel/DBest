package operations.binary.set;

import java.util.List;


public class HashDifference extends SetOperators {

    @Override
    public ibd.query.Operation createSetOperator(ibd.query.Operation operator1, ibd.query.Operation operator2, List<String> columns1, List<String> columns2, String alias) {
        try { 
            //return new IntersectionOperator(operator1, operator2, columns1, columns2);
            return new ibd.query.binaryop.set.HashDifference(operator1, operator2);
        } catch (Exception ex) {
        }
        return null;
    }
}
