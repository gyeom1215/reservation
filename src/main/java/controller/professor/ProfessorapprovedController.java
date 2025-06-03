package controller.professor;

import model.professor.ProfessorApprovedListModel;
import model.professor.ProfessorApprovedModel;
import model.user.User;
import view.professor.ProfessorApprovedView;

import javax.swing.*;
import java.util.List;

public class ProfessorapprovedController {

    public static void openUserReservation(User user) {
        if (!"PROFESSOR".equals(user.getRole())) {
            System.out.println("교수만 이용 가능합니다.");
            return;
        }

        String jsonFilePath = "approved_reservations.json";

        try {
            ProfessorApprovedListModel model = new ProfessorApprovedListModel(jsonFilePath);
            List<ProfessorApprovedModel> mySchedules = model.getMySchedules(user.getName(), user.getRole());

            SwingUtilities.invokeLater(() -> {
                ProfessorApprovedView view = new ProfessorApprovedView(mySchedules);
                view.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}