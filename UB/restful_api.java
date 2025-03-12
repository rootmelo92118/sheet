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
import javax.json.*;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Restful API implementation
 */
@WebServlet("/api/*") // 使用 /api/* 作為 API 的基本路徑
public class RestfulApi extends HttpServlet {

    private String dataTableName = "member_data";
    private String loginTableName = "login_data";
    private static final long serialVersionUID = 1L;
    private Connection conn;

    public RestfulApi() throws ServletException {
        super();
        init();
    }

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/member_management?useUnicode=true&characterEncoding=utf8", "rootmelo92118", "password"); // 替換成您的資料庫資訊
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo != null ? pathInfo.split("/") : new String[0];
        String resource = pathParts.length > 1 ? pathParts[1] : null;

        try {
            if ("members".equals(resource)) {
                handleMembers(request, response, pathParts);
            } else if ("login".equals(resource)) {
                handleLogin(request, response, pathParts);
            } else if ("custom1".equals(resource)){
                queryViaHTTPPostRequestC1(request, response);
            } else if ("custom2".equals(resource)){
                queryViaHTTPPostRequestC2(request, response);
            }else if ("idCheck".equals(resource)){
                checkIDNo(request,response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(new JSONObject().put("error", "Resource not found").toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject().put("error", "Database error: " + e.getMessage()).toString());
        }
    }

    private void handleMembers(HttpServletRequest request, HttpServletResponse response, String[] pathParts) throws SQLException, IOException {
        String idNo = pathParts.length > 2 ? pathParts[2] : null;

        if (request.getMethod().equals("GET")) {
            if (idNo != null) {
                queryData(request, response, idNo);
            } else {
                listData(request, response);
            }
        } else if (request.getMethod().equals("POST")) {
            insertData(request, response);
        } else if (request.getMethod().equals("PUT")) {
            updateData(request, response);
        } else if (request.getMethod().equals("DELETE")) {
            deleteData(request, response, idNo);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, String[] pathParts) throws SQLException, IOException {
        if (request.getMethod().equals("POST")) {
            loginCheck(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void listData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        // ... (與原程式碼相同)
    }

    private void queryData(HttpServletRequest request, HttpServletResponse response, String idNo) throws SQLException, IOException {
        // ... (與原程式碼相同)
    }

    private void insertData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        // ... (與原程式碼相同)
    }

    private void updateData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        // ... (與原程式碼相同)
    }

    private void deleteData(HttpServletRequest request, HttpServletResponse response, String idNo) throws SQLException, IOException {
        // ... (與原程式碼相同)
    }
    private void checkIDNo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //... (與原程式碼相同)
    }
    private void loginCheck(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        //... (與原程式碼相同)
    }
    private void queryViaHTTPPostRequestC1(HttpServletRequest request, HttpServletResponse response) {
        //... (與原程式碼相同)
    }
    private void queryViaHTTPPostRequestC2(HttpServletRequest request, HttpServletResponse response) {
        //... (與原程式碼相同)
    }
}
