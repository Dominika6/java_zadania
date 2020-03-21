package zadanie3;

import java.util.*;
import java.io.*; //m.in. PrintWriter
import java.util.regex.Matcher;
import java.util.regex.Pattern;
        
public class KsiazkaTelefoniczna {
    
    public static final String sciezka = "src/main/java/zadanie3/ksiazka.csv";
    
        private static void zapisz(Map<String, List<String>> ksiazka) { //Map zawiera pary: klucz i wartość
            try (PrintWriter dopisz = new PrintWriter(sciezka)) {   //zapis do pliku
                if (!ksiazka.isEmpty()) {   
                    for (Map.Entry<String, List<String>> entry : ksiazka.entrySet()) {  //entrySet() - zwraca zbiór par klucz-wartość
                        String line = String.format("%s,\"%s\"", entry.getKey(), entry.getValue().toString().replaceAll("\\[|]", ""));  //%s to format na string
                                                                                                                                        //replaceAll(x,y) zastępuje wszystkie x poprzez y
                        dopisz.println(line);
                    }
                }
            } catch (IOException ioex) {
            System.err.println(ioex.getMessage());
        }
    }
        
        private static void zaladuj(Map<String, List<String>> ksiazka){
            //załadowanie listy kontaktów
            try (BufferedReader reader = new BufferedReader(new FileReader(sciezka))) {
                Pattern pattern = Pattern.compile("^([^,\"]{2,50}),\"([0-9+, ]+)\"$");  //kompilacja podanego wyrażenia do zadanego wzoru 
                                                                                        //poniżej link do strony, gdzi ewytłumaczone są te skróty
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    Matcher matcher = pattern.matcher(line);//funkcja matcher-Tworzy moduł dopasowujący, który dopasuje dane wejściowe do tego wzoru.
                    if (matcher.find()) {
                        String[] numbers = matcher.group(2).split(",\\s*");
                        ksiazka.put(matcher.group(1), Arrays.asList(numbers));
                    }
                }   
            }catch(IOException ioex){
                System.err.println("Nie mogę załadować listy kotaktów. ");
            }
        }
    
    private static void dodaj(Map<String, List<String>> ksiazka, Scanner input){
        String imie;
        String numer;
                        //wytłumaczenie argumentów funkcji matches()
        while(true){    //https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
            System.out.println("Wpisz imię: ");
            imie = input.nextLine().trim();
            if (imie.matches("^.{2,20}$")) {//funkcja matches- Kompiluje podane wyrażenie regularne i próbuje dopasować podane dane wejściowe do niego.
                break;
            } else {
                System.out.println("Długość imienia musi mieścić się w przedziale 2-20");
            }
        }
        while(true){
            System.out.println("Wpisz numer: ");
            numer = input.nextLine().trim();
            if (imie.matches("^.{2,50}$")) {
                break;
            } else {
                System.out.println("Długość numeru musi mieścić się w przedziale 5-20");
            }
        }
        
        if(ksiazka.containsKey(imie)){
          
            System.out.println("W książce istnieje już kontakt o takiej nazwie. ");
           
            if(ksiazka.get(imie).contains(numer)){
                System.out.println("Ten numer jest już przypisany do kontaktu. ");
            }else{
                ksiazka.get(imie).add(numer);
                zapisz(ksiazka);
                System.out.println("Dodano numer do osoby. ");
            }
        }else{
            List<String> nowyNumer = new ArrayList<>();
            nowyNumer.add(numer);
            ksiazka.put(imie, nowyNumer);
            zapisz(ksiazka);
            System.out.println("Dodano nowy kontakt. ");
        }
    }
    
    private static void usun(Map<String, List<String>> ksiazka, Scanner input){
        System.out.println("Czyj numer chcesz usunąć? ");
        String imie = input.nextLine().trim();
        if (ksiazka.containsKey(imie)){
            ksiazka.remove(imie);
            zapisz(ksiazka);
            System.out.println("Usunięto kontakt z listy. ");
        }else{
            System.out.println("Nie masz takiego kontaktu. ");
        }
    }
    
    private static void szukaj(Map<String, List<String>> ksiazka, Scanner input){
        System.out.println("Czyjego numeru szukasz? ");
        String imie = input.nextLine().trim(); 
        //trim() zwraca kopię stringa pomijając białe znaki przed i po
        if (ksiazka.containsKey(imie)) {
            System.out.println(imie);
            for (String numer : ksiazka.get(imie)) {
                System.out.println(numer);
            }
        } else {
            System.out.println("Brak takiego kontaktu. ");
        }
    }
    
    private static void wypiszListe(Map<String, List<String>> ksiazka){
        if(!ksiazka.isEmpty()){
            for(Map.Entry<String, List<String>> entry: ksiazka.entrySet()){
                System.out.println(entry.getKey());
                for(String numer : entry.getValue()) {
                    System.out.println(numer);
                }
            }

        }else{
            System.out.println("Brak kontaktów");
        }
    }
        
    public static void main(String[] args) {
        System.out.println("\nKsiążka telefoniczna. ");
        System.out.println("Dostępne komendy: 'dodaj', 'usun', 'szukaj', 'wypiszListe'. ");
        System.out.println("Polecenie 'exit' zakończy działanie programu. ");
        
        Map<String, List<String>> ksiazka = new TreeMap<>();
        zaladuj(ksiazka);
        
        
        Scanner input = new Scanner(System.in);
        String line = input.nextLine().trim();
        
        while(!line.equals("exit")){
            switch(line){
                case "dodaj":
                    dodaj(ksiazka, input);
                    break;
                case "usun": 
                    usun(ksiazka, input);
                    break;
                case "szukaj":
                    szukaj(ksiazka, input);
                    break;
                case "wypiszListe":
                    wypiszListe(ksiazka);
                    break;
                default:
                    System.out.println("Niepoprawna komenda. ");
                    break;
            }
            line = input.nextLine().trim();
        }
    }
}
