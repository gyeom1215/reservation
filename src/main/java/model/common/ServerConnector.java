import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnector {
    private static final Logger LOGGER = Logger.getLogger(ServerConnector.class.getName());

    public void connect() {
        try {
            // ... existing code ...
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to server: " + ex.getMessage(), ex);
            System.err.println("서버 연결 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
} 