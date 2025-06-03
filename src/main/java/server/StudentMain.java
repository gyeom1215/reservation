package server;

import model.user.User;
import view.student.StudentView;
import controller.student.StudentReserveController;
import client.ReservationClient;
import server.ReservationServer;

import javax.swing.*;

public class StudentMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReservationServer reservationServer = new ReservationServer();
            ReservationClient reservationClient = ReservationClient.getInstance(reservationServer);
            User testUser = new User("stu01", "pw", "테스트학생", "컴퓨터공학과", "STUDENT");
            StudentView view = new StudentView(testUser, reservationClient);
            new StudentReserveController(view, testUser);
            view.setVisible(true);
        });
    }
}