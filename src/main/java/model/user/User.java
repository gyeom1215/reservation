package model.user;

public class User {
  private String id;
  private String password;
  private String name;
  private String department;
  private String role;

  public User(String id, String password, String name, String department, String role) {
    this.id = id;
    this.password = password;
    this.name = name;
    this.department = department;
    this.role = role;
  }

  public User(String id, String password, String name, String role) {
    this.id = id;
    this.password = password;
    this.name = name;
    this.role = role;
  }

  // Getter 메서드 추가
  public String getId() { return id; }
  public String getPassword() { return password; }
  public String getName() { return name; }
  public String getDepartment() { return department; }
  public String getRole() { return role; }

  public void setId(String id) {
    this.id = id;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
