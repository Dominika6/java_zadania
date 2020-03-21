//Dominika Jadach, zadanie 1
//w argumentach przy uruchamianiu programu należy podać dane w kolejności: liczba pierwiastkowana stopień pierwiastka

public class Pierwiastek {
	
	public static final double precision = 1.0e-15;	
	
	public static double pierwiastek(double a, double n) { 				//pierwiastek z 'a' stopnia 'n' obliczany metodą Newtona
		double wynik = a;												//poczatkowe przyblizenie
		double tmp = potega( wynik , ( n - 1 ) );						//a ^ ( n - 1 )
		while(bezwzgl ( a - tmp * wynik) >= precision ) {				//dopoki wynik jest mniej dokładny niz zadana wartosc
			wynik = ( 1 / n ) * ( ( n - 1 ) * wynik + ( a / tmp ) );	//obliczamy nowe przyblizenie
			tmp = potega( wynik , n - 1 );								//a ^ ( n - 1 )
		}
		return wynik;
	}
	
	public static double bezwzgl(double x) {							// | x |
		double wynik = x;
		if( x < 0 ) {
			wynik += ( -2 ) * wynik; 
		}
		return wynik;	
	}
	public static double potega(double a, double b){					// a ^ b
		double wynik = 1;
		for(int i = 0; i < b; i++) {
			wynik *= a;
		}
		return wynik;
	}
	
	public static void main(String[] args) {
		
		double a = Double.parseDouble(args[0]);
		double b = Double.parseDouble(args[1]);
		
		if(args.length > 2)
			System.out.println("za dużo argumentów, mają być dwa: liczba i potęga");
		if(b < 1)
			System.out.println("Stopień pierwiastka musi być liczbą naturalną");
		else
			System.out.println("Pierwiastek z " + a + " stopnia " + b + " wynosi " + pierwiastek( a, b ));
		
		
	}
}
