import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DataServlet")
public class DataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection conn;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/your_database_name?useUnicode=true&characterEncoding=utf8", "your_username", "your_password"); // 替換成您的資料庫資訊
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Database connection failed: " + e.getMessage());
        }
    }

    public void destroy() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        try {
            String action = request.getParameter("action");
            if (action != null) {
                switch (action) {
                    case "list":
                        listData(request, response);
                        break;
                    case "add":
                        addData(request, response);
                        break;
                    case "update":
                        updateData(request, response);
                        break;
                    case "delete":
                        deleteData(request, response);
                        break;
                    case "checkIDNo": // 檢查身分證字號
                        checkIDNo(request, response);
                        break;
                    default:
                        response.getWriter().write("Invalid action");
                }
            } else {
                response.getWriter().write("Action parameter is missing");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }

    private void listData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM your_table_name")) { // 替換成您的資料表名稱
            try (ResultSet rs = pstmt.executeQuery()) {
                PrintWriter out = response.getWriter();

                out.println("<thead><tr>");
                out.println("<th>ID</th>");
                out.println("<th>IDNo</th>");
                out.println("<th>Name</th>");
                out.println("<th>PhoneNumber</th>");
                out.println("<th>Address</th>");
                out.println("<th>Notice</th>");
                out.println("<th>Actions</th>");
                out.println("</tr></thead><tbody>");

                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getInt("id") + "</td>");
                    out.println("<td>" + rs.getString("IDNo") + "</td>");
                    out.println("<td>" + rs.getString("name") + "</td>");
                    out.println("<td>" + rs.getString("phoneNumber") + "</td>");
                    out.println("<td>" + rs.getString("address") + "</td>");
                    out.println("<td>" + rs.getString("notice") + "</td>");
                    out.println("<td>");
                    out.println("<button class='btn btn-warning btn-sm mdy' data-id='" + rs.getInt("id") + "' data-idno='" + rs.getString("IDNo") + "' data-name='" + rs.getString("name") + "' data-phonenumber='" + rs.getString("phoneNumber") + "' data-address='" + rs.getString("address") + "' data-notice='" + rs.getString("notice") + "'>修改</button>");
                    out.println("<button class='btn btn-danger btn-sm del' data-id='" + rs.getInt("id") + "'>删除</button>");
                    out.println("</td>");
                    out.println("</tr>");
                }

                out.println("</tbody>");
            }
        }
    }

    private void addData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String idNo = request.getParameter("IDNo");
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String notice = request.getParameter("notice");

        String sql = "INSERT INTO your_table_name (IDNo, name, phoneNumber, address, notice) VALUES (?, ?, ?, ?, ?)"; // 替換成您的資料表名稱

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idNo);
            pstmt.setString(2, name);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, address);
            pstmt.setString(5, notice);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("inserted");
            } else {
                response.getWriter().write("Insert failed");
            }
        }
    }

    private void updateData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String idNo = request.getParameter("IDNo");
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String notice = request.getParameter("notice");

        String sql = "UPDATE your_table_name SET IDNo=?, name=?, phoneNumber=?, address=?, notice=? WHERE id=?"; // 替換成您的資料表名稱

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idNo);
            pstmt.setString(2, name);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, address);
            pstmt.setString(5, notice);
            pstmt.setInt(6, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("updated");
            } else {
                response.getWriter().write("Update failed");
            }
        }
    }

    private void deleteData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String sql = "DELETE FROM your_table_name WHERE id=?"; // 替換成您的資料表名稱

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("deleted");
            } else {
                response.getWriter().write("Delete failed");
            }
        }
    }

    private void checkIDNo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idNo = request.getParameter("IDNo");
        if (check(idNo)) { // 假設 check() 函數存在
            response.getWriter().write("valid");
        } else {
            response.getWriter().write("invalid");
        }
    }

    // ... (check() 函數的實作，這裡省略)
}
