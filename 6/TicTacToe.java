package zadanie;
																			//java. awt - Abstract Window Toolkit, jest to pakiet zawierający 
																			//niezależny od platformy systemowej zestaw klas 
																			//do projektowania aplikacji w środowisku graficznym
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.util.Scanner;

import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.imageio.ImageIO;

import javax.swing.JPanel;
import javax.swing.JFrame;


public class TicTacToe implements Runnable {
	
	private JFrame frame;
	private Thread wyjatek;
	private int port = 12345;
	private final int width = 505;
	private final int height = 530;
	private String ip = "localhost";
	private Scanner scanner = new Scanner(System.in);


	private Painter painter;
	private Socket gniazdko;
	private DataInputStream input;
	private DataOutputStream output;

	private ServerSocket serverSocket;

	private BufferedImage tab;
	private BufferedImage x1;			//zielony X
	private BufferedImage x2;			//czarny X
	private BufferedImage o1;			//zielone kółko
	private BufferedImage o2;			//czarne kółko

	private String[] pole = new String[9];

	private boolean kolejka = false;
	private boolean kolko = true;
	private boolean akceptuj = false;
	private boolean brakPolaczenia = false;
	private boolean wygrana = false;
	private boolean przegrana = false;
	private boolean remis = false;

	private int dlugoscOkienka = 160;
	private int error = 0;
	private int firstSpot = -1;
	private int secondSpot = -1;

	private Font font = new Font("FreeSans", Font.BOLD,25);

	private String czekaj = "Czekamy na drugiego gracza";
	private String brakPolaczeniaString = "Brak połączenia.";
	private String wygranaString = "Wygrana!";
	private String przegranaString = "Niestety :(";
	private String remisString = "Remis";

