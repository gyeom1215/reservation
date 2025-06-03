import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationModel {
    private static final Logger LOGGER = Logger.getLogger(ReservationModel.class.getName());

    public void loadReservationData() {
        try {
            // ... existing code ...
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading reservation data: " + ex.getMessage(), ex);
            System.err.println("예약 데이터 로드 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void processReservation() {
        try {
            // ... existing code ...
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing reservation: " + ex.getMessage(), ex);
            System.err.println("예약 처리 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
} 