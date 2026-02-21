/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.lookup;

import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.ArrayList;
import java.util.List;

/**
 * defines a composite filter whose component filters are connected by a connector (AND or OR).
 *
 * @author Sergio
 */
public class CompositeLookupFilter implements LookupFilter {

    public static final int AND = 0;
    public static final int OR = 1; 

    List<LookupFilter> filters = new ArrayList();

    int boolean_connector;

    /**
     *
     * @param connector A boolean connector (AND or OR)
     */
    public CompositeLookupFilter(int connector) {
        this.boolean_connector = connector;
    }

    /**
     * Adds a filter to the composite filter
     * @param filter the filter to be added
     */
    public void addFilter(LookupFilter filter) {
        filters.add(filter);
    }

    /**
     *
     * @return the list of filters
     */
    public List<LookupFilter> getFilters() {
        //needs refactoring to hide the list
        return filters;
    }
    
    public int getBooleanConnector(){
        return boolean_connector;
    }

    /**
     *
     * @param tuple
     * @return true if the tuple matches this condition terms that are part of this filter
     */
    @Override
    public boolean match(Tuple tuple) {

        if (boolean_connector == CompositeLookupFilter.AND) {
            return matchAnd(tuple);
        }

        return matchOr(tuple);

    }

    private boolean matchAnd(Tuple tuple) {

        for (LookupFilter filter : filters) {
            if (!filter.match(tuple)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchOr(Tuple tuple) {

        for (LookupFilter filter : filters) {
            if (filter.match(tuple)) {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public boolean match(LinkedDataRow row) {

        if (boolean_connector == CompositeLookupFilter.AND) {
            return matchAnd(row);
        }

        return matchOr(row);

    }

    private boolean matchAnd(LinkedDataRow row) {

        for (LookupFilter filter : filters) {
            if (!filter.match(row)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchOr(LinkedDataRow row) {

        for (LookupFilter filter : filters) {
            if (filter.match(row)) {
                return true;
            }
        }

        return false;
    }

    
    @Override
    public String toString() {

        String output = "";
        String connector = " OR ";
        if (boolean_connector==AND)
            connector = " AND ";
        boolean first = true;
        for (LookupFilter filter : filters) {
            if (first)
                output +=filter.toString();
            else output +=connector+filter.toString();
            first = false;
        }

        return output;
    }

}
