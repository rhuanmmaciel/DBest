/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.binaryop.join;

import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.ComparisonTypes;
import java.util.List;

/**
 *
 * @author ferna
 */
public abstract class LookupJoin extends Join {
    //the filter that needs to be performed over the right side operation.
    protected CompositeLookupFilter joinFilter;
    
    protected boolean hasNoFilters = false;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public LookupJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public boolean useLeftSideLookups() {
        return true;
    }
    
    public boolean hasNoFilters() {
        return true;
    }

    @Override
    public void prepare() throws Exception {
        
        //creates the single filter condition that will be pushed down to the right side operation.
        createJoinFilter();
        
        super.prepare();
        
    }


    //creates the single filter condition that will be pushed down to the right side operation. 
    //each time a left-side tuple performs a lookup on the right side, ths filter is reused. 
    //Only the look-up value coming from the left is replaced, in the moment of the lookup.
    private void createJoinFilter() throws Exception {
        hasNoFilters = true;
        joinFilter = new CompositeLookupFilter(CompositeLookupFilter.AND);
        for (JoinTerm term : joinPredicate.getTerms()) {
            SingleColumnLookupFilter f = new SingleColumnLookupFilter(new ColumnElement(term.getRightColumnDescriptor()), ComparisonTypes.EQUAL, new LiteralElement(0));
            joinFilter.addFilter(f);
            hasNoFilters = false;
        }
    }

    /**
     *
     * @return the join filters
     */
    @Override
    public LookupFilter getFilters() {
        return joinFilter;
    }
    
    //fills the join filters with the left-side values that are necessary to perform the lookup.
        protected void fillFilter(Tuple currentLeftTuple) {
            List<JoinTerm> joinTerms = joinPredicate.getTerms();
            List<LookupFilter> filters = joinFilter.getFilters();
            for (int i = 0; i < filters.size(); i++) {
                LookupFilter filter = filters.get(i);
                JoinTerm joinTerm = joinTerms.get(i);
                SingleColumnLookupFilter f = (SingleColumnLookupFilter) filter;
                //Comparable value = currentLeftTuple.rows[joinTerm.getLeftTupleRowIndex()].getValue(f.getColumn());
                Comparable value = currentLeftTuple.rows[joinTerm.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(joinTerm.getLeftColumnDescriptor().getColumnLocation().colIndex);
                LiteralElement elem = (LiteralElement)f.getComparedElement();
                elem.setValue(value);

            }
        }

}
