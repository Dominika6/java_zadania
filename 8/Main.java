package com.company;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Main {

    public static void main(String[] args){
        Connection con=null;
        try{
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con=DriverManager.getConnection("jdbc:hsqldb:mydb7","SA","");
            con.createStatement().executeUpdate("SET DATABASE SQL SYNTAX MYS TRUE");
            con.createStatement().executeUpdate("create table IF NOT EXISTS contacts ("+
                    "id int auto_increment primary key," +
                    "name varchar(45)," +
                    "lname varchar(45),"+
                    "phone varchar(45))");

            JFrame frame = new JFrame("My Phone Book");
            JPanel panel = new JPanel();

            panel.setLayout(new FlowLayout());
            JLabel label = new JLabel(" Hello! What you want do to? ");
            panel.add(label);

            JButton buttonA = new JButton();
            buttonA.setText("ADD");
            panel.add(buttonA);
            JButton buttonS = new JButton();
            buttonS.setText("SEARCH");
            panel.add(buttonS);
            JButton buttonD = new JButton();
            buttonD.setText("DELETE");
            panel.add(buttonD);
            JButton buttonShow = new JButton();
            buttonShow.setText("SHOW ALL");
            panel.add(buttonShow);

            frame.add(panel);
            frame.setSize(260, 300);
            frame.setLocationRelativeTo(null);   //żeby okienko wyskakiwało na środku
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

//ADD
                for (; ; ) {
                    Connection finalCon = con;
                    buttonA.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            String nameA = JOptionPane.showInputDialog(frame, "Type in contact details in the format: name,lastname,phone", "Phone Book", JOptionPane.INFORMATION_MESSAGE);
                            String[] temp = nameA.split(",");
                            if (temp.length != 3) {
                                JOptionPane.showMessageDialog(frame, "Error, the insertion format should be in the format: firstname,lastname,phone.", "Alert", JOptionPane.WARNING_MESSAGE);
                            } else {
                                try {
                                    finalCon.createStatement().executeUpdate("insert into contacts (name,lname,phone) values" + "('" + temp[0] + "','" + temp[1] + "','" + temp[2] + "')");
                                    JOptionPane.showMessageDialog(frame, "Added");

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                }

//SEARCH
                for (; ; ) {
                    Connection finalCon1 = con;
                    buttonS.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                            String nameS = JOptionPane.showInputDialog(frame,"Type in the name you are searching for", "Phone Book", JOptionPane.INFORMATION_MESSAGE);
                            String data=nameS.trim();

                            String q="select * from contacts where name like\'%"+ data + "%\'";

                            try {
                                PreparedStatement pst= finalCon1.prepareStatement(q); //tworzy obiekt do wysłania sparametryzowanych instrukji sql do bazy
                                pst.clearParameters();                                //Natychmiast usuwa bieżące wartości parametrów.
                                ResultSet rs=pst.executeQuery();       //wykonuje zapytanie SQL w tym obiekcie PreparedStatement i zwraca obiekt ResultSet wygenerowany przez zapytanie.
                                while(rs.next()){
                                    JOptionPane.showMessageDialog(frame, "Numer " + rs.getString(2) + " " + rs.getString(3) + " to: " + rs.getString(4), "Phone Book", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                }

//DELETE
                for (; ; ) {
                    Connection finalCon2 = con;
                    buttonD.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {

                            try {
                                String nameD = JOptionPane.showInputDialog(frame,"Type in the name you want to delete", "Phone Book", JOptionPane.INFORMATION_MESSAGE);
                                String [] temp=nameD.split(",");
                                if (temp.length!=1){
                                    System.out.println("Error, only one world:");
                                }else{
                                    finalCon2.createStatement().executeUpdate("delete from contacts where name='" + nameD + "'");
                                    JOptionPane.showMessageDialog(frame, "Deleted");
                                }

                            }catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                }

//SHOW ALL

            for (; ; ) {
                Connection finalCon3 = con;
                buttonShow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {

                        String q="select * from contacts";

                        try {
                            PreparedStatement pst= finalCon3.prepareStatement(q);
                            pst.clearParameters();
                            ResultSet rs=pst.executeQuery();
                            String odp = "";
                            while(rs.next()){
                                odp += rs.getString(2) + " " + rs.getString(3) + ": " + rs.getString(4) + "\n";
                            }
                            JOptionPane.showMessageDialog(frame, odp, "Phone Book", JOptionPane.INFORMATION_MESSAGE);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
