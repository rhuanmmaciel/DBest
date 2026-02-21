/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.optimizer.join;

import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author ferna
 */
public class DataLoader {

    Table tableMovie = null;
    Table tableArtist = null;
    Table tableCast = null;
    Table tableDirector = null;
    public int cacheSize = 0;

     public void loadData() throws Exception {
        tableMovie = Directory.getTable("c:\\teste\\ibd", "movieX", null, cacheSize,Table.DEFULT_PAGE_SIZE, false);
        tableArtist = Directory.getTable("c:\\teste\\ibd", "artist", null, cacheSize, Table.DEFULT_PAGE_SIZE, false);
        tableCast = Directory.getTable("c:\\teste\\ibd", "cast", null, cacheSize, Table.DEFULT_PAGE_SIZE, false);
        tableDirector = Directory.getTable("c:\\teste\\ibd", "director", null, cacheSize, Table.DEFULT_PAGE_SIZE, false);

    }

    public void recreate() throws Exception {
        tableMovie = Directory.getTable(  "c:\\teste\\ibd", "movieX",createMovieSchema(),cacheSize, Table.DEFULT_PAGE_SIZE, true);
        tableArtist = Directory.getTable( "c:\\teste\\ibd", "artist",createArtistSchema(),cacheSize, Table.DEFULT_PAGE_SIZE, true);
        tableCast = Directory.getTable( "c:\\teste\\ibd", "cast",createCastSchema(),cacheSize, Table.DEFULT_PAGE_SIZE, true);
        tableDirector = Directory.getTable( "c:\\teste\\ibd", "director",createDirectorSchema(),cacheSize, Table.DEFULT_PAGE_SIZE, true);

        addDirector(tableDirector, 1, "Steven Spielberg");
        addDirector(tableDirector, 2, "Christopher Nolan");
        addDirector(tableDirector, 3, "Martin Scorcese");
        addDirector(tableDirector, 4, "James Cameron");
        tableDirector.flushDB();

        addArtist(tableArtist, 1, "Leonardo DiCaprio");
        addArtist(tableArtist, 2, "Christian Bale");
        addArtist(tableArtist, 3, "Tom Hanks");
        addArtist(tableArtist, 4, "Tom Cruise");
        addArtist(tableArtist, 5, "Anne Hathaway");
        addArtist(tableArtist, 6, "Alec Baldwin");
        addArtist(tableArtist, 7, "Billy Zane");
        addArtist(tableArtist, 8, "Colin Farrell");
        addArtist(tableArtist, 9, "Catherine Zeta-Jones");
        addArtist(tableArtist, 10, "Cate Blanchett");
        addArtist(tableArtist, 11, "Dakota Fanning");
        tableArtist.flushDB();

        addMovie(tableMovie, 1, "Guerra dos Mundos", 2005, 1);
        addMovie(tableMovie, 2, "Minority Report", 2002, 1);
        addMovie(tableMovie, 3, "O Terminal", 2004, 1);
        addMovie(tableMovie, 4, "Interstelar", 2014, 2);
        addMovie(tableMovie, 5, "Batman Begins", 2005, 2);
        addMovie(tableMovie, 8, "O Lobo de Wall Street", 2013, 3);
        tableMovie.flushDB();

        addCast(tableCast, 1, 4);
        addCast(tableCast, 2, 4);
        addCast(tableCast, 2, 8);
        addCast(tableCast, 3, 3);
        addCast(tableCast, 3, 9);
        addCast(tableCast, 4, 5);
        addCast(tableCast, 5, 2);
        addCast(tableCast, 8, 1);
        tableCast.flushDB();

    }
    
    public Table getMovieTable(){
        return tableMovie;
    }
    
    public Table getDirectorTable(){
        return tableDirector;
    }
    
    public Table getCastTable(){
        return tableCast;
    }
    
    public Table getArtistTable(){
        return tableArtist;
    }
    
    
    private Prototype createMovieSchema() {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("idMovie", true));
        prototype.addColumn(new StringColumn("title"));
        prototype.addColumn(new IntegerColumn("year"));
        prototype.addColumn(new IntegerColumn("idDirector"));
        return prototype;
    }

    private Prototype createArtistSchema() {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("idArtist", true));
        prototype.addColumn(new StringColumn("name"));
        return prototype;
    }

    private Prototype createCastSchema() {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("idMovie", true));
        prototype.addColumn(new IntegerColumn("idArtist", true));
        return prototype;
    }

    private Prototype createDirectorSchema() {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("idDirector", true));
        prototype.addColumn(new StringColumn("name"));
        return prototype;
    }

    private void addMovie(Table table, int idMovie, String title, int year, int idDirector) throws Exception {
        BasicDataRow dataRow = new BasicDataRow();
        dataRow.setInt("idMovie", idMovie);
        dataRow.setInt("year", year);
        dataRow.setString("title", title);
        dataRow.setInt("idDirector", idDirector);
        table.addRecord(dataRow, true);
    }

    private void addArtist(Table table, int idArtist, String name) throws Exception {
        BasicDataRow dataRow = new BasicDataRow();
        dataRow.setInt("idArtist", idArtist);
        dataRow.setString("name", name);
        table.addRecord(dataRow, true);
    }

    private void addCast(Table table, int idMovie, int idArtist) throws Exception {
        BasicDataRow dataRow = new BasicDataRow();
        dataRow.setInt("idMovie", idMovie);
        dataRow.setInt("idArtist", idArtist);
        table.addRecord(dataRow, true);
    }

    private void addDirector(Table table, int idDirector, String name) throws Exception {
        BasicDataRow dataRow = new BasicDataRow();
        dataRow.setInt("idDirector", idDirector);
        dataRow.setString("name", name);
        table.addRecord(dataRow, true);
    }

   
}
