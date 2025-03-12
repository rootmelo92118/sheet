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
 * Servlet implementation class servlet
 */
@WebServlet("/servlet")
public class servlet extends HttpServlet {
	private String dataTableName = "member_data";
	private String loginTableName = "login_data";
	private static final long serialVersionUID = 1L;
	private Connection conn;
       
    /**
     * @throws ServletException 
     * @see HttpServlet#HttpServlet()
     */
    public servlet() throws ServletException {
        super();
        init();
        // TODO Auto-generated constructor stub
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
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
                    case "insert":
                        insertData(request, response);
                        break;
                    case "delete":
                        deleteData(request, response);
                        break;
                    case "query":
                        queryData(request, response);
                        break;
                    case "update":
                        updateData(request, response);
                        break;
                    case "checkIDNo": // 檢查身分證字號
                        checkIDNo(request, response);
                        break;
                    case "loginCheck":
                        loginCheck(request, response);
                        break;
                    case "costumize1":
                    	queryViaHTTPPostRequestC1(request, response);
                        break;
                    case "costumize2":
                    	queryViaHTTPPostRequestC2(request, response);
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
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM " + dataTableName)) { // 替換成您的資料表名稱
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObjectBuilder objBuilder = Json.createObjectBuilder();
                    objBuilder.add("IDNo", rs.getString("IDNo"));
                    objBuilder.add("name", rs.getString("name"));
                    objBuilder.add("phoneNumber", rs.getString("phoneNumber"));
                    objBuilder.add("address", rs.getString("address"));
                    objBuilder.add("notice", rs.getString("notice"));
                    arrayBuilder.add(objBuilder);
                }
            }
        }

        JsonObject json = Json.createObjectBuilder()
                             .add("data", arrayBuilder)
                             .build();

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
    private void queryData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String idNo = request.getParameter("IDNo");
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String sql = "SELECT * FROM " + dataTableName + " WHERE IDNo=?";

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) { // 替換成您的資料表名稱
        	pstmt.setString(1, idNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObjectBuilder objBuilder = Json.createObjectBuilder();
                    objBuilder.add("IDNo", rs.getString("IDNo"));
                    objBuilder.add("name", rs.getString("name"));
                    objBuilder.add("phoneNumber", rs.getString("phoneNumber"));
                    objBuilder.add("address", rs.getString("address"));
                    objBuilder.add("notice", rs.getString("notice"));
                    arrayBuilder.add(objBuilder);
                }
            }
        }

        JsonObject json = Json.createObjectBuilder()
                             .add("data", arrayBuilder)
                             .build();

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    private void insertData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String idNo = request.getParameter("IDNo");
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String notice = request.getParameter("notice");

        String sqlV = "SELECT * FROM " + dataTableName + " WHERE IDNo=?";

        try (PreparedStatement pstmtV = conn.prepareStatement(sqlV)) {
            pstmtV.setString(1, idNo);
            ResultSet rs = pstmtV.executeQuery();

            if (rs.next()) {
                response.getWriter().write("Insert failed");
            } else {
                String sql = "INSERT INTO " + dataTableName + " (IDNo, name, phoneNumber, address, notice) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, idNo);
                    pstmt.setString(2, name);
                    pstmt.setString(3, phoneNumber);
                    pstmt.setString(4, address);
                    pstmt.setString(5, notice);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        response.getWriter().write("Inserted");
                    } else {
                        response.getWriter().write("Insert failed");
                    }
                }
            }
        }
    }


    private void updateData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String idNo = request.getParameter("IDNo");
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String notice = request.getParameter("notice");

        String sql = "UPDATE " + dataTableName + " SET IDNo=?, name=?, phoneNumber=?, address=?, notice=? WHERE IDNo=?"; // 替換成您的資料表名稱

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idNo);
            pstmt.setString(2, name);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, address);
            pstmt.setString(5, notice);
            pstmt.setString(6, idNo);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("updated");
            } else {
                response.getWriter().write("Update failed");
            }
        }
    }

    private void deleteData(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String idNo = request.getParameter("IDNo");
        String sql = "DELETE FROM " + dataTableName + " WHERE IDNo=?"; // 替換成您的資料表名稱

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idNo);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("deleted");
            } else {
                response.getWriter().write("Delete failed");
            }
        }
    }

    private void checkIDNo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        IdChecker idChecker = new IdChecker();
        String idNo = request.getParameter("IDNo");
        if (idChecker.check(idNo)) { // 假設 check() 函數存在
            response.getWriter().write("valid");
        } else {
            response.getWriter().write("invalid");
        }
    }
    
    private void loginCheck(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
    	Boolean result = false;
    	String userName = request.getParameter("userName");
    	String password = request.getParameter("password");
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM " + loginTableName + " WHERE account=?")) { // 替換成您的資料表名稱
        	pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
            	 while (rs.next()) {
            		 JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            		 System.out.println("PasswordHashInput : " + password);
            		 System.out.println("PasswordHashSQL : " + rs.getString("password"));
            		 String SQLPassword = rs.getString("password");
	                 if(password.equals(SQLPassword)) {
	                 	result = true;
	                 }else {
	                 	result = false;
	                 }
	                 System.out.println(result);
	                 objBuilder.add("loginResult", result);
                     arrayBuilder.add(objBuilder);
                 }
            }
        }

        JsonObject json = Json.createObjectBuilder()
                             .add("data", arrayBuilder)
                             .build();
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
        
    }
    
    private void queryViaHTTPPostRequestC1(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 忽略 SSL 憑證驗證
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            String idNo = request.getParameter("IDNo");
            URL url = new URL("https://172.16.45.135:443/EaiHub/resCommon/ZosCommon");
            String upStreamData = "{\"TxnType\":\"ZQ952A100v00\",\"SV952A_TYPE\":\"00\"}";
            JSONObject postData = new JSONObject(upStreamData);
            postData.put("SV952A_ID", idNo + "A");

            JSONArray arrayBuilder = new JSONArray();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            
            // Add basic authentication
            String userCredentials = "newChallenger:newChallenger";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            conn.setRequestProperty("Authorization", basicAuth);
            
            conn.setRequestProperty("Content-Length", Integer.toString(postData.toString().length()));
            conn.setUseCaches(false);

            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.writeBytes(postData.toString());
            }

            StringBuilder responses = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    responses.append(line);
                }
            }

            JSONObject jsonResponse = new JSONObject(responses.toString());
            String statusCode = jsonResponse.getJSONObject("result")
                                            .getJSONObject("data")
                                            .getJSONObject("HEAD")
                                            .getString("H_STUS");
            if(statusCode.equals("0")){
            	System.out.println("status : " + statusCode);
            	String name = jsonResponse.getJSONObject("result")
                    .getJSONObject("data")
                    .getJSONObject("T9H1_DATA")
                    .optString("NAME", "null");
				JSONArray t9h2DataArray = jsonResponse.getJSONObject("result")
				                                .getJSONObject("data")
				                                .getJSONArray("T9H2_DATA");
				
				for(int i = 0 ; i < t9h2DataArray.length() ; i+=1) {
					JSONObject eachJSONExport = new JSONObject();
					JSONObject eachJSON = t9h2DataArray.getJSONObject(i);
					eachJSONExport.put("NAME", name);
					eachJSONExport.put("ACCOUNT", eachJSON.optString("ACCOUNT", "null"));
					eachJSONExport.put("OPENDATE", eachJSON.optString("OPENDATE", "null"));
					eachJSONExport.put("ACBAL", eachJSON.optString("ACBAL", "null"));
					arrayBuilder.put(eachJSONExport);
				}
			}
            
            JSONObject json = new JSONObject().put("data", arrayBuilder);

            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void queryViaHTTPPostRequestC2(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 忽略 SSL 憑證驗證
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            String idNo = request.getParameter("IDNo");
            String password = request.getParameter("password");
            URL url = new URL("https://172.16.45.135:443/EaiHub/resCommon/getAd01");
            JSONObject postData = new JSONObject();
            postData.put("loginId", idNo);
            postData.put("loginP_ss", password);

            JSONArray arrayBuilder = new JSONArray();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            
            // Add basic authentication
            String userCredentials = "newChallenger:newChallenger";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            conn.setRequestProperty("Authorization", basicAuth);
            
            conn.setRequestProperty("Content-Length", Integer.toString(postData.toString().length()));
            conn.setUseCaches(false);

            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.writeBytes(postData.toString());
            }

            StringBuilder responses = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    responses.append(line);
                }
            }

            JSONObject jsonResponse = new JSONObject(responses.toString());
            JSONObject jsonExport = new JSONObject();
            String statusCode = jsonResponse.getString("rc2");
            jsonExport.put("rc2", statusCode);
            jsonExport.put("msg2", jsonResponse.getString("msg2"));
            if(statusCode.equals("M000")){
            	System.out.println("status : " + statusCode);
            	String loginID = jsonResponse.getJSONObject("result")
                                             .getJSONObject("data")
                                             .getString("loginID");
				String displayName = jsonResponse.getJSONObject("result")
                                                 .getJSONObject("data")
                                                 .optString("displayName", "null");

				jsonExport.put("loginID", loginID);
				jsonExport.put("displayName", displayName);
				
			}else {
				jsonExport.put("loginID", "null");
				jsonExport.put("displayName", "null");
			}
            arrayBuilder.put(jsonExport);
            JSONObject json = new JSONObject().put("data", arrayBuilder);

            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
