/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import dsl.DslParser;
import ibd.query.binaryop.conditional.Exists;
import ibd.query.binaryop.conditional.LogicalAnd;
import ibd.query.binaryop.conditional.LogicalOr;
import ibd.query.binaryop.join.CrossJoinOld;
import ibd.query.binaryop.join.anti.NestedLoopLeftAntiJoin;
import ibd.query.binaryop.set.Union;
import ibd.query.binaryop.join.HashInnerJoin;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.binaryop.join.semi.NestedLoopSemiJoin;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.sourceop.IndexScan;
import ibd.query.unaryop.aggregation.Aggregation;
import ibd.query.unaryop.HashIndex;
import ibd.query.unaryop.Projection;
import ibd.query.unaryop.aggregation.AggregationType;
import ibd.query.unaryop.filter.Condition;
import ibd.query.unaryop.filter.Filter;
import ibd.query.unaryop.sort.Sort;
import ibd.table.ComparisonTypes;
import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.function.Supplier;

/**
 *
 * @author Sergio
 */
public class SBBD {

    public static int pageSize = 4096;
    public static int cacheSize = 99999999;

    //public static final String folder = "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\";
    public static final String folder = "C:\\Users\\ferna\\Documents\\dbest\\oficina\\testeConversao\\";
    //public static final String folder = "./";

