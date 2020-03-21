// Proszę napisać program rysujący wykres dowolnej, wskazanej funkcji f:R->R.
// Klasa implementująca funkcję powinna być podana przez użytkownika w trakcie działania programu.

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Main extends JPanel implements ActionListener {

    JButton go;
    Object obj;
    Method m;

    public Main() throws ClassNotFoundException, NoSuchMethodException {
        super();
        go = new JButton("Wybierz funkcję");
        go.addActionListener(this);
        add(go);
        m = Class.forName("java.lang.Math").getDeclaredMethod("sin", double.class);
    }

    public void actionPerformed(ActionEvent e) {

        try {
            JFrame frame = new JFrame("Wykres");
            String funkcja = JOptionPane.showInputDialog(frame, "Funkcja którą chcesz narysować: " );

            double x = 0;
            Class c = Class.forName(funkcja);
            obj = c.newInstance();
            m = c.getMethod("funkcja", double.class);
            this.repaint();


        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        int x0 = 0, y0 = 0;
        double fx = 0.0;
        int y;
        for (int x = 0; x < this.getWidth(); x++) {
            try {
                System.out.println(m);
                System.out.println(obj);
                fx = (double) m.invoke(obj, (x/(0.5*this.getWidth())-1) );

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            y = (int) ((-fx + 1.0) * 0.5*this.getHeight());
            if (x != 0) {
                g.drawLine(x0, y0, x, y);
            }
            x0 = x;
            y0 = y;
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {
        JFrame frame = new JFrame("Wykres");
        Main panel = new Main();
        frame.getContentPane().add(panel, "Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Main());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}



