/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block.management;

import ibd.table.DataFaker;
import ibd.table.Directory;
import ibd.table.Params;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author Sergio
 */
public class Main {

    private void resetMetrics() {
        Params.RECORDS_ADDED = 0;
        Params.RECORDS_REMOVED = 0;
        Params.BLOCKS_LOADED = 0;
        Params.BLOCKS_SAVED = 0;
    }

    private void printMetrics(long time) {
        //System.out.println("records added during reorganization " + Params.RECORDS_ADDED);
        //System.out.println("records removed during reorganization " + Params.RECORDS_REMOVED);
        System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
        System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
        System.out.println("time : " + time);
    }

    private Integer[] createLongArray(int offset, int amount, boolean ordered) {
        Integer[] array = new Integer[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + offset;
        }

        if (!ordered) {
            DataFaker.shuffleArray(array);
        }
        return array;
    }

    public void testMultipleInsertions(Table table, int offset, int amount, boolean ordered, boolean display, int len, boolean update) throws Exception {

        //long[] array = new long[]{735,453,1,406,288,469,366,198,202,797,953,337,176,376,132,360,114,312,439,398,772,655,242,578,401,993,130,814,994,213,851,510,734,857,877,155,889,129,607,593,643,784,23,83,441,335,969,869,696,308,445,448,345,7,950,632,418,131,84,737,462,73,758,672,816,393,458,821,631,442,704,497,39,703,411,6,932,699,792,788,457,319,387,839,490,487,56,501,485,275,320,158,103,767,790,840,58,750,292,156,187,507,700,754,965,708,414,3,212,830,346,619,496,154,2,785,225,177,942,777,859,402,681,729,988,550,394,595,843,828,429,357,55,960,303,987,450,135,415,222,69,298,522,259,813,46,518,739,862,153,477,325,972,291,731,28,44,434,143,268,815,500,779,895,532,494,829,771,822,575,377,261,916,343,702,428,471,512,913,235,417,334,200,623,978,186,751,636,680,848,765,576,358,539,306,484,894,800,139,545,601,5,32,778,218,389,768,695,605,431,531,674,716,995,910,427,119,173,371,984,659,671,466,568,164,665,407,569,908,690,96,444,645,991,627,304,590,420,274,295,933,384,571,120,626,493,165,640,160,799,255,356,197,915,899,133,0,622,660,567,621,67,766,117,451,88,205,363,732,876,100,54,635,102,92,685,613,299,727,638,82,959,986,426,438,856,127,673,926,361,649,719,13,964,841,936,30,191,615,559,396,783,38,924,761,892,883,247,714,449,720,867,975,976,323,49,585,711,364,35,495,589,762,324,52,565,271,573,300,934,416,159,309,515,220,174,252,553,43,142,992,823,509,207,580,498,786,644,946,217,33,315,413,381,912,886,706,452,807,63,774,541,162,517,178,348,355,379,151,400,244,920,260,760,873,108,301,461,637,125,625,239,686,106,618,386,513,455,642,136,999,140,338,982,121,276,723,171,557,79,801,998,549,302,18,661,562,538,579,653,925,41,624,24,921,317,412,914,211,938,424,794,741,167,97,294,519,105,842,838,419,524,91,633,210,272,614,19,258,170,78,375,834,620,812,609,66,951,879,138,254,907,146,267,827,321,369,196,970,656,465,296,64,989,855,548,663,904,430,611,488,216,440,820,931,163,408,759,909,262,694,736,971,36,903,956,533,870,852,968,283,42,952,628,905,328,470,564,53,885,534,948,318,20,744,911,997,678,93,152,817,770,104,486,374,937,837,397,793,954,584,602,833,314,388,654,390,111,717,850,966,582,240,563,676,733,14,474,273,77,256,215,825,489,134,234,279,776,194,537,504,89,192,330,433,209,378,137,639,977,339,697,75,316,882,199,311,530,47,740,724,887,277,871,521,983,930,76,241,257,826,670,432,570,943,940,190,148,606,94,464,329,658,181,107,21,492,57,979,116,342,923,802,368,798,182,746,284,906,206,835,80,698,858,709,675,467,382,594,572,141,245,646,748,612,468,677,226,898,753,629,367,341,289,769,604,265,34,804,536,514,253,95,542,22,193,118,290,667,586,781,725,447,603,684,722,896,250,27,775,395,399,29,525,511,98,230,362,349,996,527,682,561,664,472,214,927,297,546,233,195,935,238,849,506,808,865,287,878,229,9,4,763,425,810,647,902,560,591,552,796,281,809,172,738,25,243,651,780,31,721,824,189,713,236,359,945,392,860,266,333,819,86,223,973,845,554,385,535,479,503,592,340,730,540,70,113,587,963,17,919,683,166,928,836,219,853,482,71,481,831,574,949,728,437,917,616,149,10,491,145,354,110,922,307,456,610,373,600,246,688,62,985,264,791,961,365,669,248,726,435,157,650,48,90,947,811,72,204,847,597,747,391,344,666,687,124,558,436,351,897,403,547,803,757,890,880,350,499,782,322,112,508,422,409,50,179,617,188,15,868,596,293,61,630,795,459,941,224,476,336,101,974,875,641,249,588,463,764,707,715,691,332,109,169,280,231,51,147,566,773,144,313,832,668,40,718,806,749,443,150,81,168,577,282,327,652,460,787,278,263,410,980,405,634,454,372,529,185,326,608,893,523,755,383,818,12,45,208,854,689,657,929,958,183,99,990,227,201,60,543,752,981,59,285,891,305,232,544,251,874,228,16,352,805,421,128,900,74,864,846,861,286,955,161,203,65,712,743,881,756,502,692,710,270,662,745,520,475,844,888,789,944,310,221,331,123,423,347,598,679,505,967,473,122,863,37,269,872,705,901,87,115,939,446,693,26,648,478,701,599,480,581,742,175,11,404,583,370,551,184,483,180,884,380,556,85,957,516,237,866,68,126,8,526,918,962,528,555,353};
        resetMetrics();
        System.out.println("inserting....");
        Integer[] array = createLongArray(offset, amount, ordered);

        long start = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            if (display) {
                System.out.println(i + "=>adding primary key =  " + array[i]);
            }
            String text = "Novo registros " + array[i];
            BasicDataRow rowData = new BasicDataRow();
            rowData.setInt("id", array[i]);
            rowData.setString("nome", DataFaker.pad(text, len));
            rowData.setString("bla", DataFaker.pad(text, len));

            table.addRecord(rowData, true);

        }

