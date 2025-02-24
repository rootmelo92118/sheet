import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AnimalServlet
 */
@WebServlet("/AnimalServlet") // 更正WebServlet路徑
public class AnimalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection conn;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/jq_sample?useUnicode=true&characterEncoding=utf8", "test", "test");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Database connection failed: " + e.getMessage()); // 抛出ServletException
        }
    }

    public void destroy() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        try {
            String action = request.getParameter("do");
            if (action != null) {
                switch (action) {
                    case "select":
                        selectAnimals(request, response);
                        break;
                    case "update":
                        updateAnimal(request, response);
                        break;
                    case "delete":
                        deleteAnimal(request, response);
                        break;
                    case "insert":
                        insertAnimal(request, response);
                        break;
                    default:
                        response.getWriter().write("Invalid action"); // 處理無效的 action
                }
            } else {
                response.getWriter().write("Action parameter is missing"); // 處理缺少 action 參數的情況
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Database error: " + e.getMessage()); // 將錯誤訊息回傳給前端
        }
    }

    private void selectAnimals(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int start = Integer.parseInt(request.getParameter("start"));
        String sql = "SELECT * FROM ajax_animal LIMIT " + start + ", 10";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) { // 使用 PreparedStatement

            PrintWriter out = response.getWriter();
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td class='name'>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getDouble("weight") + "</td>");
                out.println("<td>" + rs.getString("info") + "</td>");
                out.println("<td>" + rs.getTimestamp("date") + "</td>");
                out.println("<td>");
                out.println("<button class='mdy'>修改</button>");
                out.println("<button onclick='del(this)'>刪除</button>");
                out.println("</td>");
                out.println("</tr>");
            }
        }
    }


    private void updateAnimal(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        double weight = Double.parseDouble(request.getParameter("weight"));
        String info = request.getParameter("info");

        String sql = "UPDATE ajax_animal SET name=?, weight=?, info=?, date=NOW() WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, weight);
            pstmt.setString(3, info);
            pstmt.setInt(4, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = sdf.format(new Date());
                response.getWriter().write(formattedDate);
            } else {
                response.getWriter().write("Update failed"); // 處理更新失敗的情況
            }
        }
    }

    private void deleteAnimal(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String sql = "DELETE FROM ajax_animal WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("deleted");
            } else {
                response.getWriter().write("Delete failed"); // 處理刪除失敗的情況
            }
        }
    }

    private void insertAnimal(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String name = request.getParameter("name");
        double weight = Double.parseDouble(request.getParameter("weight"));
        String info = request.getParameter("info");

        String sql = "INSERT INTO ajax_animal (name, weight, info, date) VALUES (?, ?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, weight);
            pstmt.setString(3, info);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("inserted");
            } else {
                response.getWriter().write("Insert failed"); // 處理新增失敗的情況
            }
        }
    }
}
