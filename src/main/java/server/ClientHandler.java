package server;

import com.google.gson.Gson;
import controller.login.AuthController;
import model.user.User;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
  private Socket socket;
  private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    AuthController authController = new AuthController();
    try (
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
    ) {
      String requestJson = in.readLine();
      Gson gson = new Gson();
      Map<String, String> request = gson.fromJson(requestJson, Map.class);

      String type = request.get("type");
      Map<String, String> response = new HashMap<>();

      if ("login".equals(type)) {
        String id = request.get("id");
        String pw = request.get("password");
        User user = authController.login(id, pw);
        if (user != null) {
          response.put("result", "success");
          response.put("role", user.getRole());
          response.put("name", user.getName());
          response.put("department", user.getDepartment());
        } else {
          response.put("result", "fail");
        }
      } else if ("signup".equals(type)) {
        String id = request.get("id");
        String pw = request.get("password");
        String name = request.get("name");
        String dept = request.get("department");
        String role = request.get("role");

        User newUser = new User(id, pw, name, dept, role);
        boolean isSuccess = authController.register(newUser);
        response.put("result", isSuccess ? "success" : "fail");
      } else {
        response.put("result", "unknown_request");
      }
      out.println(new Gson().toJson(response));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error handling client request: " + e.getMessage(), e);
      System.err.println("클라이언트 요청 처리 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

