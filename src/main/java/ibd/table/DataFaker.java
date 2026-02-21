/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author pccli
 */
public class DataFaker {

    static final String[] names = {"Alexandre", "Alice", "Ana", "Andre", "Antonia", "Arthur",
        "Beatriz", "Carla", "Carlos", "Carolina", "Clara", "Cristiano", "Daniel", "Denise", "Diana",
        "Eduardo", "Elias", "Eva", "Evandro", "Fabiano", "Fernanda", "Felipe", "Francisco", "Fred",
        "Gabriela", "Gisele", "Gustavo", "Helena", "Henrique", "Hugo", "Ines", "Irene", "Isabel",
        "Joao", "Joaquim", "Jorge", "Julia", "Laura", "Lucas", "Luis", "Luisa",
        "Manuel", "Marcos", "Maria", "Mario", "Marta", "Mateus", "Miguel", "Monica",
        "Nelson", "Nicole", "Ofelia", "Patricia", "Paula", "Paulo", "Pierre",
        "Raquel", "Raul", "Ricardo", "Rita", "Rodrigo", "Rosa", "Sandra", "Sara", "Sofia",
        "Tatiana", "Teresa", "Tiago", "Ursula", "Vera", "Vitor", "Yasmin"
    };

    static public String[] generateUniqueNames(int size, String names[]) {
        String[] names_ = new String[size];
        if (size < names.length) {
            System.arraycopy(names, 0, names_, 0, size);
            return names_;
        }
        int turns = size / names.length + 1;
        for (int i = 0; i < turns; i++) {
            for (int j = 0; j < names.length; j++) {
                if (i * names.length + j == size) {
                    return names_;
                }
                names_[i * names.length + j] = names[j] + i;

            }
        }

        return names_;

    }

    static public String[] generateNames(int size, String names[]) {
        String[] names_ = new String[size];
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            String text = names[r.nextInt(names.length)];
            names_[i] = text;

        }

        return names_;

    }

    static public String[] generateNames(int cardinality, int size) {
        return generateNames(cardinality, size, names);
    }

    static public String[] generateNames(int cardinality, int size, String names[]) {
        String[] names_ = new String[size];
        String uniqueNames[] = generateUniqueNames(cardinality, names);
        if (cardinality > size) {
            System.arraycopy(names, 0, names_, 0, size);
            return names_;

        }
        int turns = size / cardinality + 1;
        for (int i = 0; i < turns; i++) {
            for (int j = 0; j < cardinality; j++) {
                if (i * cardinality + j == size) {
                    return names_;
                }
                names_[i * cardinality + j] = uniqueNames[j];

            }
        }

        return names_;

    }
    
    static public String[] generateStrings(String[] values, int size, boolean ordered) {
        String[] strings = new String[size];
        
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            strings[i] = values[random.nextInt(values.length)];
        }
        
        if (ordered)
            Arrays.sort(strings);
        return strings;
    }

    static public Integer[] generateInts(int cardinality, int size, int startValue, int gap, boolean random) {
        Integer[] uniqueInts = new Integer[cardinality];
        int current = startValue;
        for (int i = 0; i < cardinality; i++) {
            uniqueInts[i] = current;
            current += gap;
        }

        Random r = new Random();
        int index = 0;
        Integer[] result = new Integer[size];
        for (int i = 0; i < size; i++) {
            if (random) {
                result[i] = uniqueInts[r.nextInt(uniqueInts.length)];
            } else {
                result[i] = uniqueInts[index++];
                if (index >= uniqueInts.length) {
                    index = 0;
                }
            }

        }
        return result;

    }

    static public String[] generateUniqueNames(int size, String prefix) {
        String[] names = new String[size];

        for (int i = 0; i < size; i++) {
            names[i] = prefix + ":" + (i + 1);

        }
        return names;

    }
    
    

    public static String pad(String text, int len) {
        if (text.length() > len) {
            return text;
        }
        StringBuilder buf = new StringBuilder(text);
        int len_ = len - text.length();
        for (int i = 0; i < len_; i++) {
            buf.append(" ");
        }
        return buf.toString();
    }

    public static void shuffleArray(Comparable[] ar) {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Comparable a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
    

    public static void main(String[] args) {
//        Integer ids[] = DataFaker.generateInts(10000, 100000, 1, 1, true);
//        String names[] = DataFaker.generateUniqueNames(10000, "Proj");
//        Integer ids2[] = DataFaker.generateInts(1000, 100000, 1, 1, true);
//        
//        Integer idF[] = DataFaker.generateInts(1000, 1000, 1, 1, false);
//        String nameF[] = DataFaker.generateUniqueNames(1000, "Func");
//        Integer salario[] = DataFaker.generateInts(100, 1000, 1000, 200, true);
//        Integer idDepto[] = DataFaker.generateInts(40, 1000, 1, 1, true);
//        
//        
//        Integer idP[] = DataFaker.generateInts(10000, 10000, 1, 1, false);
//        String nameP[] = DataFaker.generateUniqueNames(10000, "Proj");
//        Integer custo[] = DataFaker.generateInts(50, 10000, 0, 5000, true);
//        Integer duracao[] = DataFaker.generateInts(66, 10000, 1, 1, true);

        Integer idP[] = DataFaker.generateInts(10000000, 10000000, 1, 1, false);
        String nameP[] = DataFaker.generateUniqueNames(10000000, "Proj");
        Integer custoEstimado[] = DataFaker.generateInts(50, 10000000, 10000, 5000, true);
        Integer custoReal[] = DataFaker.generateInts(50000, 10000000, 10000, 5000, true);
        Integer duracaoEstimada[] = DataFaker.generateInts(5, 10000000, 1, 1, true);
        Integer duracaoReal[] = DataFaker.generateInts(10, 10000000, 1, 1, true);
        String uniqueStatus[] = DataFaker.generateUniqueNames(2, "Status");
        String status[] = DataFaker.generateNames(10000000, uniqueStatus);

        HashSet<String> dic = new HashSet();

        Random random = new Random();

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("c:\\teste\\LargeProj.sql"), "utf-8"));
            for (int i = 0; i < 10000000; i++) {
//                String key = "";
//                while (true){
//                    int idP = 1+random.nextInt(10000);
//                    int idF = 1+random.nextInt(1000);
//                    key = idP+","+idF;
//                    if (!dic.contains(key)){
//                        dic.add(key);
//                        break;
//                    }
//                }
//                
//                String sql = "insert into aloc values("+key+");";

                String sql = 
                        " "
                        //+"insert into proj values("
                        + idP[i] + ","
                        + nameP[i] + ","
                        + custoEstimado[i] + ","
                        + custoReal[i] + ","
                        + duracaoEstimada[i] + ","
                        + duracaoReal[i] + ","
                        + status[i]
                        //+ ");"
                        ;
                writer.write(sql);
                writer.write(System.lineSeparator());
            }

        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }

    }

}
