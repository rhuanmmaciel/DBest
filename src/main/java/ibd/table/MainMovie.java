/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.sourceop.IndexScan;
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
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 *
 * @author Sergio
 */
public class MainMovie {

    public void addMovies(Table table, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                System.out.println(count);
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
                System.out.println(count);
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
                System.out.println(count);
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
                System.out.println(count);
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

    
    /**
     * Print information of all rows from the table
     *
     * @param table the target table
     * @throws Exception
     */
    public void getAll(Table table) throws Exception {
        List<LinkedDataRow> list = table.getAllRecords();
        for (DataRow rowData : list) {
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
        pt.addColumn(new StringColumn("title"));
        return pt;
    }
    
    public Prototype createPersonSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("person_id", true));
        pt.addColumn(new StringColumn("person_name", (short)80));
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
    
    public Prototype createMovieCrewSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("movie_id", true));
        pt.addColumn(new IntegerColumn("person_id", true));
        pt.addColumn(new IntegerColumn("department_id"));
        pt.addColumn(new StringColumn("job"));
        return pt;
    }
    

    public Operation createQuery() throws Exception{
        Table movieTable = Directory.getTable("c:\\teste\\ibd", "movie",null, 99999,  16384, false);
        Table movieCastTable = Directory.getTable("c:\\teste\\ibd", "movieCast",null, 99999,  16384, false);
        Table movieCrewTable = Directory.getTable("c:\\teste\\ibd", "movieCrew",null, 99999,  16384, false);
        Table personTable = Directory.getTable("c:\\teste\\ibd", "person",null, 99999,  16384, false);
        //Table personTable1 = Directory.getTable(null, "c:\\teste\\ibd", "person1", 16384, false);
        
        
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
        
        SingleColumnLookupFilter filter1 = new SingleColumnLookupFilter(new ColumnElement("person_crew.person_name"), ComparisonTypes.EQUAL,new ReferencedElement("\"Brad Pitt\""));
        SingleColumnLookupFilter filter2 = new SingleColumnLookupFilter(new ColumnElement("person_cast.person_name"),  ComparisonTypes.EQUAL,new ReferencedElement("\"Brad Pitt\""));
        compositeFilter.addFilter(filter1);
        compositeFilter.addFilter(filter2);
        
        
        Filter filter = new Filter(join4,  compositeFilter);
        //3718093
        
        return join2;
    }
    
    public static void main(String[] args) throws Exception {

        MainMovie main = new MainMovie();

        Prototype movieSchema = main.createMovieSchema();
        Prototype movieCastSchema = main.createMovieCastSchema();
        Prototype movieCrewSchema = main.createMovieCrewSchema();
        Prototype personSchema = main.createPersonSchema();

        
//        Table movieTable = Directory.getTable(movieSchema, "c:\\teste\\ibd", "movie", 16384, true);
//        main.addMovies(movieTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movie.csv");
//        movieTable.flushDB();

        //Table movieCastTable = Directory.getTable(movieCastSchema, "c:\\teste\\ibd", "movieCast", 16384, true);
        //main.addMovieCasts(movieCastTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movie_cast.csv");
        //movieCastTable.flushDB();
        
        //Table movieCrewTable = Directory.getTable(movieCrewSchema, "c:\\teste\\ibd", "movieCrew", 16384, true);
        //main.addMovieCrew(movieCrewTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\movie_crew.csv");
        //movieCrewTable.flushDB();

//        Table personTable = Directory.getTable(personSchema, "c:\\teste\\ibd", "person", 16384, true);
//        main.addPerson(personTable, "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\person.csv");
//        personTable.flushDB();

        //main.getAll(movieTable);
        
        Operation query = main.createQuery();
        Iterator<Tuple> tuples = query.run();
        int count = 0;
        while (tuples.hasNext()){
            Tuple tuple = tuples.next();
            //System.out.println(tuple);
            count++;
            if (count>100)
                break;
        }
        System.out.println("count "+count);
        
        
    }

}
