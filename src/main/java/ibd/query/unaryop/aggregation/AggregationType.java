/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.unaryop.aggregation;

import entities.Column;
import ibd.query.ColumnDescriptor;
import java.util.ArrayList;
import java.util.List;
import static operations.unary.Aggregation.PREFIXES;
import utils.Utils;

/**
 *
 * @author ferna
 */
public class AggregationType {

    //defines the five types of aggregation
    public final static int MAX = 1;
    public final static int MIN = 2;
    public final static int COUNT = 3;
    public final static int AVG = 4;
    public final static int SUM = 5;
    public final static int FIRST = 6;
    public final static int LAST = 7;
    public final static int COUNT_ALL = 8;
    public final static int COUNT_NULL = 9;

    public ColumnDescriptor aggregateColumn;

    public String aggregateColumnName;

    //the aggregation type
    public int type;

    public AggregationType(String col, int type) throws Exception {
        this.type = type;
        aggregateColumn = new ColumnDescriptor(col);
        aggregateColumnName = getStringType() + "_" + aggregateColumn.getColumnName();
    }

    public AggregationType(String table, String col, int type) {
        this.type = type;
        aggregateColumn = new ColumnDescriptor(table, col);
        aggregateColumnName = getStringType() + "_" + aggregateColumn.getColumnName();
    }

    public int getAgregationType() {
        return type;
    }

    private String getStringType() {
        return switch (type) {
            case AVG ->
                "AVG";
            case MIN ->
                "MIN";
            case MAX ->
                "MAX";
            case COUNT ->
                "COUNT";
            case SUM ->
                "SUM";
            case FIRST ->
                "FIRST";
            case LAST ->
                "LAST";
            case COUNT_ALL ->
                "COUNT_ALL";
            case COUNT_NULL ->
                "COUNT_NULL";
            
            default ->
                "";
        };
    }

    public static List<AggregationType> getAggregationTypes(String fixedArgument) {
        List<AggregationType> aggregations = new ArrayList<>();
        String aggregateCol = null;
        int aggregateType = -1;

        String sourceName = Column.removeName(fixedArgument).substring(Utils.getFirstMatchingPrefixIgnoreCase(fixedArgument, PREFIXES).length());
        String columnName = Column.removeSource(fixedArgument);
        if (Utils.startsWithIgnoreCase(fixedArgument, "MAX:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.MAX));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "MIN:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.MIN));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "AVG:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.AVG));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "SUM:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.SUM));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "FIRST:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.FIRST));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "LAST:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.LAST));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "COUNT:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.COUNT));
        }
        else if (Utils.startsWithIgnoreCase(fixedArgument, "COUNT_ALL:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.COUNT_ALL));
        }
        else if (Utils.startsWithIgnoreCase(fixedArgument, "COUNT_NULL:")) {
            aggregations.add(new AggregationType(sourceName, columnName, AggregationType.COUNT_NULL));
        }
        return aggregations;
    }
    
    public static List<AggregationType> getAggregationTypes1(List<String> arguments){
    List<String> fixedArguments = new ArrayList<>();

        for (String argument : arguments.subList(1, arguments.size())) {
//            String fixedArgument = argument.substring(0, Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length())
//                + entities.Column.composeSourceAndName(parentCell.getSourceNameByColumnName(argument.substring(Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length())), argument.substring(Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length())
//            );

            fixedArguments.add(argument);
        }


        List<AggregationType> aggregations = new ArrayList<>();

        for (String argument : fixedArguments) {
            String column = argument.substring(Utils.getFirstMatchingPrefixIgnoreCase(argument, PREFIXES).length());
            String sourceName = entities.Column.removeName(column);
            String columnName = entities.Column.removeSource(column);

            if (Utils.startsWithIgnoreCase(argument, "MAX:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.MAX));
            } else if (Utils.startsWithIgnoreCase(argument, "MIN:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.MIN));
            } else if (Utils.startsWithIgnoreCase(argument, "AVG:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.AVG));
            } else if (Utils.startsWithIgnoreCase(argument, "COUNT:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.COUNT));
            } else if (Utils.startsWithIgnoreCase(argument, "FIRST:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.FIRST));
            } else if (Utils.startsWithIgnoreCase(argument, "LAST:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.LAST));
            } 
            else if (Utils.startsWithIgnoreCase(argument, "SUM:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.SUM));
            }
            else if (Utils.startsWithIgnoreCase(argument, "COUNT_ALL:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.COUNT_ALL));
            }
            else if (Utils.startsWithIgnoreCase(argument, "COUNT_NULL:")) {
                aggregations.add(new AggregationType(sourceName, columnName, AggregationType.COUNT_NULL));
            }
        }
        return aggregations;
    }
    

    @Override
    public String toString() {
        return getStringType() + "_" + aggregateColumn;
    }
}
