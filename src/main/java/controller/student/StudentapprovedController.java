package controller.student;

import model.student.StudentApprovedListModel;
import model.student.StudentApprovedModel;
import model.user.User;
import view.student.StudentApprovedView;

import javax.swing.*;
import java.util.List;

public class StudentapprovedController {

    public static void openUserReservation(User user) {
        if (!"STUDENT".equals(user.getRole())) {
            System.out.println("학생만 이용 가능합니다.");
            return;
        }

        String jsonFilePath = "reservations.json";

        try {
            StudentApprovedListModel model = new StudentApprovedListModel(jsonFilePath);
            List<StudentApprovedModel> mySchedules = model.getMySchedules(user.getName(), user.getRole());

            SwingUtilities.invokeLater(() -> {
                StudentApprovedView view = new StudentApprovedView(mySchedules);
                view.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}