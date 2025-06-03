/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.ta;

import client.ReservationClient;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogFrame extends JFrame {
    private final ReservationClient reservationClient;
    private static final Logger LOGGER = Logger.getLogger(LogFrame.class.getName());

    public LogFrame(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;
        setTitle("조교 로그");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 로그 텍스트 영역
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            new featureFrame(reservationClient).setVisible(true);
            dispose();
        });
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Error setting look and feel: " + ex.getMessage(), ex);
        } catch (InstantiationException ex) {
            LOGGER.log(Level.SEVERE, "Error setting look and feel: " + ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, "Error setting look and feel: " + ex.getMessage(), ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.log(Level.SEVERE, "Error setting look and feel: " + ex.getMessage(), ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                server.ReservationServer reservationServer = new server.ReservationServer();
                client.ReservationClient reservationClient = client.ReservationClient.getInstance(reservationServer);
                new LogFrame(reservationClient).setVisible(true);
            }
        });
    }
}