    //8192, 16384, 32768
    /* *********** SCHEMA CREATION ****************/
    public void createAllTables() throws Exception {
        Prototype movieSchema = createMovieSchema();
        Prototype movieCastSchema = createMovieCastSchema();
        Prototype movieCastIndexSchema = createMovieCastIndexSchema();
        Prototype movieCrewSchema = createMovieCrewSchema();
        Prototype movieCrewIndexSchema = createMovieCrewIndexSchema();
        Prototype personSchema = createPersonSchema();

        System.out.println("Creating tables from CSV files");
        System.out.println("Make sure all CSV are located at the same folder as this program.");

        System.out.println("Page Size: " + pageSize + " bytes");

        System.out.println("Creating table Movie...");
        Table movieTable = Directory.getTable(folder, "movie", movieSchema, cacheSize, pageSize, true);
        addMovies(movieTable, folder + "movieOrdered.csv");
        movieTable.flushDB();

        System.out.println("Creating table Cast...");
        Table movieCastTable = Directory.getTable(folder, "movieCast", movieCastSchema, cacheSize, pageSize, true);
        addMovieCasts(movieCastTable, folder + "movie_castOrdered.csv");
        movieCastTable.flushDB();

        System.out.println("Creating index for cast...");
        Table movieCastIndexTable = Directory.getTable(folder, "movieCastIndex", movieCastIndexSchema, cacheSize, pageSize, true);
        addMovieCastsIndex(movieCastIndexTable, folder + "movie_castOrdered.csv");
        movieCastIndexTable.flushDB();

        System.out.println("Creating table crew...");
        Table movieCrewTable = Directory.getTable(folder, "movieCrew", movieCrewSchema, cacheSize, pageSize, true);
        addMovieCrew(movieCrewTable, folder + "movie_crewOrdered.csv");
        movieCrewTable.flushDB();

        System.out.println("Creating index for crew...");
        Table movieCrewIndexTable = Directory.getTable(folder, "movieCrewIndex", movieCrewIndexSchema, cacheSize, pageSize, true);
        addMovieCrewIndex(movieCrewIndexTable, folder + "movie_crewOrdered.csv");
        movieCrewIndexTable.flushDB();

        System.out.println("Creating table person...");
        Table personTable = Directory.getTable(folder, "person", personSchema, cacheSize, pageSize, true);
        addPerson(personTable, folder + "personOrdered.csv");
        personTable.flushDB();

        System.out.println("done!");
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

    /* *********** ADDING ROWS ****************/
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

    /* *********** QUERIES CREATION ****************/
    public Operation createTest1XXX(boolean hash) {
        try {
            return DslParser.readQuery(new File("C:\\Users\\ferna\\Dropbox\\dbest\\query trees\\23 - union 4.txt"));
            //return join2;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    
    public Operation createTest1(boolean hash) {
        try {

            Table movieTable = Directory.getTable(folder, "movie.dat", null, cacheSize, pageSize, false);
            Table movieCastTable = Directory.getTable(folder, "movieCast.dat", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person.dat", null, cacheSize, pageSize, false);

            Operation scanMovie = new IndexScan("movie", movieTable);
            Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);
            Operation scanPerson = new IndexScan("person", personTable);

            if (hash) {
                //scanMovie = new Memoize(scanMovie,"");
                scanMovieCast = new HashIndex(scanMovieCast);
                scanPerson = new HashIndex(scanPerson);
            }

            JoinPredicate terms = new JoinPredicate();
            terms.addTerm("movie.movie_id", "movie_cast.movie_id");
            //terms.addTerm("movie_cast.person_id", "person.person_id");
            Operation join1 = new HashInnerJoin(scanMovie, scanMovieCast, terms);

            JoinPredicate terms1 = new JoinPredicate();
            terms1.addTerm("movie_cast.person_id", "person.person_id");
            Operation join2 = new HashInnerJoin(join1, scanPerson, terms1);

            /*
        CompositeLookupFilter filter1 = new CompositeLookupFilter(CompositeLookupFilter.OR);

        SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter("movie.title", ComparisonTypes.EQUAL, "a");
        SingleColumnLookupFilter singleFilter2 = new SingleColumnLookupFilter("person.person_name", ComparisonTypes.EQUAL, "b");
        filter1.addFilter(singleFilter1);
        filter1.addFilter(singleFilter2);

        Filter filter = new Filter(join2, filter1);
             */
            //join2.setPageInfo(0, 10);
            //return new IndexScan("movie", movieTable);
            
            
            return join2;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest2(boolean hash) {
        try {
            Table movieTable = Directory.getTable(folder, "movie", null, cacheSize, 0, false);
            Table movieCastTable = Directory.getTable(folder, "movieCast", null, cacheSize, 0, false);
            Table personTable = Directory.getTable(folder, "person", null, cacheSize, 0, false);

            Operation scanMovie = new IndexScan("movie", movieTable);
            Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);
            Operation scanPerson = new IndexScan("person", personTable);

            if (hash) {
                //scanMovie = new HashIndex(scanMovie,"");
                scanMovieCast = new HashIndex(scanMovieCast);
                scanPerson = new HashIndex(scanPerson);
            }

            LookupFilter lup = new SingleColumnLookupFilter(new ColumnElement("movie.title"), ComparisonTypes.GREATER_THAN, new LiteralElement("ZZ"));
            Filter filter = new Filter(scanMovie, lup);

            JoinPredicate terms = new JoinPredicate();
            terms.addTerm("movie.movie_id", "movie_cast.movie_id");
            Operation join1 = new NestedLoopJoin(filter, scanMovieCast, terms);

            JoinPredicate terms1 = new JoinPredicate();
            terms1.addTerm("movie_cast.person_id", "person.person_id");
            Operation join2 = new NestedLoopJoin(join1, scanPerson, terms1);

            return join2;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest3(boolean hash) {
        try {
            Table movieCastIndexTable = Directory.getTable(folder, "movieCastIndex", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person", null, cacheSize, pageSize, false);

            Operation scanPerson = new IndexScan("person", personTable);
            Operation scanMovieCrewIndex = new IndexScan("movie_castIndex", movieCastIndexTable);

            if (hash) {
                scanMovieCrewIndex = new HashIndex(scanMovieCrewIndex);
            }

            JoinPredicate terms = new JoinPredicate();
            terms.addTerm("person.person_id", "movie_castIndex.person_id");
            Operation join1 = new NestedLoopSemiJoin(scanPerson, scanMovieCrewIndex, terms);
            //TwoColumnsLookupFilter filter1 = new TwoColumnsLookupFilter("person.person_id", "movie_castIndex.person_id", ComparisonTypes.DIFF);

            return join1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest4(boolean hash) {
        try {
            Table movieCastIndexTable = Directory.getTable(folder, "movieCastIndex", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person", null, cacheSize, pageSize, false);

            Operation scanPerson = new IndexScan("person", personTable);
            Operation scanMovieCrewIndex = new IndexScan("movie_castIndex", movieCastIndexTable);

            if (hash) {
                scanMovieCrewIndex = new HashIndex(scanMovieCrewIndex);
            }

            JoinPredicate terms = new JoinPredicate();
            terms.addTerm("person.person_id", "movie_castIndex.person_id");
            NestedLoopLeftAntiJoin join1 = new NestedLoopLeftAntiJoin(scanPerson, scanMovieCrewIndex, terms);
            //TwoColumnsLookupFilter filter1 = new TwoColumnsLookupFilter("person.person_id", "movie_castIndex.person_id", ComparisonTypes.DIFF);

            return join1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest51(boolean hash) {
        try {
            Table movieTable = Directory.getTable(folder, "movie.dat", null, cacheSize, pageSize, false);
            Table movieCastTable = Directory.getTable(folder, "movieCast.dat", null, cacheSize, pageSize, false);
            Table movieCrewTable = Directory.getTable(folder, "movieCrew.dat", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person.dat", null, cacheSize, pageSize, false);
            //Table personTable1 = Directory.getTable(null, folder, "person1", pageSize, false);

            Operation scanMovie = new IndexScan("movie", movieTable);
            Operation scanMovie1 = new IndexScan("movie1", movieTable);
            Operation scanMovie2 = new IndexScan("movie2", movieTable);
            Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);
            Operation scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);

            if (hash) {
                scanMovieCast = new HashIndex(scanMovieCast);
                scanMovieCrew = new HashIndex(scanMovieCrew);
            }

            SingleColumnLookupFilter lookupCast = new SingleColumnLookupFilter(new ColumnElement("movie1.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
            Operation filterCast = new Filter(scanMovie1, lookupCast);

            SingleColumnLookupFilter lookupCrew = new SingleColumnLookupFilter(new ColumnElement("movie2.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
            Operation filterCrew = new Filter(scanMovie2, lookupCrew);

            JoinPredicate termsSemiJoinCast = new JoinPredicate();
            termsSemiJoinCast.addTerm("movie1.movie_id", "movie_cast.movie_id");
            NestedLoopSemiJoin joinCast = new NestedLoopSemiJoin(filterCast, scanMovieCast, termsSemiJoinCast);

            JoinPredicate termsSemiJoinCrew = new JoinPredicate();
            termsSemiJoinCrew.addTerm("movie2.movie_id", "movie_crew.movie_id");
            Operation joinCrew = new NestedLoopSemiJoin(filterCrew, scanMovieCrew, termsSemiJoinCrew);

            Exists exists = new Exists(joinCast, joinCrew, true);

            JoinPredicate termsFinalSemiJoin = new JoinPredicate();
            Operation finalSemiJoinCrew = new NestedLoopSemiJoin(scanMovie, exists, termsFinalSemiJoin);

            return finalSemiJoinCrew;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    
    public Operation createTest52(boolean hash) {
        try {
            Table movieTable = Directory.getTable(folder, "movie.dat", null, cacheSize, pageSize, false);
            Table movieCastTable = Directory.getTable(folder, "movieCast.dat", null, cacheSize, pageSize, false);
            Table movieCrewTable = Directory.getTable(folder, "movieCrew.dat", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person.dat", null, cacheSize, pageSize, false);
            //Table personTable1 = Directory.getTable(null, folder, "person1", pageSize, false);

            Operation scanMovie = new IndexScan("movie", movieTable);
            Operation scanMovie1 = new IndexScan("movie1", movieTable);
            Operation scanMovie2 = new IndexScan("movie2", movieTable);
            Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);
            Operation scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);

            if (hash) {
                scanMovieCast = new HashIndex(scanMovieCast);
                scanMovieCrew = new HashIndex(scanMovieCrew);
            }

            SingleColumnLookupFilter lookupCast = new SingleColumnLookupFilter(new ColumnElement("movie1.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
            Operation filterCast = new Filter(scanMovie1, lookupCast);

            SingleColumnLookupFilter lookupCrew = new SingleColumnLookupFilter(new ColumnElement("movie2.movie_id"), ComparisonTypes.EQUAL, new ReferencedElement("movie.movie_id"));
            Operation filterCrew = new Filter(scanMovie2, lookupCrew);

            JoinPredicate termsSemiJoinCast = new JoinPredicate();
            termsSemiJoinCast.addTerm("movie1.movie_id", "movie_cast.movie_id");
            NestedLoopSemiJoin joinCast = new NestedLoopSemiJoin(filterCast, scanMovieCast, termsSemiJoinCast);

            JoinPredicate termsSemiJoinCrew = new JoinPredicate();
            termsSemiJoinCrew.addTerm("movie2.movie_id", "movie_crew.movie_id");
            Operation joinCrew = new NestedLoopSemiJoin(filterCrew, scanMovieCrew, termsSemiJoinCrew);

            LogicalAnd exists = new LogicalAnd(joinCast, joinCrew);

            JoinPredicate termsFinalSemiJoin = new JoinPredicate();
            Operation finalSemiJoinCrew = new NestedLoopSemiJoin(scanMovie, exists, termsFinalSemiJoin);

            return finalSemiJoinCrew;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    
    public Operation createTest5(boolean hash) {
        try {
            Table movieCastTable = Directory.getTable(folder, "movieCast.dat", null, cacheSize, pageSize, false);
            //Table personTable1 = Directory.getTable(null, folder, "person1", pageSize, false);

            Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);

            if (hash) {
                scanMovieCast = new HashIndex(scanMovieCast);
            }

            
            SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter(new ColumnElement("movie_cast.cast_order"), ComparisonTypes.EQUAL, new LiteralElement(5));
            Condition condition1 = new Condition(singleFilter1);
            
            SingleColumnLookupFilter singleFilter2 = new SingleColumnLookupFilter(new ColumnElement("movie_cast.cast_order"), ComparisonTypes.EQUAL, new LiteralElement(2));
            Condition condition2 = new Condition(singleFilter2);
            
            SingleColumnLookupFilter singleFilter3 = new SingleColumnLookupFilter(new ColumnElement("movie_cast.movie_id"), ComparisonTypes.EQUAL, new LiteralElement(5));
            Condition condition3 = new Condition(singleFilter3);
            
            LogicalOr or = new LogicalOr(condition1, condition2);
            LogicalAnd and = new LogicalAnd(or, condition3);

            JoinPredicate termsFinalSemiJoin = new JoinPredicate();
            Operation finalSemiJoinCrew = new NestedLoopSemiJoin(scanMovieCast, and, termsFinalSemiJoin);

            return finalSemiJoinCrew;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest6(boolean hash) {
        try {
            Table movieCastIndexTable = Directory.getTable(folder, "movieCastIndex", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person", null, cacheSize, pageSize, false);
            //Table personTable1 = Directory.getTable(null, folder, "person1", pageSize, false);

            Operation scanPerson = new IndexScan("person", personTable);
            Operation scanMovieCastIndex = new IndexScan("movie_cast", movieCastIndexTable);

            if (hash) {
                scanPerson = new HashIndex(scanPerson);
            }

            Operation groupBy = new Aggregation(scanMovieCastIndex, "gr", "movie_cast", "person_id", "movie_cast", "movie_id", AggregationType.COUNT, true);

            JoinPredicate terms1 = new JoinPredicate();
            terms1.addTerm("gr.person_id", "person.person_id");
            Operation join1 = new HashInnerJoin(groupBy, scanPerson, terms1);
            SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter(new ColumnElement("person.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
            Operation filter = new Filter(join1, singleFilter1);
            return join1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest7(boolean hash) {
        try {
            Table movieTable = Directory.getTable(folder, "movie", null, cacheSize, pageSize, false);
            Table movieCastTable = Directory.getTable(folder, "movieCast", null, cacheSize, pageSize, false);
            Table movieCastIndexTable = Directory.getTable(folder, "movieCastIndex", null, cacheSize, pageSize, false);
            Table movieCrewTable = Directory.getTable(folder, "movieCrew", null, cacheSize, pageSize, false);
            Table movieCrewIndexTable = Directory.getTable(folder, "movieCrewIndex", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person", null, cacheSize, pageSize, false);
            //Table personTable1 = Directory.getTable(null, folder, "person1", pageSize, false);

            Operation scanMovie1 = new IndexScan("movie1", movieTable);
            Operation scanMovie2 = new IndexScan("movie2", movieTable);
            Operation scanMovieCastIndex = new IndexScan("movie_cast_index", movieCastIndexTable);
            Operation scanMovieCrewIndex = new IndexScan("movie_crew_index", movieCrewIndexTable);
            Operation scanPersonCast = new IndexScan("person_cast", personTable);
            Operation scanPersonCrew = new IndexScan("person_crew", personTable);

            if (hash) {
                scanMovie1 = new HashIndex(scanMovie1);
                scanMovie2 = new HashIndex(scanMovie2);
                //scanMovieCastIndex = new HashIndex(scanMovieCastIndex, "");
                //scanMovieCrewIndex = new HashIndex(scanMovieCrewIndex, "");
            }

            SingleColumnLookupFilter filter1 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
            Filter filterCast = new Filter(scanPersonCast, filter1);

            JoinPredicate terms1 = new JoinPredicate();
            terms1.addTerm("person_cast.person_id", "movie_cast_index.person_id");
            Operation join1 = new NestedLoopJoin(filterCast, scanMovieCastIndex, terms1);

            JoinPredicate terms2 = new JoinPredicate();
            terms2.addTerm("movie_cast_index.movie_id", "movie1.movie_id");
            Operation join2 = new NestedLoopJoin(join1, scanMovie1, terms2);

            Projection proj1 = new Projection(join2,  new String[]{"movie1.title"});

            SingleColumnLookupFilter filter2 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL, new LiteralElement("\"Brad Pitt\""));
            Filter filterCrew = new Filter(scanPersonCrew, filter2);

            JoinPredicate terms3 = new JoinPredicate();
            terms3.addTerm("person_crew.person_id", "movie_crew_index.person_id");
            Operation join3 = new NestedLoopJoin(filterCrew, scanMovieCrewIndex, terms3);

            JoinPredicate terms4 = new JoinPredicate();
            terms4.addTerm("movie_crew_index.movie_id", "movie2.movie_id");
            Operation join4 = new NestedLoopJoin(join3, scanMovie2, terms4);

            Projection proj2 = new Projection(join4,new String[]{"movie2.title"});

            Sort s1 = new Sort(proj1, "movie1.title", true);
            Sort s2 = new Sort(proj2, "movie2.title", true);
            Operation union = new Union(s1, s2);

            return union;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest7_old(boolean hash) {
        try {
            Table movieTable = Directory.getTable(folder, "movie", null, cacheSize, pageSize, false);
            Table movieCastTable = Directory.getTable(folder, "movieCast", null, cacheSize, pageSize, false);
            Table movieCrewTable = Directory.getTable(folder, "movieCrew", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person", null, cacheSize, pageSize, false);
            //Table personTable1 = Directory.getTable(null, folder, "person1", pageSize, false);

//        movieTable.flushDB();
//        movieCastTable.flushDB();
//        movieCrewTable.flushDB();
//        personTable.flushDB();
            Operation scanMovie = new IndexScan("movie", movieTable);
            Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);
            Operation scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
            Operation scanPersonCast = new IndexScan("person_cast", personTable);
            Operation scanPersonCrew = new IndexScan("person_crew", personTable);

            if (hash) {
                scanMovieCast = new HashIndex(scanMovieCast);
                scanMovieCrew = new HashIndex(scanMovieCrew);
                scanPersonCast = new HashIndex(scanPersonCast);
                scanPersonCrew = new HashIndex(scanPersonCrew);
            }

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Operation createTest8(boolean hash) {
        try {
            Table movieTable = Directory.getTable(folder, "movie", null, cacheSize, pageSize, false);
            Table movieCastTable = Directory.getTable(folder, "movieCast", null, cacheSize, pageSize, false);
            Table movieCrewTable = Directory.getTable(folder, "movieCrew", null, cacheSize, pageSize, false);
            Table personTable = Directory.getTable(folder, "person", null, cacheSize, pageSize, false);
            //Table personTable1 = Directory.getTable(null, folder, "person1", pageSize, false);

            Operation scanMovie = new IndexScan("movie", movieTable);
            Operation scanMovieCast = new IndexScan("movie_cast", movieCastTable);
            Operation scanMovieCrew = new IndexScan("movie_crew", movieCrewTable);
            Operation scanPersonCast = new IndexScan("person_cast", personTable);
            Operation scanPersonCrew = new IndexScan("person_crew", personTable);

            if (hash) {
                scanPersonCast = new HashIndex(scanPersonCast);
                scanPersonCrew = new HashIndex(scanPersonCrew);
            }

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
            CrossJoinOld join3 = new CrossJoinOld(join1, join2);
            //NestedLoopJoin join3 = new NestedLoopJoin(filterCrew, filterCast, terms);

            //SingleColumnLookupFilter filterMovie_ = new SingleColumnLookupFilter("movie.title", ComparisonTypes.GREATER_EQUAL_THAN, "X");
            //Filter filterMovie = new Filter(scanMovie, filterMovie_);
            NestedLoopJoin join4 = new NestedLoopJoin(scanMovie, join3, terms);

            //TwoColumnsLookupFilter filter1 = new TwoColumnsLookupFilter("person_crew.person_name", "person_cast.person_name", ComparisonTypes.EQUAL);
//            CompositeLookupFilter filter1 = new CompositeLookupFilter(CompositeLookupFilter.OR);
//
//            SingleColumnLookupFilter singleFilter1 = new SingleColumnLookupFilter("person_crew.person_name", ComparisonTypes.EQUAL, "\"Brad Pitt\"");
//            SingleColumnLookupFilter singleFilter2 = new SingleColumnLookupFilter("person_cast.person_name", ComparisonTypes.EQUAL, "\"Brad Pitt\"");
//            filter1.addFilter(singleFilter1);
//            filter1.addFilter(singleFilter2);
//
//            Filter filter = new Filter(join4, filter1);

            return join4;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public void evaluate(boolean hash, boolean all, int queryNumber, int times) throws Exception {
        System.out.println("number of executions: " + times);
        System.out.println("cache size: " + cacheSize + " bytes");
        System.out.println("Using hash: " + hash);
        System.out.println("");
        if (all) {
            System.out.println("Running all queries");
            evaluateQuery(times, "Query 1", () -> createTest1(hash));
            evaluateQuery(times, "Query 2", () -> createTest2(hash));
            evaluateQuery(times, "Query 3", () -> createTest3(hash));
            evaluateQuery(times, "Query 4", () -> createTest4(hash));
            evaluateQuery(times, "Query 5", () -> createTest5(hash));
            evaluateQuery(times, "Query 6", () -> createTest6(hash));
            evaluateQuery(times, "Query 7", () -> createTest7(hash));
            evaluateQuery(times, "Query 8", () -> createTest8(hash));
        } else {
            switch (queryNumber) {
                case 1 ->
                    evaluateQuery(times, "Query 1", () -> createTest1(hash));
                case 2 ->
                    evaluateQuery(times, "Query 2", () -> createTest2(hash));
                case 3 ->
                    evaluateQuery(times, "Query 3", () -> createTest3(hash));
                case 4 ->
                    evaluateQuery(times, "Query 4", () -> createTest4(hash));
                case 5 ->
                    evaluateQuery(times, "Query 5", () -> createTest5(hash));
                case 6 ->
                    evaluateQuery(times, "Query 6", () -> createTest6(hash));
                case 7 ->
                    evaluateQuery(times, "Query 7", () -> createTest7(hash));
                case 8 ->
                    evaluateQuery(times, "Query 8", () -> createTest8(hash));
            }
        }
    }

    public void run(int queryNumber, int tuplesToRead, boolean hash) throws Exception {
        System.out.println("Use hash: " + hash);
        System.out.println("Running query " + queryNumber);
        switch (queryNumber) {
            case 1 ->
                runQuery(createTest1(hash), tuplesToRead);
            case 2 ->
                runQuery(createTest2(hash), tuplesToRead);
            case 3 ->
                runQuery(createTest3(hash), tuplesToRead);
            case 4 ->
                runQuery(createTest4(hash), tuplesToRead);
            case 5 ->
                runQuery(createTest5(hash), tuplesToRead);
            case 6 ->
                runQuery(createTest6(hash), tuplesToRead);
            case 7 ->
                runQuery(createTest7(hash), tuplesToRead);
            case 8 ->
                runQuery(createTest8(hash), tuplesToRead);
        }
    }

    public void view(int queryNumber, boolean hash) throws Exception {
        {
            System.out.println("Execution plan for query " + queryNumber);
            if (hash) {
                System.out.println("The plan may contain the hash operator if deemed necessary");
            } else {
                System.out.println("The plan does not contain the hash operator");
            }

            System.out.println("");
            TreePrinter printer = new TreePrinter();
            switch (queryNumber) {
                case 1 ->
                    printer.printTree(createTest1(hash));
                case 2 ->
                    printer.printTree(createTest2(hash));
                case 3 ->
                    printer.printTree(createTest3(hash));
                case 4 ->
                    printer.printTree(createTest4(hash));
                case 5 ->
                    printer.printTree(createTest5(hash));
                case 6 ->
                    printer.printTree(createTest6(hash));
                case 7 ->
                    printer.printTree(createTest7(hash));
                case 8 ->
                    printer.printTree(createTest8(hash));
            }
        }
    }

    public void runQuery(Operation query, int tuplesToRead) throws Exception {
        TuplesPrinter printer = new TuplesPrinter();
        printer.execQueryAndPrint(query, tuplesToRead);
    }

    public void execQuery(Operation query) throws Exception {
        Iterator<Tuple> tuples = query.run();
        while (tuples.hasNext()) {
            tuples.next();
        }
    }

    public void evaluateQuery(int times, String queryID, Supplier<Operation> supplier) throws Exception {
        List<Operation> warmUpQueries = new ArrayList();
        List<Operation> testQueries = new ArrayList();
        for (int i = 0; i < times; i++) {
            Operation warmUpQuery = supplier.get();
            warmUpQueries.add(warmUpQuery);
            Operation testQuery = supplier.get();
            testQueries.add(testQuery);
        }

        //Operation query = main.createTest1();
        System.out.println("Exec " + queryID);

        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            execQuery(warmUpQueries.get(i));
        }
        long end = System.currentTimeMillis();
        System.out.println("warm up time: " + (end - start) / times);

        Stats.passes = 0;
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            execQuery(testQueries.get(i));
        }
        end = System.currentTimeMillis();
        System.out.println("measured time: " + (end - start) / times);
        //System.out.println("passes " + Stats.passes);
        System.out.println("-----------------------");
    }

    public void testMemory() throws Exception {
//        Table movieTable = Directory.getTable(folder, "movie", null, cacheSize, pageSize, false);
//        Operation scanMovie = new IndexScan("movie", movieTable);
//        scanMovie.run();
        execQuery(createTest1(false));
        try {
            // Sleep for 50 milliseconds
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args)  {

        SBBD main = new SBBD();

        int times = 10;
        boolean hash = false;

        String action = "NONE";

        //args = new String[]{"EVAL", "8", "10","no_HASH"};
        args = new String[]{"RUN", "5", "ALL_ROWS","NO_HASH"};
        //args = new String[]{"VIEW", "5", "no_HASH"};
        //args = new String[]{"CREATE"};
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toUpperCase();
        }

        //System.out.println(Arrays.toString(args));
        //args = new String[]{"CREATE"};
        if (args.length < 1 || !(args[0].equals("CREATE") || args[0].equals("RUN")
                || args[0].equals("EVAL") || args[0].equals("VIEW"))) {
            System.out.println("usage: ");
            System.out.println("  - To create the database:  CREATE [4KB | 8KB | 16KB]");
            System.out.println("  - To evaluate queries: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
            System.out.println("  - To run a query:  RUN <query_number> (<rows_amount> | ALL_ROWS> [HASH | NO_HASH]");
            System.out.println("  - TO view a query tree:  VIEW <query_number> [HASH | NO_HASH]");
            System.out.println("");
            System.out.println("If using for the first time, start creating the database.");
            System.exit(1);
        }

        boolean all = false;
        int queryNumber = 0;
        int tuplesToRead = -1;
        String arg = args[0];
        if (arg.equals("CREATE")) {
            action = "CREATE";
            if (args.length > 1) {
                arg = args[1];
            } else {
                arg = "4KB";
            }
            switch (arg) {
                case "4KB" ->
                    pageSize = 4096;
                case "8KB" ->
                    pageSize = 8192;
                case "16KB" ->
                    pageSize = 16384;
                default -> {
                    System.out.println("Invalid page size: Use 4KB, 8KB or 16KB");
                    System.exit(1);
                }

            }

        } else if (arg.equals("EVAL")) {
            action = "EVAL";
            if (args.length < 3) {
                System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
                System.out.println("Invalid number of arguments. At least the query number and the executions amount must be provided.");
                System.exit(times);
            }
            arg = args[1];
            if (arg.equals("ALL_QUERIES")) {
                all = true;
            } else {
                try {
                    queryNumber = Integer.parseInt(arg);
                } catch (Exception e) {
                    System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
                    System.out.println("The query_number must be between 1 and 8 or ALL_QUERIES to evaluate all queries.");
                    System.exit(1);
                }
                if (queryNumber <= 0 || queryNumber > 8) {
                    System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
                    System.out.println("The query_number must be between 1 and 8 or ALL_QUERIES to evaluate all queries.");
                    System.exit(1);
                }
            }
            arg = args[2];
            try {
                times = Integer.parseInt(arg);
            } catch (Exception e) {
                System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
                System.out.println("The executions_amount must be a number between 1 and 50.");
                System.exit(1);
            }
            if (times < 1 || times > 50) {
                System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
                System.out.println("The executions_amount must be a number between 1 and 50.");
                System.exit(1);
            }
            if (args.length > 3) {
                arg = args[3];
            } else {
                arg = "NO_HASH";
            }

            if (arg.equals("HASH")) {
                hash = true;
            } else if (arg.equals("NO_HASH")) {
                hash = false;
            } else {
                System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
                System.out.println("Use HASH or NO_HASH");
                System.exit(1);
            }

//            if (args.length > 4) {
//                arg = args[4];
//            } else {
//                arg = "99999999";
//            }
//
//            try {
//                cacheSize = Integer.parseInt(arg);
//            } catch (Exception e) {
//                System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
//                System.out.println("If provided, the cache_size (in bytes) must be at least 16384.");
//                System.exit(1);
//            }
//            if (cacheSize < 16384) {
//                System.out.println("Usage: EVAL (<query_number> | ALL_QUERIES) <executions_amount> [HASH | NO_HASH]");
//                System.out.println("If provided, the cache_size (in bytes) must be at least 16384.");
//                System.exit(1);
//            }

        } else if (arg.equals("RUN")) {
            action = "RUN";
            if (args.length < 3) {
                System.out.println("Usage:  RUN <query_number> (<rows_amount> | ALL_ROWS> [HASH | NO_HASH]");
                System.out.println("At least the query number and the rows amount need to be informed");
                System.exit(1);
            }
            arg = args[1];

            try {
                queryNumber = Integer.parseInt(arg);
            } catch (Exception e) {
                System.out.println("Usage:  RUN <query_number> (<rows_amount> | ALL_ROWS> [HASH | NO_HASH]");
                System.out.println("The query_number must be between 1 and 8.");
                System.exit(1);
            }
            if (queryNumber <= 0 || queryNumber > 8) {
                System.out.println("Usage:  RUN <query_number> (<rows_amount> | ALL_ROWS> [HASH | NO_HASH]");
                System.out.println("The query_number must be between 1 and 8");
                System.exit(1);
            }

            arg = args[2];
            if (arg.equals("ALL_ROWS")) {

            } else {
                try {
                    tuplesToRead = Integer.parseInt(arg);
                } catch (Exception e) {
                    System.out.println("Usage:  RUN <query_number> (<rows_amount> | ALL_ROWS> [HASH | NO_HASH]");
                    System.out.println("The rows_amount must a number greater than 0 or the flag ALL_ROWS to return all rows.");
                    System.exit(1);
                }
                if (tuplesToRead < 1) {
                    System.out.println("Usage:  RUN <query_number> (<rows_amount> | ALL_ROWS> [HASH | NO_HASH]");
                    System.out.println("The rows_amount must a number greater than 0 or the flag ALL_ROWS to return all rows.");
                    System.exit(1);
                }
            }
            if (args.length > 3) {
                arg = args[3];
            } else {
                arg = "NO_HASH";
            }

            if (arg.equals("HASH")) {
                hash = true;
            } else if (arg.equals("NO_HASH")) {
                hash = false;
            } else {
                System.out.println("Usage:  RUN <query_number> (<rows_amount> | ALL_ROWS> [HASH | NO_HASH]");
                System.out.println("Use HASH or NO_HASH");
                System.exit(1);
            }

        } else if (arg.equals("VIEW")) {
            action = "VIEW";
            if (args.length < 2) {
                System.out.println("Usage: VIEW <query_number>");
                System.exit(1);
            }
            arg = args[1];

            try {
                queryNumber = Integer.parseInt(arg);
            } catch (Exception e) {
                System.out.println("VIEW <query_number> [HASH | NO_HASH]");
                System.out.println("The query_number  must be between 1 and 8.");
                System.exit(1);
            }
            if (queryNumber <= 0 || queryNumber > 8) {
                System.out.println("VIEW <query_number> [HASH | NO_HASH]");
                System.out.println("The query_number must be between 1 and 8.");
                System.exit(1);
            }
            if (args.length > 2) {
                arg = args[2];
            } else {
                arg = "NO_HASH";
            }
            if (arg.equals("HASH")) {
                hash = true;
            } else if (arg.equals("NO_HASH")) {
                hash = false;
            } else {
                System.out.println("VIEW <query_number> [HASH | NO_HASH]");
                System.out.println("Use HASH or NO_HASH");
                System.exit(1);
            }
        }

        try{
        if (action.equals("CREATE")) {
            main.createAllTables();
        } else if (action.equals("EVAL")) {
            main.evaluate(hash, all, queryNumber, times);
        } else if (action.equals("RUN")) {
            main.run(queryNumber, tuplesToRead, hash);
        } else if (action.equals("VIEW")) {
            main.view(queryNumber, hash);
        }
        }
        catch(Exception e){
            e.printStackTrace();
        }
//        for (int i = 0; i < 400; i++) {
//            System.out.println("i = " + i);
//            main.testMemory();
//        }
//
//        while (true) {
//        }

    }
}
