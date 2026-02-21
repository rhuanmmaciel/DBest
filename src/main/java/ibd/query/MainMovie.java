/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import ibd.query.binaryop.conditional.Exists;
import ibd.query.binaryop.join.anti.NestedLoopLeftAntiJoin;
import ibd.query.binaryop.set.Union;
import ibd.query.binaryop.join.BlockNestedLoopJoin;
import ibd.query.binaryop.join.CrossJoin;
import ibd.table.*;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.binaryop.join.semi.NestedLoopSemiJoin;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.sourceop.IndexScan;
import ibd.query.unaryop.aggregation.Aggregation;
import ibd.query.unaryop.HashIndex;
import ibd.query.unaryop.Projection;
import ibd.query.unaryop.aggregation.AggregationType;
import ibd.query.unaryop.filter.Filter;
import ibd.table.prototype.BasicDataRow;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 *
 * @author Sergio
 */
public class MainMovie {

    public static int pageSize = 4096;
    public static int cacheSize = 99999999;

    //8192, 16384, 32768
    public void addMovies(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ",");
                String cod = tok.nextToken();
                String title = tok.nextToken();
                BasicDataRow row = new BasicDataRow();
                try {
                    row.setInt("movie_id", Integer.parseInt(cod));
                } catch (Exception e) {
                    System.out.println(count + ":" + cod + "," + title);
                }
                row.setString("title", title);

                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } finally {
            br.close();
        }

        table.flushDB();
    }

    public void addPerson(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ",");
                String cod = tok.nextToken();
                String name = tok.nextToken();
                BasicDataRow row = new BasicDataRow();
                row.setInt("person_id", Integer.parseInt(cod));
                row.setString("person_name", name);

                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } finally {
            br.close();
        }

        table.flushDB();
    }

    public void addMovieCasts(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String movie_id = "";
        String person_id = "";
        String gender_id = "";
        String character_name = "";
        String cast_order = "";
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ",");
                cast_order = tok.nextToken();
                character_name = tok.nextToken();
                gender_id = tok.nextToken();
                boolean repeat = true;
                while (repeat) {
                    repeat = false;
                    try {
                        Integer.parseInt(gender_id);
                    } catch (Exception e) {
                        character_name += gender_id;
                        gender_id = tok.nextToken();
                        repeat = true;
                    }
                }
                movie_id = tok.nextToken();
                person_id = tok.nextToken();

                if (character_name.length() > 255) {
                    character_name = character_name.substring(0, 255);
                }

                BasicDataRow row = new BasicDataRow();
                row.setInt("movie_id", Integer.parseInt(movie_id));
                row.setInt("person_id", Integer.parseInt(person_id));
                row.setInt("cast_order", Integer.parseInt(cast_order));
                row.setInt("gender_id", Integer.parseInt(gender_id));
                row.setString("character_name", character_name);

                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } catch (Exception e) {
            System.out.println("movie_id " + movie_id);
            System.out.println("person_id " + person_id);
            System.out.println("cast_order " + cast_order);
            System.out.println("character_name " + character_name);
            System.out.println("gender_id " + gender_id);
        } finally {
            br.close();
        }

        table.flushDB();
    }

    public void addMovieCasts1(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String movie_id = "";
        String person_id = "";
        String gender_id = "";
        String character_name = "";
        String cast_order = "";
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ";");

                movie_id = tok.nextToken();
                person_id = tok.nextToken();
                character_name = tok.nextToken();
                if (tok.hasMoreTokens()) {
                    gender_id = tok.nextToken();
                }
                if (tok.hasMoreTokens()) {
                    cast_order = tok.nextToken();
                }

                if (character_name.length() > 255) {
                    character_name = character_name.substring(0, 255);
                }

                BasicDataRow row = new BasicDataRow();
                row.setInt("movie_id", Integer.parseInt(movie_id));
                row.setInt("person_id", Integer.parseInt(person_id));
                row.setInt("cast_order", Integer.parseInt(cast_order));
                row.setInt("gender_id", Integer.parseInt(gender_id));
                row.setString("character_name", character_name);

                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } catch (Exception e) {
            System.out.println("movie_id " + movie_id);
            System.out.println("person_id " + person_id);
            System.out.println("cast_order " + cast_order);
            System.out.println("character_name " + character_name);
            System.out.println("gender_id " + gender_id);
            e.printStackTrace();
        } finally {
            br.close();
        }

        table.flushDB();
    }

    public void addMovieCastsIndex(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String movie_id = "";
        String person_id = "";
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ";");

                movie_id = tok.nextToken();
                person_id = tok.nextToken();

                BasicDataRow row = new BasicDataRow();
                row.setInt("person_id", Integer.parseInt(person_id));
                row.setInt("movie_id", Integer.parseInt(movie_id));

                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } catch (Exception e) {
            System.out.println("movie_id " + movie_id);
            System.out.println("person_id " + person_id);
            e.printStackTrace();
        } finally {
            br.close();
        }

        table.flushDB();
    }

    public void addMovieCrew(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String movie_id = "";
        String person_id = "";
        String department_id = "";
        String job = "";
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ",");
                department_id = tok.nextToken();
                job = tok.nextToken();
                movie_id = tok.nextToken();
                person_id = tok.nextToken();

                BasicDataRow row = new BasicDataRow();
                row.setInt("movie_id", Integer.parseInt(movie_id));
                row.setInt("person_id", Integer.parseInt(person_id));
                row.setInt("department_id", Integer.parseInt(department_id));
                row.setString("job", job);

                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } catch (Exception e) {
            System.out.println("movie_id " + movie_id);
            System.out.println("person_id " + person_id);
            System.out.println("department_id " + department_id);
            System.out.println("job " + job);
            System.out.println(e.getMessage());

        } finally {
            br.close();
        }

        table.flushDB();
    }

    public void addMovieCrew1(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String movie_id = "";
        String person_id = "";
        String department_id = "";
        String job = "";
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ",");

                movie_id = tok.nextToken();
                person_id = tok.nextToken();
                department_id = tok.nextToken();
                job = tok.nextToken();

                BasicDataRow row = new BasicDataRow();
                row.setInt("movie_id", Integer.parseInt(movie_id));
                row.setInt("person_id", Integer.parseInt(person_id));
                row.setInt("department_id", Integer.parseInt(department_id));
                row.setString("job", job);

                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } catch (Exception e) {
            System.out.println("movie_id " + movie_id);
            System.out.println("person_id " + person_id);
            System.out.println("department_id " + department_id);
            System.out.println("job " + job);
            System.out.println(e.getMessage());

        } finally {
            br.close();
        }

        table.flushDB();
    }

    public void addMovieCrewIndex(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String movie_id = "";
        String person_id = "";
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                //System.out.println(count);
                if (count == 0) {
                    count++;
                    line = br.readLine();
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line, ",");

                movie_id = tok.nextToken();
                person_id = tok.nextToken();

                BasicDataRow row = new BasicDataRow();
                row.setInt("person_id", Integer.parseInt(person_id));
                row.setInt("movie_id", Integer.parseInt(movie_id));
                table.addRecord(row, true);
                count++;
                line = br.readLine();

            }

        } catch (Exception e) {
            System.out.println("movie_id " + movie_id);
            System.out.println("person_id " + person_id);
            System.out.println(e.getMessage());

        } finally {
            br.close();
        }

        table.flushDB();
    }

    /**
     * Print information of all rows from the table
     *
     * @param table the target table
     * @throws Exception
     */
    public void getAll(Table table) throws Exception {
        List<LinkedDataRow> list = table.getAllRecords();
        for (LinkedDataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }

    /**
     * Print information from people of a specific department
     *
     * @param table the target table
     * @param dept the department
     * @throws Exception
     */
    public void getSalary(Table table, String dept) throws Exception {
        List<LinkedDataRow> list = table.getRecords("dept", dept, ComparisonTypes.EQUAL);
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }

    public Prototype createMovieSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("movie_id", true));
        pt.addColumn(new StringColumn("title", (short) 90));
        return pt;
    }

    public Prototype createPersonSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("person_id", true));
        pt.addColumn(new StringColumn("person_name", (short) 80));
        return pt;
    }

    public Prototype createMovieCastSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("movie_id", true));
        pt.addColumn(new IntegerColumn("person_id", true));
        pt.addColumn(new IntegerColumn("cast_order"));
        pt.addColumn(new IntegerColumn("gender_id"));
        pt.addColumn(new StringColumn("character_name"));
        return pt;
    }

    public Prototype createMovieCastIndexSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("person_id", true));
        pt.addColumn(new IntegerColumn("movie_id", true));
        return pt;
    }

    public Prototype createMovieCrewSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("movie_id", true));
        pt.addColumn(new IntegerColumn("person_id", true));
        pt.addColumn(new IntegerColumn("department_id"));
        pt.addColumn(new StringColumn("job", (short) 60));
        return pt;
    }

    public Prototype createMovieCrewIndexSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("person_id", true));
        pt.addColumn(new IntegerColumn("movie_id", true));
        return pt;
    }

    public Operation createQuery1() throws Exception {
        Table movieTable = Directory.getTable("c:\\teste\\ibd", "movie",null,cacheSize,   pageSize, false);
        Table movieCastTable = Directory.getTable("c:\\teste\\ibd", "movieCast",null,cacheSize,   pageSize, false);
        Table movieCrewTable = Directory.getTable( "c:\\teste\\ibd", "movieCrew",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable("c:\\teste\\ibd", "person",null,cacheSize,   pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);

        LookupFilter lup = new SingleColumnLookupFilter(new ColumnElement("movie.movie_id"),  ComparisonTypes.LOWER_THAN, new LiteralElement(100));
        Filter filter = new Filter(scanMovie, lup);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("movie.movie_id", "movie_cast.movie_id");
        NestedLoopJoin join1 = new NestedLoopJoin(filter, scanMovieCast, terms);

        Aggregation groupBy = new Aggregation(join1, "g", "movie.title", "movie_cast.cast_order", AggregationType.MAX, true);
        //3718093

        return groupBy;
    }

    public Operation createQuery2() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie",null,cacheSize,  pageSize, false);
        Table movieCastTable = Directory.getTable("c:\\teste\\ibd", "movieCast",null,cacheSize,   pageSize, false);
        Table movieCrewTable = Directory.getTable("c:\\teste\\ibd", "movieCrew",null,cacheSize,   pageSize, false);
        Table personTable = Directory.getTable("c:\\teste\\ibd", "person",null,cacheSize,   pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
        IndexScan scanPersonCast = new IndexScan("person_cast", personTable);
        IndexScan scanPersonCrew = new IndexScan("person_crew", personTable);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("movie.movie_id", "movie_cast.movie_id");
        NestedLoopJoin join1 = new NestedLoopJoin(scanMovie, scanMovieCast, terms);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("movie_cast.person_id", "person_cast.person_id");
        NestedLoopJoin join2 = new NestedLoopJoin(join1, scanPersonCast, terms1);

        JoinPredicate terms2 = new JoinPredicate();
        terms2.addTerm("movie.movie_id", "movie_crew.movie_id");
        NestedLoopJoin join3 = new NestedLoopJoin(join2, scanMovieCrew, terms2);

        JoinPredicate terms3 = new JoinPredicate();
        terms3.addTerm("movie_crew.person_id", "person_crew.person_id");
        NestedLoopJoin join4 = new NestedLoopJoin(join3, scanPersonCrew, terms3);

        CompositeLookupFilter compositeFilter = new CompositeLookupFilter(CompositeLookupFilter.OR);

        SingleColumnLookupFilter filter1 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        SingleColumnLookupFilter filter2 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        compositeFilter.addFilter(filter1);
        compositeFilter.addFilter(filter2);

        Filter filter = new Filter(join4, compositeFilter);
        //3718093

        return join4;
    }

    public Operation createQuery3() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie",null,cacheSize,  pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table movieCrewTable = Directory.getTable("c:\\teste\\ibd", "movieCrew",null,cacheSize,   pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person",null,cacheSize,  pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
        IndexScan scanPersonCast = new IndexScan("person_cast", personTable);
        IndexScan scanPersonCrew = new IndexScan("person_crew", personTable);

        SingleColumnLookupFilter lookupCast = new SingleColumnLookupFilter(new ColumnElement("movie_cast.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
        Filter filterCast = new Filter(scanMovieCast, lookupCast);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("movie_cast.person_id", "person_cast.person_id");
        NestedLoopJoin join1 = new NestedLoopJoin(filterCast, scanPersonCast, terms1);

        SingleColumnLookupFilter lookupCrew = new SingleColumnLookupFilter(new ColumnElement("movie_crew.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
        Filter filterCrew = new Filter(scanMovieCrew, lookupCrew);

        JoinPredicate terms2 = new JoinPredicate();
        terms2.addTerm("movie_crew.person_id", "person_crew.person_id");
        NestedLoopJoin join2 = new NestedLoopJoin(filterCrew, scanPersonCrew, terms2);

        JoinPredicate terms = new JoinPredicate();
        CrossJoin join3 = new CrossJoin(join1, join2);
        //NestedLoopJoin join3 = new NestedLoopJoin(filterCrew, filterCast, terms);

        NestedLoopJoin join4 = new NestedLoopJoin(scanMovie, join3, terms);

        //TwoColumnsLookupFilter filter1 = new TwoColumnsLookupFilter("person_crew.person_name", "person_cast.person_name", ComparisonTypes.EQUAL);
        CompositeLookupFilter filter1 = new CompositeLookupFilter(CompositeLookupFilter.OR);

        SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        SingleColumnLookupFilter singleFilter2 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        filter1.addFilter(singleFilter1);
        filter1.addFilter(singleFilter2);

        Filter filter = new Filter(join4, filter1);

        //3718093
        return filter;
    }

    public Operation createQuery4() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie", null,cacheSize, pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table movieCrewTable = Directory.getTable( "c:\\teste\\ibd", "movieCrew",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person", null,cacheSize, pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        IndexScan scanMovie1 = new IndexScan("movie1", movieTable);
        IndexScan scanMovie2 = new IndexScan("movie2", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
        IndexScan scanPersonCast = new IndexScan("person_cast", personTable);
        IndexScan scanPersonCrew = new IndexScan("person_crew", personTable);

        SingleColumnLookupFilter filter1 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        Filter filterCast = new Filter(scanPersonCast, filter1);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("person_cast.person_id", "movie_cast.person_id");
        NestedLoopJoin join1 = new NestedLoopJoin(filterCast, scanMovieCast, terms1);

        JoinPredicate terms2 = new JoinPredicate();
        terms2.addTerm("movie_cast.movie_id", "movie1.movie_id");
        NestedLoopJoin join2 = new NestedLoopJoin(join1, scanMovie1, terms2);

        Projection proj1 = new Projection(join2, new String[]{"movie1.title"});

        SingleColumnLookupFilter filter2 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        Filter filterCrew = new Filter(scanPersonCrew, filter2);

        JoinPredicate terms3 = new JoinPredicate();
        terms3.addTerm("person_crew.person_id", "movie_crew.person_id");
        NestedLoopJoin join3 = new NestedLoopJoin(filterCrew, scanMovieCrew, terms3);

        JoinPredicate terms4 = new JoinPredicate();
        terms4.addTerm("movie_crew.movie_id", "movie2.movie_id");
        NestedLoopJoin join4 = new NestedLoopJoin(join3, scanMovie2, terms4);

        Projection proj2 = new Projection(join4,  new String[]{"movie2.title"});

        Union union = new Union(proj1, proj2);

        return union;
    }

    public Operation createQuery5() throws Exception {

        Table movieTable = Directory.getTable("c:\\teste\\ibd", "movie",null,cacheSize,   pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table movieCrewTable = Directory.getTable( "c:\\teste\\ibd", "movieCrew",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person",null,cacheSize,  pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanPersonCast = new IndexScan("person", personTable);

        LookupFilter lup = new SingleColumnLookupFilter(new ColumnElement("person.person_id"), ComparisonTypes.LOWER_EQUAL_THAN, new LiteralElement(10000));
        Filter filter = new Filter(scanPersonCast, lup);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("person.person_id", "movie_cast.movie_id");
        //NestedLoopJoin join1 = new NestedLoopJoin(filter, scanMovieCast, terms);
        BlockNestedLoopJoin join1 = new BlockNestedLoopJoin(filter, scanMovieCast, terms, 2000000);

        return join1;
    }

    public Operation createTest1() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie",null,cacheSize,  pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable("c:\\teste\\ibd", "person",null,cacheSize,   pageSize, false);

        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanPerson = new IndexScan("person", personTable);

        HashIndex hash = new HashIndex(scanMovieCast);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("movie.movie_id", "movie_cast.movie_id");
        NestedLoopJoin join1 = new NestedLoopJoin(scanMovie, scanMovieCast, terms);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("movie_cast.person_id", "person.person_id");
        NestedLoopJoin join2 = new NestedLoopJoin(join1, scanPerson, terms1);

        /*
        CompositeLookupFilter filter1 = new CompositeLookupFilter(CompositeLookupFilter.OR);

        SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter("movie.title", ComparisonTypes.EQUAL, "a");
        SingleColumnLookupFilter singleFilter2 = new SingleColumnLookupFilter("person.person_name", ComparisonTypes.EQUAL, "b");
        filter1.addFilter(singleFilter1);
        filter1.addFilter(singleFilter2);

        Filter filter = new Filter(join2, filter1);
        */

        //join2.setPageInfo(0, 10);
        return join1;
    }

    public Operation createTest2() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie",null,cacheSize,  pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable(  "c:\\teste\\ibd", "person",null,cacheSize, pageSize, false);

        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanPerson = new IndexScan("person", personTable);

        LookupFilter lup = new SingleColumnLookupFilter(new ColumnElement("movie.title"), ComparisonTypes.GREATER_THAN, new LiteralElement("L"));
        Filter filter = new Filter(scanMovie, lup);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("movie.movie_id", "movie_cast.movie_id");
        NestedLoopJoin join1 = new NestedLoopJoin(filter, scanMovieCast, terms);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("movie_cast.person_id", "person.person_id");
        NestedLoopJoin join2 = new NestedLoopJoin(join1, scanPerson, terms1);

        return join2;
    }
    
    
    public Operation createTest3() throws Exception {
        Table movieCastIndexTable = Directory.getTable("c:\\teste\\ibd", "movieCastIndex",null,cacheSize,   pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person1",null,cacheSize,  pageSize, false);

        IndexScan scanPerson = new IndexScan("person", personTable);
        IndexScan scanMovieCrewIndex = new IndexScan("movie_castIndex", movieCastIndexTable);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("person.person_id", "movie_castIndex.person_id");
        NestedLoopSemiJoin join1 = new NestedLoopSemiJoin(scanPerson, scanMovieCrewIndex, terms);
        //TwoColumnsLookupFilter filter1 = new TwoColumnsLookupFilter("person.person_id", "movie_castIndex.person_id", ComparisonTypes.DIFF);
        
        return join1;
    }

    public Operation createTest4() throws Exception {
        Table movieCastIndexTable = Directory.getTable( "c:\\teste\\ibd", "movieCastIndex",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person1", null,cacheSize, pageSize, false);

        IndexScan scanPerson = new IndexScan("person", personTable);
        IndexScan scanMovieCrewIndex = new IndexScan("movie_castIndex", movieCastIndexTable);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("person.person_id", "movie_castIndex.person_id");
        NestedLoopLeftAntiJoin join1 = new NestedLoopLeftAntiJoin(scanPerson, scanMovieCrewIndex, terms);
        //TwoColumnsLookupFilter filter1 = new TwoColumnsLookupFilter("person.person_id", "movie_castIndex.person_id", ComparisonTypes.DIFF);
        
        return join1;
    }
    
    
    public Operation createTest5() throws Exception {
        Table movieTable = Directory.getTable("c:\\teste\\ibd", "movie", null,cacheSize,  pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table movieCrewTable = Directory.getTable("c:\\teste\\ibd", "movieCrew", null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person",null,cacheSize,  pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovie1 = new IndexScan("movie1", movieTable);
        IndexScan scanMovie2 = new IndexScan("movie2", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);

        
        
        SingleColumnLookupFilter lookupCast = new SingleColumnLookupFilter(new ColumnElement("movie1.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
        Filter filterCast = new Filter(scanMovie1, lookupCast);

        SingleColumnLookupFilter lookupCrew = new SingleColumnLookupFilter(new ColumnElement("movie2.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
        Filter filterCrew = new Filter(scanMovie2, lookupCrew);
        
        JoinPredicate termsSemiJoinCast = new JoinPredicate();
        termsSemiJoinCast.addTerm("movie1.movie_id", "movie_cast.movie_id");
        NestedLoopSemiJoin joinCast = new NestedLoopSemiJoin(filterCast, scanMovieCast, termsSemiJoinCast);
        
        JoinPredicate termsSemiJoinCrew = new JoinPredicate();
        termsSemiJoinCrew.addTerm("movie2.movie_id", "movie_crew.movie_id");
        NestedLoopSemiJoin joinCrew = new NestedLoopSemiJoin(filterCrew, scanMovieCrew, termsSemiJoinCrew);
        
        Exists exists = new Exists(joinCast, joinCrew, false);
        
        JoinPredicate termsFinalSemiJoin = new JoinPredicate();
        NestedLoopSemiJoin finalSemiJoinCrew = new NestedLoopSemiJoin(scanMovie, exists, termsFinalSemiJoin);
        
        

        return finalSemiJoinCrew;
    }
    
    
    public Operation createTest6() throws Exception {
        Table movieTable = Directory.getTable("c:\\teste\\ibd", "movie",null,cacheSize,   pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table movieCastIndexTable = Directory.getTable("c:\\teste\\ibd", "movieCastIndex",null,cacheSize,   pageSize, false);
        Table movieCrewTable = Directory.getTable( "c:\\teste\\ibd", "movieCrew",null,cacheSize,  pageSize, false);
        Table movieCrewIndexTable = Directory.getTable( "c:\\teste\\ibd", "movieCrewIndex",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person",null,cacheSize,  pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        IndexScan scanPerson = new IndexScan("person", personTable);
        IndexScan scanMovieCastIndex = new IndexScan("movie_cast", movieCastIndexTable);

        Aggregation groupBy = new Aggregation(scanMovieCastIndex, "gr", "movie_cast", "person_id", "movie_cast", "movie_id",  AggregationType.COUNT,  true) ;

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("gr.person_id", "person.person_id");
        NestedLoopJoin join1 = new NestedLoopJoin(groupBy, scanPerson, terms1);
        SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter(new ColumnElement("person.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        Filter filter = new Filter(join1, singleFilter1);
        return join1;
    }

    public Operation createTest7() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie", null,cacheSize, pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table movieCastIndexTable = Directory.getTable( "c:\\teste\\ibd", "movieCastIndex",null,cacheSize, pageSize, false);
        Table movieCrewTable = Directory.getTable("c:\\teste\\ibd", "movieCrew",null,cacheSize,   pageSize, false);
        Table movieCrewIndexTable = Directory.getTable("c:\\teste\\ibd", "movieCrewIndex",null,cacheSize,   pageSize, false);
        Table personTable = Directory.getTable("c:\\teste\\ibd", "person",null,cacheSize,   pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        IndexScan scanMovie1 = new IndexScan("movie1", movieTable);
        IndexScan scanMovie2 = new IndexScan("movie2", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanMovieCastIndex = new IndexScan("movie_cast_index", movieCastIndexTable);

        IndexScan scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
        IndexScan scanMovieCrewIndex = new IndexScan("movie_crew_index", movieCrewIndexTable);
        IndexScan scanPersonCast = new IndexScan("person_cast", personTable);
        IndexScan scanPersonCrew = new IndexScan("person_crew", personTable);

        SingleColumnLookupFilter filter1 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        Filter filterCast = new Filter(scanPersonCast, filter1);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("person_cast.person_id", "movie_cast_index.person_id");
        NestedLoopJoin join1 = new NestedLoopJoin(filterCast, scanMovieCastIndex, terms1);

        JoinPredicate terms2 = new JoinPredicate();
        terms2.addTerm("movie_cast_index.movie_id", "movie1.movie_id");
        NestedLoopJoin join2 = new NestedLoopJoin(join1, scanMovie1, terms2);

        Projection proj1 = new Projection(join2,  new String[]{"movie1.title"});

        SingleColumnLookupFilter filter2 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        Filter filterCrew = new Filter(scanPersonCrew, filter2);

        JoinPredicate terms3 = new JoinPredicate();
        terms3.addTerm("person_crew.person_id", "movie_crew_index.person_id");
        NestedLoopJoin join3 = new NestedLoopJoin(filterCrew, scanMovieCrewIndex, terms3);

        JoinPredicate terms4 = new JoinPredicate();
        terms4.addTerm("movie_crew_index.movie_id", "movie2.movie_id");
        NestedLoopJoin join4 = new NestedLoopJoin(join3, scanMovie2, terms4);

        Projection proj2 = new Projection(join4,  new String[]{"movie2.title"});

        Union union = new Union(proj1, proj2);

        return union;
    }


    public Operation createTest7_old() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie",null,cacheSize,  pageSize, false);
        Table movieCastTable = Directory.getTable( "c:\\teste\\ibd", "movieCast",null,cacheSize,  pageSize, false);
        Table movieCrewTable = Directory.getTable("c:\\teste\\ibd", "movieCrew",null,cacheSize,   pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person",null,cacheSize,  pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        Operation scanMovie = new IndexScan("movie", movieTable);
        Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        Operation scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
        Operation scanPersonCast = new IndexScan("person_cast", personTable);
        Operation scanPersonCrew = new IndexScan("person_crew", personTable);

        //scanMovieCast = new HashIndex(scanMovieCast,"");
        scanMovieCrew = new HashIndex(scanMovieCrew);
        //scanPersonCast = new HashIndex(scanPersonCast,"");
        scanPersonCrew = new HashIndex(scanPersonCrew);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("movie.movie_id", "movie_cast.movie_id");
        NestedLoopJoin join1 = new NestedLoopJoin(scanMovie, scanMovieCast, terms);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("movie_cast.person_id", "person_cast.person_id");
        NestedLoopJoin join2 = new NestedLoopJoin(join1, scanPersonCast, terms1);

        JoinPredicate terms2 = new JoinPredicate();
        terms2.addTerm("movie.movie_id", "movie_crew.movie_id");
        NestedLoopJoin join3 = new NestedLoopJoin(join2, scanMovieCrew, terms2);

        JoinPredicate terms3 = new JoinPredicate();
        terms3.addTerm("movie_crew.person_id", "person_crew.person_id");
        NestedLoopJoin join4 = new NestedLoopJoin(join3, scanPersonCrew, terms3);

        CompositeLookupFilter compositeFilter = new CompositeLookupFilter(CompositeLookupFilter.OR);

        SingleColumnLookupFilter filter1 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        SingleColumnLookupFilter filter2 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        compositeFilter.addFilter(filter1);
        compositeFilter.addFilter(filter2);

        Filter filter = new Filter(join4, compositeFilter);
        //3718093

        return filter;
    }
    
    
    public Operation createTest8() throws Exception {
        Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie",null,cacheSize,  pageSize, false);
        Table movieCastTable = Directory.getTable(  "c:\\teste\\ibd", "movieCast",null,cacheSize, pageSize, false);
        Table movieCrewTable = Directory.getTable( "c:\\teste\\ibd", "movieCrew",null,cacheSize,  pageSize, false);
        Table personTable = Directory.getTable( "c:\\teste\\ibd", "person",null,cacheSize,  pageSize, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
        IndexScan scanMovie = new IndexScan("movie", movieTable);
        IndexScan scanMovieCast = new IndexScan("movie_cast", movieCastTable);
        IndexScan scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
        IndexScan scanPersonCast = new IndexScan("person_cast", personTable);
        IndexScan scanPersonCrew = new IndexScan("person_crew", personTable);

        SingleColumnLookupFilter lookupCast = new SingleColumnLookupFilter(new ColumnElement("movie_cast.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
        Filter filterCast = new Filter(scanMovieCast, lookupCast);

        JoinPredicate terms1 = new JoinPredicate();
        terms1.addTerm("movie_cast.person_id", "person_cast.person_id");
        NestedLoopJoin join1 = new NestedLoopJoin(filterCast, scanPersonCast, terms1);

        SingleColumnLookupFilter lookupCrew = new SingleColumnLookupFilter(new ColumnElement("movie_crew.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
        Filter filterCrew = new Filter(scanMovieCrew, lookupCrew);

        JoinPredicate terms2 = new JoinPredicate();
        terms2.addTerm("movie_crew.person_id", "person_crew.person_id");
        NestedLoopJoin join2 = new NestedLoopJoin(filterCrew, scanPersonCrew, terms2);

        JoinPredicate terms = new JoinPredicate();
        CrossJoin join3 = new CrossJoin(join1, join2);
        //NestedLoopJoin join3 = new NestedLoopJoin(filterCrew, filterCast, terms);

        NestedLoopJoin join4 = new NestedLoopJoin(scanMovie, join3, terms);

        //TwoColumnsLookupFilter filter1 = new TwoColumnsLookupFilter("person_crew.person_name", "person_cast.person_name", ComparisonTypes.EQUAL);
        CompositeLookupFilter filter1 = new CompositeLookupFilter(CompositeLookupFilter.OR);

        SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        SingleColumnLookupFilter singleFilter2 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
        filter1.addFilter(singleFilter1);
        filter1.addFilter(singleFilter2);

        Filter filter = new Filter(join4, filter1);

        //3718093
        return filter;
    }

    

    public void execQuery(Operation query) throws Exception {
        //query.prepare();
        Iterator<Tuple> tuples = query.run();
        int count = 0;
        while (tuples.hasNext()) {
            Tuple tuple = tuples.next();
            //System.out.println(count+":"+tuple);
            count++;
//                if (count > 100) {
//                    break;
//                }
        }

      System.out.println("count " + count);
    }
    
    public Operation createQuery() throws Exception{
        return createTest7_old();
    }

    public static void main(String[] args) throws Exception {

        MainMovie main = new MainMovie();

        Prototype movieSchema = main.createMovieSchema();
        Prototype movieCastSchema = main.createMovieCastSchema();
        Prototype movieCastIndexSchema = main.createMovieCastIndexSchema();
        Prototype movieCrewSchema = main.createMovieCrewSchema();
        Prototype movieCrewIndexSchema = main.createMovieCrewIndexSchema();
        Prototype personSchema = main.createPersonSchema();
        
        

        boolean execInsert = false, execQuery = true;

        if (execInsert) {

            Table movieTable = Directory.getTable( "c:\\teste\\ibd", "movie", movieSchema,cacheSize, pageSize, true);
            main.addMovies(movieTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movieOrdered.csv");
            movieTable.flushDB();

            Table movieCastTable = Directory.getTable("c:\\teste\\ibd", "movieCast",movieCastSchema,cacheSize,   pageSize, true);
            main.addMovieCasts1(movieCastTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movie_castOrdered.csv");
            movieCastTable.flushDB();

            Table movieCastIndexTable = Directory.getTable( "c:\\teste\\ibd", "movieCastIndex",movieCastIndexSchema,cacheSize,  pageSize, true);
            main.addMovieCastsIndex(movieCastIndexTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movie_castOrdered.csv");
            movieCastIndexTable.flushDB();

            Table movieCrewTable = Directory.getTable("c:\\teste\\ibd", "movieCrew", movieCrewSchema,cacheSize,  pageSize, true);
            main.addMovieCrew1(movieCrewTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movie_crewOrdered.csv");
            movieCrewTable.flushDB();

            Table movieCrewIndexTable = Directory.getTable("c:\\teste\\ibd", "movieCrewIndex",movieCrewIndexSchema,cacheSize,   pageSize, true);
            main.addMovieCrewIndex(movieCrewIndexTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movie_crewOrdered.csv");
            movieCrewIndexTable.flushDB();

            Table personTable = Directory.getTable("c:\\teste\\ibd", "person", personSchema,cacheSize,  pageSize, true);
            main.addPerson(personTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\personOrdered.csv");
            personTable.flushDB();
        }
        //main.getAll(movieTable);

        

        if (execQuery) {
            //main.execQuery(main.createQuery4());
//            main.execQuery(main.createQuery4());
//            main.execQuery(main.createQuery3());
//            main.execQuery(main.createQuery2());
//            main.execQuery(main.createQuery3());
            int times = 5;
            List<Operation> warmUpQueries = new ArrayList();
            List<Operation> testQueries = new ArrayList();
            for (int i = 0; i < times; i++) {
                Operation warmUpQuery = main.createQuery();
                if (i==0){
                    System.out.println("Query Tree");
                    TreePrinter printer = new TreePrinter();
                    printer.printTree(warmUpQuery);
                }
                warmUpQueries.add(warmUpQuery);
                Operation testQuery = main.createQuery();
                testQueries.add(testQuery);
            }
            
            //Operation query = main.createTest1();
            
            
            System.out.println("Exec Query");
            
            System.out.println("warm up");
            long start = System.currentTimeMillis();
            for (int i = 0; i < times; i++) {
                main.execQuery(warmUpQueries.get(i));
            }
            long end = System.currentTimeMillis();
            System.out.println("time: " + (end - start) / times);
            System.out.println("begin measuring");
            start = System.currentTimeMillis();
            for (int i = 0; i < times; i++) {
                main.execQuery(testQueries.get(i));
            }
            end = System.currentTimeMillis();
            System.out.println("time: " + (end - start) / times);
        }

    }

}
