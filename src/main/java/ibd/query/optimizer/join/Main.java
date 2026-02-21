/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.join;

import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.table.Params;
import ibd.query.Operation;
import ibd.query.TreePrinter;
import ibd.query.Tuple;
import ibd.query.binaryop.join.JoinTerm;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.sourceop.FullTableScan;
import ibd.table.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main {

    public void testPushDownOptimization(JoinQueryOptimizer1 opt, Operation query, boolean showTree, boolean runQuery) throws Exception {

        Params.BLOCKS_LOADED = 0;
        
        TreePrinter printer = new TreePrinter();

        System.out.println("BEFORE");
        if (showTree) {
            //Utils.toString(query, 0);
            printer.printTree(query);
            System.out.println("");
        }

        if (runQuery) {
            Params.BLOCKS_LOADED = 0;
            Iterator<Tuple> tuples = query.run();
            while (tuples.hasNext()) {
                Tuple r = (Tuple) tuples.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("blocks loaded " + Params.BLOCKS_LOADED);
        }

        query = opt.optimizeQuery(query);

        System.out.println("AFTER");
        if (showTree) {
            //Utils.toString(query, 0);
            printer.printTree(query);
            System.out.println("");
        }

        if (runQuery) {
            Params.BLOCKS_LOADED = 0;
            Iterator<Tuple> tuples = query.run();
            while (tuples.hasNext()) {
                Tuple r = (Tuple) tuples.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("blocks loaded  " + Params.BLOCKS_LOADED);
        }
    }

    private Operation createQuery1(DataLoader loader) throws Exception {

        Table tableMovie = loader.getMovieTable();
        Table tableDirector = loader.getDirectorTable();

        Operation scanMovie = new FullTableScan("movie", tableMovie);
        Operation scanDirector = new FullTableScan("director", tableDirector);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("movie", "idDirector", "director", "idDirector");

        Operation join1 = new NestedLoopJoin(scanMovie, scanDirector, terms);

        return join1;
    }

    private Operation createQuery2(DataLoader loader) throws Exception {

        Table tableMovie = loader.getMovieTable();
        Table tableArtist = loader.getArtistTable();
        Table tableCast = loader.getCastTable();

        Operation scanMovie = new FullTableScan("movie", tableMovie);
        Operation scanArtist = new FullTableScan("artist", tableArtist);
        Operation scanCast = new FullTableScan("cast", tableCast);

        JoinPredicate termsArtist = new JoinPredicate();
        termsArtist.addTerm("artist.idArtist", "cast.idArtist");
        termsArtist.addTerm("movie.idMovie", "cast.idMovie");

        Operation join1 = new NestedLoopJoin(scanMovie, scanArtist, new JoinPredicate());
        Operation join2 = new NestedLoopJoin(join1, scanCast, termsArtist);
        return join2;
    }

    private Operation createQuery3(DataLoader loader) throws Exception {

        Table tableMovie = loader.getMovieTable();
        Table tableArtist = loader.getArtistTable();
        Table tableCast = loader.getCastTable();

        Operation scanMovie = new FullTableScan("movie", tableMovie);
        Operation scanArtist = new FullTableScan("artist", tableArtist);
        Operation scanCast = new FullTableScan("cast", tableCast);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("movie.idMovie", "cast.idMovie");
        Operation join1 = new NestedLoopJoin(scanMovie, scanCast, terms);

        terms = new JoinPredicate();
        terms.addTerm("cast.idArtist", "artist.idArtist");
        Operation join2 = new NestedLoopJoin(join1, scanArtist, terms);

        return join2;
    }

    private Operation createRandomQuery(DataLoader loader) throws Exception {

        Table tableMovie = loader.getMovieTable();
        Table tableArtist = loader.getArtistTable();
        Table tableCast = loader.getCastTable();
        Table tableDirector = loader.getDirectorTable();

        FullTableScan scanMovie = new FullTableScan("movie", tableMovie);
        FullTableScan scanArtist = new FullTableScan("artist", tableArtist);
        FullTableScan scanCast = new FullTableScan("cast", tableCast);
        FullTableScan scanDirector = new FullTableScan("director", tableDirector);

        List<Operation> scans = new ArrayList();
        scans.add(scanMovie);
        scans.add(scanArtist);
        scans.add(scanCast);
        scans.add(scanDirector);

        List<JoinTerm> terms = new ArrayList();
        terms.add(new JoinTerm("movie.idMovie", "cast.idMovie"));
        terms.add(new JoinTerm("cast.idArtist", "artist.idArtist"));
        terms.add(new JoinTerm("movie.idDirector", "director.idDirector"));

        return buildQuery(scans, terms);

    }

    private NestedLoopJoin buildQuery1(List<FullTableScan> tables, List<JoinTerm> terms) throws Exception {
        List<Integer> addedTables = new ArrayList();
        HashMap<FullTableScan, NestedLoopJoin> tablesJoins = new HashMap();
        Random r = new Random();
        int leftIndex = -1;
        int rightIndex = -1;
        NestedLoopJoin lastJoin = null;
        while (addedTables.size() != tables.size()) {
            leftIndex = r.nextInt(tables.size());
            if (!addedTables.contains(leftIndex)) {
                addedTables.add(leftIndex);
            }
            while (addedTables.contains(rightIndex) || rightIndex == -1) {
                rightIndex = r.nextInt(tables.size());
            }

            

            FullTableScan leftTable = tables.get(leftIndex);
            FullTableScan rightTable = tables.get(rightIndex);
            JoinPredicate newTerms = new JoinPredicate();
            for (JoinTerm term : terms) {
                for (Integer addedTable : addedTables) {
                    FullTableScan leftScan = tables.get(addedTable);
                    if (term.getLeftTableAlias().equals(leftScan.getDataSourceAlias()) && term.getRightTableAlias().equals(rightTable.getDataSourceAlias())) {
                        newTerms.addTerm(term.getLeftTableAlias(), term.getLeftColumn(), term.getRightTableAlias(), term.getRightColumn());
                    } else if (term.getRightTableAlias().equals(leftScan.getDataSourceAlias()) && term.getLeftTableAlias().equals(rightTable.getDataSourceAlias())) {
                        newTerms.addTerm(term.getRightTableAlias(), term.getRightColumn(), term.getLeftTableAlias(), term.getLeftColumn());
                    }
                }
            }
            
            if (!addedTables.contains(rightIndex)) {
                addedTables.add(rightIndex);
            }
            
            NestedLoopJoin join = tablesJoins.get(leftTable);
            if (join == null) {
                lastJoin = new NestedLoopJoin(leftTable, rightTable, newTerms);
                System.out.println("join between "+leftTable.getDataSourceAlias()+" and "+rightTable.getDataSourceAlias());
                System.out.println("join terms ");
                for (JoinTerm term : newTerms.getTerms()) {
                    System.out.println(term.getLeftTableAlias()+"."+term.getLeftTableAlias()+"="+term.getRightTableAlias()+"."+term.getRightColumn());
                }
                tablesJoins.put(leftTable, lastJoin);
                tablesJoins.put(rightTable, lastJoin);
            } else {
                lastJoin = new NestedLoopJoin(join, rightTable, newTerms);
                System.out.println("join between the last join and "+rightTable.getDataSourceAlias());
                System.out.println("join terms ");
                for (JoinTerm term : newTerms.getTerms()) {
                    System.out.println(term.getLeftTableAlias()+"."+term.getLeftTableAlias()+"="+term.getRightTableAlias()+"."+term.getRightColumn());
                }
                tablesJoins.put(rightTable, lastJoin);
            }
        }
        return lastJoin;
    }
    
    private void getTables(Operation op, List<String> list){
        if (op instanceof FullTableScan){
            FullTableScan scan = (FullTableScan)op;
            list.add(scan.getDataSourceAlias());
        }
        else if (op instanceof NestedLoopJoin){
            NestedLoopJoin join = (NestedLoopJoin)op;
            getTables(join.getLeftOperation(), list);
            getTables(join.getRightOperation(), list);
        }
    }
    
    private NestedLoopJoin buildQuery(List<Operation> tables, List<JoinTerm> terms) throws Exception {
        Random r = new Random();
        NestedLoopJoin lastJoin = null;
        while (tables.size()>1) {
            int leftIndex = r.nextInt(tables.size());
            Operation leftOp = tables.remove(leftIndex);
            int rightIndex = r.nextInt(tables.size());
            Operation rightOp = tables.remove(rightIndex);
            
            
            List<String> leftTables = new ArrayList();
            getTables(leftOp, leftTables);
            List<String> rightTables = new ArrayList();
            getTables(rightOp, rightTables);
            
            
            JoinPredicate newTerms = new JoinPredicate();
            for (JoinTerm term : terms) {
                
                    if (leftTables.contains(term.getLeftTableAlias()) && rightTables.contains(term.getRightTableAlias())){
                        newTerms.addTerm(term.getLeftTableAlias(), term.getLeftColumn(), term.getRightTableAlias(), term.getRightColumn());
                    } if (rightTables.contains(term.getLeftTableAlias()) && leftTables.contains(term.getRightTableAlias())){
                        newTerms.addTerm(term.getRightTableAlias(), term.getRightColumn(), term.getLeftTableAlias(), term.getLeftColumn());
                    }
                
            }
            
            lastJoin = new NestedLoopJoin(leftOp, rightOp, newTerms);
            tables.add(lastJoin);
            
            
        }
        return lastJoin;
    }

    public static void main(String[] args) {
        try {
            Main m = new Main();
            JoinQueryOptimizer1 opt = new JoinQueryOptimizer1();

            DataLoader loader = new DataLoader();
            boolean recreate = true;
            if (recreate) loader.recreate();
            else loader.loadData();

            m.testPushDownOptimization(opt, m.createRandomQuery(loader), true, true);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
