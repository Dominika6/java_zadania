import java.io.*;
import java.util.*;


public class Main{

    public static void main(String[] args) {

        int tab2[] = new int[20];
        
        
        try{
            //reader do czytania kolejnych linii
            BufferedReader reader = new BufferedReader(new FileReader("plik.txt"));
            //read do sprawdzania, czy skończyliśmy już czytać plik
            BufferedReader read = new BufferedReader(new FileReader("plik.txt"));
            
            String line = read.readLine();
            
            while(line != null){
                String linia = reader.readLine();
                StringTokenizer tokeny = new StringTokenizer(linia, " ,.:-!?");

                while (tokeny.hasMoreTokens()) {
                    String abc = tokeny.nextToken();
                    int dlugosc = abc.length();
                    tab2[dlugosc] += 1;
                }
                line = read.readLine();
            }
            reader.close();
            read.close();
            
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        
        for (int i=0;i<20;i++){
            System.out.println("Liczba wyrazów o długości "+(i+1) + "\t:  " + tab2[i]);
        
        }
    }
}
