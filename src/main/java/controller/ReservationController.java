import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationController {

    private static final Logger LOGGER = Logger.getLogger(ReservationController.class.getName());

    public void handleReservation() {
        try {
            // ... existing code ...
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing reservation: " + ex.getMessage(), ex);
            System.err.println("예약 처리 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
} 