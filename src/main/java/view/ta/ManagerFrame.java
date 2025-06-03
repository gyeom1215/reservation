/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.ta;

import client.ReservationClient;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerFrame extends JFrame {
    private final ReservationClient reservationClient;
    private JTable table;
    private static final Logger LOGGER = Logger.getLogger(ManagerFrame.class.getName());

    public ManagerFrame(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;
        setTitle("사용자 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 사용자 테이블
        String[] columnNames = {"아이디", "이름", "학과", "역할"};
        Object[][] data = {}; // 실제 데이터는 서버에서 가져와야 함
        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
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

    public JTable getUserTable() {
        return table;
    }

    public void setUserTableModel(DefaultTableModel model) {
        table.setModel(model);
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
                try {
                    new ManagerFrame(reservationClient).setVisible(true);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error processing manager: " + ex.getMessage(), ex);
                    System.err.println("관리자 처리 중 오류 발생: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }
}
