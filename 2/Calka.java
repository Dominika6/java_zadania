public abstract class Calka {

    public abstract double funkcja(double x);

    double a, b;    //przedział całkowania
    double n = 100; //liczba iteracji

    double obliczanie(){  //implementacja metody trapezów
        double h = (b - a) / n;
        double suma = 0;
        double x1 = a;
        double y1 = funkcja(x1);
        double xn = a + n * h;
        double yn = funkcja(xn);
        double x;
        double y;

        for(int i = 1; i < n; i++){
            x = a + i * h;
            y = funkcja(x);
            suma += y;
        }
        double wynik = h * ( y1/2 + suma + yn/2 );
        return wynik;
    }

}
