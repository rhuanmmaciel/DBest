package threads;

import database.TuplesExtractor;

import org.apache.poi.ss.formula.functions.T;

import sgbd.query.Operator;

import java.util.List;
import java.util.Map;

public class ReadTuplesRunnable implements Runnable{

    private final TuplesExtractor tuplesExtractor;

    public ReadTuplesRunnable(Operator operator, boolean sourceAndName, int amount, TuplesExtractor.Type type){

        this.tuplesExtractor = new TuplesExtractor(operator, sourceAndName, amount, type);

    }

    public ReadTuplesRunnable(Operator operator, boolean sourceAndName, TuplesExtractor.Type type){

        this.tuplesExtractor = new TuplesExtractor(operator, sourceAndName, type);

    }

    @Override
    public void run() {

        switch (tuplesExtractor.type){
            case ALL_ROWS_IN_A_LIST -> allRowsInAList = tuplesExtractor.getAllRowsList();
            case ALL_ROWS_IN_A_MAP -> allRowsInAMap = tuplesExtractor.getAllRowsMap();
            case ROWS_IN_A_LIST -> rowsInAList = tuplesExtractor.getRows();
            case ROW -> row = tuplesExtractor.getRow();
            case ROWS_LEFT_WITHOUT_CLOSING -> rowsInAList = tuplesExtractor.getAllRowsLeftWithoutClosingOperatorList();
        };

    }
    Map<Integer, Map<String, String>> allRowsInAMap;
    List<Map<String, String>> allRowsInAList;
    List<Map<String, String>> rowsInAList;
    Map<String, String> row;
    public Map<Integer, Map<String, String>>  getAllRowsMap(){
        return allRowsInAMap;
    }

    public List<Map<String, String>> getAllRowsList() {
        return allRowsInAList;
    }

    public List<Map<String, String>> getRows(){
        return rowsInAList;
    }

    public Map<String, String> getRow() {
        return row;
    }

}
