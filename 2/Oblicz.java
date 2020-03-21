public class Oblicz {

    public static void main(String[] args){

        //deklarujemy tutaj przedział w jakim obliczamy całkę
        Definicja przedzial1 = new Definicja();
        przedzial1.a = 1;
        przedzial1.b = 3;

        System.out.println("Całka z zadanej funkcji w przedziale od "+ przedzial1.a + " do "+ przedzial1.b + " wynosi: "+ przedzial1.obliczanie());
    }
}
