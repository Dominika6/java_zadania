package com.company;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class Main{

    public static void main(String[] args){
        new Okienko();
    }

}


class JMultilineLabel extends JTextArea{

    public JMultilineLabel(){
        setOpaque(false);   //pole z tym otrzymaną wiadomością jest ciemniejsze niż to w którym wpisujemy
    }

}


class Okienko extends JFrame{

    int port1;
    int port2;
    byte[] buffer;
    DatagramSocket datagramSocket;
    InetAddress inetAddress;

    Okienko(){             //to okienko na początku, do wpisania portów

        super("Komunikator");

        JPanel l = new JPanel();
        l.setLayout(new BoxLayout(l, BoxLayout.PAGE_AXIS)); //to robi miejsce do wpisywania nr portu
        l.setPreferredSize(new Dimension(250, 100));

        JButton start = new JButton(" Połącz! ");

        JPanel l1 = new JPanel();
        l1.setLayout(new BoxLayout(l1, BoxLayout.LINE_AXIS));
        l1.add(new JLabel(" Wysyłaj na porcie:  "));
        JTextField f1 = new JTextField();

        JPanel l2 = new JPanel();
        l2.setLayout(new BoxLayout(l2, BoxLayout.LINE_AXIS));
        l2.add(new JLabel(" Odbieraj na porcie: "));
        JTextField f2 = new JTextField();


        start.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                port1 = Integer.parseInt(f1.getText());
                port2 = Integer.parseInt(f2.getText());
                remove(l);
                chat();
            }
        });


        l1.add(f1);
        l2.add(f2);
        l.add(l1);
        l.add(l2);
        l.add(start);
        setVisible(true);
        getContentPane().add(l);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    void listenerThread(JMultilineLabel output){

        Thread listenerThread = new Thread(){

            @Override
            public void run(){

                DatagramSocket datagramSocket = null;
                try{
                    datagramSocket = new DatagramSocket(port2);
                }catch (SocketException e){
                    e.printStackTrace();
                }

                if (datagramSocket != null){
                    byte[] receive = new byte[11111];
                    DatagramPacket datagramPacket;
                    while(true){
                        datagramPacket = new DatagramPacket(receive, receive.length);
                        try{
                            datagramSocket.receive(datagramPacket);
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                        output.setText(output.getText() + data(receive) + "\n");
                        receive = new byte[11111];
                    }
                }
            }

            public String data(byte[] tab){
                if (tab == null)
                    return null;
                StringBuilder returned = new StringBuilder();   //używa się StringBuilder przy zmiennych sekwencjach znaków
                int i = 0;
                while (tab[i] != 0) {
                    returned.append((char) tab[i]);
                    i++;
                }
                return returned.toString();
            }
        };
        listenerThread.start();
    }

    void writeListener(JTextArea write){
        write.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }

            @Override
            public void insertUpdate(DocumentEvent e){

                try{
                    String txt = e.getDocument().getText(0, e.getDocument().getLength());

                    if (txt.endsWith("\n")) {
                        inetAddress = InetAddress.getLocalHost();
                        datagramSocket = new DatagramSocket();
                        String[] lines = txt.split("\n");
                        String inp = lines[lines.length - 1];
                        buffer = inp.getBytes();
                        DatagramPacket datagramPacketSend = new DatagramPacket(buffer, buffer.length, inetAddress, port1);
                        datagramSocket.send(datagramPacketSend);
                    }

                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void chat(){

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        JTextArea txt = new JTextArea();                //okienko do wpisywania
        writeListener(txt);
        txt.setPreferredSize(new Dimension(400, 250));

        JMultilineLabel txt1 = new JMultilineLabel();     //okienko w którym wyświetlają się przychodzące wiadomości
        listenerThread(txt1);
        txt1.setPreferredSize(new Dimension(400, 250));
        getContentPane().add(p);
        setMinimumSize(new Dimension(400, 500));

        p.add(txt);
        p.add(txt1);
        setVisible(true);

    }
}