        table.flushDB();

        if (update) {

            for (int i = 0; i < array.length; i++) {
                if (display) {
                    System.out.println(i + "=>updating primary key =  " + array[i]);
                }
                String text = "Novo registros " + array[i];
                BasicDataRow rowData = new BasicDataRow();
                rowData.setInt("id", array[i]);
                rowData.setString("bla", "xxxx" + array[i]);

                table.updateRecord(rowData);

            }

            table.flushDB();
        }
        long end = System.currentTimeMillis();

        printMetrics((end - start));

    }

    public void testMultipleRemotions(Table table, int offset, int amount, boolean ordered, boolean display) throws Exception {

        System.out.println("removing....");
        resetMetrics();
        //long[] array = new long[]{735,453,1,406,288,469,366,198,202,797,953,337,176,376,132,360,114,312,439,398,772,655,242,578,401,993,130,814,994,213,851,510,734,857,877,155,889,129,607,593,643,784,23,83,441,335,969,869,696,308,445,448,345,7,950,632,418,131,84,737,462,73,758,672,816,393,458,821,631,442,704,497,39,703,411,6,932,699,792,788,457,319,387,839,490,487,56,501,485,275,320,158,103,767,790,840,58,750,292,156,187,507,700,754,965,708,414,3,212,830,346,619,496,154,2,785,225,177,942,777,859,402,681,729,988,550,394,595,843,828,429,357,55,960,303,987,450,135,415,222,69,298,522,259,813,46,518,739,862,153,477,325,972,291,731,28,44,434,143,268,815,500,779,895,532,494,829,771,822,575,377,261,916,343,702,428,471,512,913,235,417,334,200,623,978,186,751,636,680,848,765,576,358,539,306,484,894,800,139,545,601,5,32,778,218,389,768,695,605,431,531,674,716,995,910,427,119,173,371,984,659,671,466,568,164,665,407,569,908,690,96,444,645,991,627,304,590,420,274,295,933,384,571,120,626,493,165,640,160,799,255,356,197,915,899,133,0,622,660,567,621,67,766,117,451,88,205,363,732,876,100,54,635,102,92,685,613,299,727,638,82,959,986,426,438,856,127,673,926,361,649,719,13,964,841,936,30,191,615,559,396,783,38,924,761,892,883,247,714,449,720,867,975,976,323,49,585,711,364,35,495,589,762,324,52,565,271,573,300,934,416,159,309,515,220,174,252,553,43,142,992,823,509,207,580,498,786,644,946,217,33,315,413,381,912,886,706,452,807,63,774,541,162,517,178,348,355,379,151,400,244,920,260,760,873,108,301,461,637,125,625,239,686,106,618,386,513,455,642,136,999,140,338,982,121,276,723,171,557,79,801,998,549,302,18,661,562,538,579,653,925,41,624,24,921,317,412,914,211,938,424,794,741,167,97,294,519,105,842,838,419,524,91,633,210,272,614,19,258,170,78,375,834,620,812,609,66,951,879,138,254,907,146,267,827,321,369,196,970,656,465,296,64,989,855,548,663,904,430,611,488,216,440,820,931,163,408,759,909,262,694,736,971,36,903,956,533,870,852,968,283,42,952,628,905,328,470,564,53,885,534,948,318,20,744,911,997,678,93,152,817,770,104,486,374,937,837,397,793,954,584,602,833,314,388,654,390,111,717,850,966,582,240,563,676,733,14,474,273,77,256,215,825,489,134,234,279,776,194,537,504,89,192,330,433,209,378,137,639,977,339,697,75,316,882,199,311,530,47,740,724,887,277,871,521,983,930,76,241,257,826,670,432,570,943,940,190,148,606,94,464,329,658,181,107,21,492,57,979,116,342,923,802,368,798,182,746,284,906,206,835,80,698,858,709,675,467,382,594,572,141,245,646,748,612,468,677,226,898,753,629,367,341,289,769,604,265,34,804,536,514,253,95,542,22,193,118,290,667,586,781,725,447,603,684,722,896,250,27,775,395,399,29,525,511,98,230,362,349,996,527,682,561,664,472,214,927,297,546,233,195,935,238,849,506,808,865,287,878,229,9,4,763,425,810,647,902,560,591,552,796,281,809,172,738,25,243,651,780,31,721,824,189,713,236,359,945,392,860,266,333,819,86,223,973,845,554,385,535,479,503,592,340,730,540,70,113,587,963,17,919,683,166,928,836,219,853,482,71,481,831,574,949,728,437,917,616,149,10,491,145,354,110,922,307,456,610,373,600,246,688,62,985,264,791,961,365,669,248,726,435,157,650,48,90,947,811,72,204,847,597,747,391,344,666,687,124,558,436,351,897,403,547,803,757,890,880,350,499,782,322,112,508,422,409,50,179,617,188,15,868,596,293,61,630,795,459,941,224,476,336,101,974,875,641,249,588,463,764,707,715,691,332,109,169,280,231,51,147,566,773,144,313,832,668,40,718,806,749,443,150,81,168,577,282,327,652,460,787,278,263,410,980,405,634,454,372,529,185,326,608,893,523,755,383,818,12,45,208,854,689,657,929,958,183,99,990,227,201,60,543,752,981,59,285,891,305,232,544,251,874,228,16,352,805,421,128,900,74,864,846,861,286,955,161,203,65,712,743,881,756,502,692,710,270,662,745,520,475,844,888,789,944,310,221,331,123,423,347,598,679,505,967,473,122,863,37,269,872,705,901,87,115,939,446,693,26,648,478,701,599,480,581,742,175,11,404,583,370,551,184,483,180,884,380,556,85,957,516,237,866,68,126,8,526,918,962,528,555,353};
        Integer[] array = createLongArray(offset, amount, ordered);
        //long[] array = new long[]{0,9,1,2,3,4,5,6,7,8};

        long start = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            if (display) {
                System.out.println("removing primary key =  " + array[i]);
            }
            BasicDataRow rowData = new BasicDataRow();
            rowData.setInt("id", array[i]);
            table.removeRecord(rowData);

        }

        table.flushDB();
        long end = System.currentTimeMillis();
        printMetrics((end - start));
    }

    public void testMultipleSearches(Table table, int offset, int amount, boolean ordered, boolean display) throws Exception {

        System.out.println("searching....");
        resetMetrics();
        Integer[] array = createLongArray(offset, amount, ordered);

        long start = System.currentTimeMillis();

        for (int i = 0; i < array.length; i++) {
            BasicDataRow rowData = new BasicDataRow();
            rowData.setInt("id", array[i]);
            DataRow rec = table.getRecord(rowData);

            if (rec != null) {
                if (!display) {
                    continue;
                }

                System.out.println(rec.toString() + " in block " + rec.getBlockId());
            } else {
                System.out.println("erro: inexistente");
            }
        }
        long end = System.currentTimeMillis();
        printMetrics((end - start));
    }

    public void testMultipleSearches(Table table, boolean display) throws Exception {

        System.out.println("searching....");
        resetMetrics();

        long start = System.currentTimeMillis();

        Iterator<LinkedDataRow> records = table.getAllRecords().iterator();
        while (records.hasNext()) {
            DataRow rec = records.next();

            if (!display) {
                continue;
            }

            System.out.println(rec.toString().trim() + " in block " + rec.getBlockId());
        }
        long end = System.currentTimeMillis();
        printMetrics((end - start));
    }

    public void test(boolean order) throws Exception {
        try {
            firstInsert();
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }

        try {
            removal(order);
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }

        try {
            secondInsert();
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            e.printStackTrace();
        }

        search();
    }

    public void firstInsert() throws Exception {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome"));
        pt.addColumn(new StringColumn("bla"));
        Table table = Directory.getTable("c:\\teste\\ibd", "newTable",pt, 999, 4096, true);
        testMultipleInsertions(table, 0, 1000, true, false, 50, false);
    }

    public void removal(boolean order) throws Exception {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome"));
        pt.addColumn(new StringColumn("bla"));
        Table table = Directory.getTable("c:\\teste\\ibd", "newTable",pt, 9999, 4096, false);
        testMultipleRemotions(table, 0, 1000, order, false);
    }

    public void secondInsert() throws Exception {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome"));
        pt.addColumn(new StringColumn("bla"));
        Table table = Directory.getTable("c:\\teste\\ibd", "newTable",pt, 9999, 4096, false);
        testMultipleInsertions(table, 0, 1500, true, false, 50, true);
    }

    public void search() throws Exception {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome"));
        pt.addColumn(new StringColumn("bla"));
        Table table = Directory.getTable("c:\\teste\\ibd", "newTable",pt, 999,  4096, false);
        testMultipleSearches(table, 0, 1500, true, true);
    }

    public static void main(String[] args) {
        try {
            Main m = new Main();

            System.out.println("Caso 1");
            m.test(true);

            System.out.println("Caso 2");
            //m.test(false);

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