	private int[][] kiedyWygrana = new int[][] { 
		{ 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, 	// poziome
		{ 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, 	// pionowe
		{ 0, 4, 8 }, { 2, 4, 6 }};				// ukośne


	public static void main(String[] args)throws Exception {
		TicTacToe ticTacToe = new TicTacToe();
	}

	public TicTacToe() {
		System.out.println("IP: ");
		ip = scanner.nextLine();
		System.out.println("PORT: ");
		port = scanner.nextInt();

		
		try {									//wczytujemy obrazki
			tab = ImageIO.read(getClass().getResourceAsStream("/tab.png"));
			x1 = ImageIO.read(getClass().getResourceAsStream("/x1.png"));
			o1 = ImageIO.read(getClass().getResourceAsStream("/o1.png"));
			x2 = ImageIO.read(getClass().getResourceAsStream("/x2.png"));
			o2 = ImageIO.read(getClass().getResourceAsStream("/o2.png"));
		}catch (IOException e) {
			e.printStackTrace();
		}

		painter = new Painter();
		painter.setPreferredSize(new Dimension(width, height));

		if (!connect()){
			try {
				serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
			}catch (Exception e) {
				e.printStackTrace();
			}
			kolejka = true;
			kolko = false;
		}

		frame = new JFrame();						//robimy "okienko"
		frame.setTitle("Tic-Tac-Toe");
		frame.setContentPane(painter);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		wyjatek = new Thread(this, "TicTacToe");
		wyjatek.start();
	}

	public void run() {
		while (true) {
			
			if (error >= 10) 
				brakPolaczenia = true;	
			if (!kolejka && !brakPolaczenia) {	//kliknięcie
				try {
					int space = input.readInt();
					if (kolko) pole[space] = "X";
					else pole[space] = "O";
					czyPrzegrana();
					czyWygrana();
					kolejka = true;
				} catch (IOException e) {
					e.printStackTrace();
					error++;
				}
			}																				//

			painter.repaint();

			if (!kolko && !akceptuj) {		//nasłuchiwanie odpowiedzi od serwera
				Socket gniazdko = null;
				try {
					gniazdko = serverSocket.accept();
					output = new DataOutputStream(gniazdko.getOutputStream());
					input = new DataInputStream(gniazdko.getInputStream());
					akceptuj = true;
					System.out.println("Użytkownik powinien dołączyć");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}															//

		}
	}

	private void render(Graphics g) {
		g.drawImage(tab, 0, 0, null);
		if (brakPolaczenia) {
			g.setColor(Color.RED);
			g.setFont(font);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(brakPolaczeniaString);
			g.drawString(brakPolaczeniaString, width / 2 - stringWidth / 2, height / 2);
			return;
		}

		if (akceptuj) {
			for (int i = 0; i < pole.length; i++) {
				if (pole[i] != null) {						//rysujemy X lub 0
					if (pole[i].equals("X")) {
						if (kolko) {
							g.drawImage(x1, (i % 3) * dlugoscOkienka + 10 * (i % 3), (int) (i / 3) * dlugoscOkienka + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(x2, (i % 3) * dlugoscOkienka + 10 * (i % 3), (int) (i / 3) * dlugoscOkienka + 10 * (int) (i / 3), null);
						}
					} else if (pole[i].equals("O")) {
						if (kolko) {
							g.drawImage(o2, (i % 3) * dlugoscOkienka + 10 * (i % 3), (int) (i / 3) * dlugoscOkienka + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(o1, (i % 3) * dlugoscOkienka + 10 * (i % 3), (int) (i / 3) * dlugoscOkienka + 10 * (int) (i / 3), null);
						}
					}
				}
			}
			if (wygrana || przegrana) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(10));
				g.setColor(Color.RED);
				g.drawLine(firstSpot % 3 * dlugoscOkienka + 10 * firstSpot % 3 + dlugoscOkienka / 2, (int) (firstSpot / 3) * dlugoscOkienka + 10 * (int) 
									(firstSpot / 3) + dlugoscOkienka / 2, secondSpot % 3 * dlugoscOkienka + 10 * secondSpot % 3 + dlugoscOkienka / 2, (int) (secondSpot / 3) 
									* dlugoscOkienka + 10 * (int) (secondSpot / 3) + dlugoscOkienka / 2);

				g.setColor(Color.RED);
				g.setFont(font);
				if (wygrana) {
					int stringWidth = g2.getFontMetrics().stringWidth(wygranaString);
					g.drawString(wygranaString, width / 2 - stringWidth / 2, height / 2);
				} else if (przegrana) {
					int stringWidth = g2.getFontMetrics().stringWidth(przegranaString);
					g.drawString(przegranaString, width / 2 - stringWidth / 2, height / 2);
				}
			}
			if (remis) {
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.RED);
				g.setFont(font);
				int stringWidth = g2.getFontMetrics().stringWidth(remisString);
				g.drawString(remisString, width / 2 - stringWidth / 2, height / 2);
			}
		} else {
			g.setColor(Color.RED);
			g.setFont(font);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(czekaj);
			g.drawString(czekaj, width / 2 - stringWidth / 2, height / 2);
		}

	}



	private void czyWygrana() {
		for (int i = 0; i < kiedyWygrana.length; i++) {
			if (kolko) {
				if (pole[kiedyWygrana[i][0]] == "O" && pole[kiedyWygrana[i][1]] == "O" && pole[kiedyWygrana[i][2]] == "O") {
					firstSpot = kiedyWygrana[i][0];
					secondSpot = kiedyWygrana[i][2];
					wygrana = true;
				}
			} else {
				if (pole[kiedyWygrana[i][0]] == "X" && pole[kiedyWygrana[i][1]] == "X" && pole[kiedyWygrana[i][2]] == "X") {
					firstSpot = kiedyWygrana[i][0];
					secondSpot = kiedyWygrana[i][2];
					wygrana = true;
				}
			}
		}
	}

	private void czyPrzegrana() {
		for (int i = 0; i < kiedyWygrana.length; i++) {
			if (kolko) {
				if (pole[kiedyWygrana[i][0]] == "X" && pole[kiedyWygrana[i][1]] == "X" && pole[kiedyWygrana[i][2]] == "X") {
					firstSpot = kiedyWygrana[i][0];
					secondSpot = kiedyWygrana[i][2];
					przegrana = true;
				}
			} else {
				if (pole[kiedyWygrana[i][0]] == "O" && pole[kiedyWygrana[i][1]] == "O" && pole[kiedyWygrana[i][2]] == "O") {
					firstSpot = kiedyWygrana[i][0];
					secondSpot = kiedyWygrana[i][2];
					przegrana = true;
				}
			}
		}
	}

	private void czyRemis() {
		for (int i = 0; i < pole.length; i++) {
			if (pole[i] == null) {
				return;
			}
		}
		remis = true;
	}

	private boolean connect() {
		try {
			gniazdko = new Socket(ip, port);
			output = new DataOutputStream(gniazdko.getOutputStream());
			input = new DataInputStream(gniazdko.getInputStream());
			akceptuj = true;
		} catch (IOException e) {
			System.out.println("Nie można się połączyć");
			return false;
		}
		System.out.println("Połączono z serwerem.");
		return true;
	}


	

	private class Painter extends JPanel implements MouseListener {
		private static final long serialVersionUID = 1L;	//default serial version ID

		public Painter() {
			setFocusable(true);
			requestFocus();
			setBackground(Color.WHITE);
			addMouseListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			render(g);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (akceptuj) {
				if (kolejka && !brakPolaczenia && !wygrana && !przegrana) {
					int x = e.getX() / dlugoscOkienka;
					int y = e.getY() / dlugoscOkienka;
					y *= 3;
					int position = x + y;

					if (pole[position] == null) {
						if (!kolko) pole[position] = "X";
						else pole[position] = "O";
						kolejka = false;
						repaint();
						Toolkit.getDefaultToolkit().sync();

						try {
							output.writeInt(position);
							output.flush();
						}catch (IOException e1) {
							error++;
							e1.printStackTrace();
						}

						System.out.println("przesłano dane");
						czyWygrana();
						czyRemis();

					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
}
