/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.ta;
import controller.ta.ReservationController;
import model.ta.ReservationModel;
import view.login.LoginView;
import client.ReservationClient;
import server.ReservationServer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class featureFrame extends JFrame {

    private ReservationController controller;
    private final ReservationClient reservationClient;
    private static final Logger LOGGER = Logger.getLogger(featureFrame.class.getName());

    public featureFrame(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;
        setTitle("조교 기능");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initializeUI();
        controller = new ReservationController(new ReservationModel(), this);
        try {
            controller.loadReservedDataToTable(); // 예약 내역 테이블에 표시
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing reservation: " + ex.getMessage(), ex);
            System.err.println("예약 처리 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // 🔽 JTable 접근용 getter
    public JTable getReservationTable() {
        return jTable2; // 이 이름이 NetBeans 테이블 컴포넌트 이름과 같아야 함
    }
    public void setReservationTableModel(DefaultTableModel model) {
        jTable2.setModel(model);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnReservations = new JButton("예약 관리");
        JButton btnApproved = new JButton("승인된 예약");
        JButton btnRejected = new JButton("거절된 예약");
        JButton btnLogs = new JButton("로그 보기");
        JButton btnManager = new JButton("사용자 관리");
        JButton btnBack = new JButton("로그아웃");

        btnReservations.addActionListener(e -> {
            new ReservationFrame(reservationClient).setVisible(true);
            dispose();
        });

        btnApproved.addActionListener(e -> {
            new approvedFrame(reservationClient).setVisible(true);
            dispose();
        });

        btnRejected.addActionListener(e -> {
            new rejectedFrame(reservationClient).setVisible(true);
            dispose();
        });

        btnLogs.addActionListener(e -> {
            new LogFrame(reservationClient).setVisible(true);
            dispose();
        });

        btnManager.addActionListener(e -> {
            new ManagerFrame(reservationClient).setVisible(true);
            dispose();
        });

        btnBack.addActionListener(e -> {
            new LoginView(reservationClient).setVisible(true);
            dispose();
        });

        mainPanel.add(btnReservations);
        mainPanel.add(btnApproved);
        mainPanel.add(btnRejected);
        mainPanel.add(btnLogs);
        mainPanel.add(btnManager);
        mainPanel.add(btnBack);

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
            java.util.logging.Logger.getLogger(featureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(featureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(featureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(featureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                server.ReservationServer reservationServer = new server.ReservationServer();
                ReservationClient reservationClient = ReservationClient.getInstance(reservationServer);
                new featureFrame(reservationClient).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
