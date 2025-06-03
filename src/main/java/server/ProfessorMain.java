package server;

import model.user.User;
import view.professor.ProfessorView;
import controller.professor.ProfessorReserveController;
import client.ReservationClient;
import server.ReservationServer;

import javax.swing.*;

public class ProfessorMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReservationServer reservationServer = new ReservationServer();
            ReservationClient reservationClient = ReservationClient.getInstance(reservationServer);
            User testUser = new User("prof01", "pw", "테스트교수", "컴퓨터공학과", "PROFESSOR");
            ProfessorView view = new ProfessorView(testUser, reservationClient);
            new ProfessorReserveController(view, testUser);
        });
    }
}