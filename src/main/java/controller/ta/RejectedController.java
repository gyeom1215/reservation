/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.rejectedFrame;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class RejectedController {
    private ReservationModel model;
    private rejectedFrame view;

    public RejectedController(ReservationModel model, rejectedFrame view) {
        this.model = model;
        this.view = view;
    }

    public void loadRejectedReservations() {
        List<Reservation> rejectedList = model.loadRejectedReservations();

        DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태" }, 0
        );

        for (Reservation r : rejectedList) {
            String timeSlot = r.getTimeSlots().isEmpty() ? "" : r.getTimeSlots().get(0);

            tableModel.addRow(new Object[] {
                r.getName(),
                r.getRole(),
                r.getType(),
                r.getRoomNumber(),
                r.getDay(),
                timeSlot,
                r.getState()
            });
        }

        System.out.println("📥 컨트롤러에서 받은 거절 예약 수: " + rejectedList.size());
        view.setRejectedTableModel(tableModel);
    }
}

