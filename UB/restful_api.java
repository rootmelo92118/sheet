import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.net.*;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.Base64;
import javax.net.ssl.*;
import org.json.*;

@Path("/api")
public class MemberAPI {
    private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/member_management?useUnicode=true&characterEncoding=utf8";
    private static final String DATABASE_USER = "rootmelo92118";
    private static final String DATABASE_PASSWORD = "password";
    private static final String DATA_TABLE = "member_data";
    private static final String LOGIN_TABLE = "login_data";
    private static final String BASE_URL_C1 = "https://172.16.45.135:443/EaiHub/resCommon/ZosCommon";
    private static final String BASE_URL_C2 = "https://172.16.45.135:443/EaiHub/resCommon/getAd01";
    private static final String BASIC_AUTH_CREDENTIALS = "newChallenger:newChallenger";

    // 忽略 SSL 憑證驗證
    private void disableSSLValidation() throws Exception {
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
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    // 成員列表
    @GET
    @Path("/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listData() {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM " + DATA_TABLE);
             ResultSet rs = pstmt.executeQuery()) {

            JSONArray members = new JSONArray();
            while (rs.next()) {
                JSONObject member = new JSONObject();
                member.put("IDNo", rs.getString("IDNo"));
                member.put("name", rs.getString("name"));
                member.put("phoneNumber", rs.getString("phoneNumber"));
                member.put("address", rs.getString("address"));
                member.put("notice", rs.getString("notice"));
                members.put(member);
            }
            return Response.ok(members.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error retrieving data: " + e.getMessage())
                           .build();
        }
    }

    // 查詢特定成員
    @GET
    @Path("/members/{idNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryData(@PathParam("idNo") String idNo) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM " + DATA_TABLE + " WHERE IDNo=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, idNo);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        JSONObject member = new JSONObject();
                        member.put("IDNo", rs.getString("IDNo"));
                        member.put("name", rs.getString("name"));
                        member.put("phoneNumber", rs.getString("phoneNumber"));
                        member.put("address", rs.getString("address"));
                        member.put("notice", rs.getString("notice"));
                        return Response.ok(member.toString()).build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).entity("Member not found").build();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error querying data: " + e.getMessage())
                           .build();
        }
    }

    // 插入新成員
    @POST
    @Path("/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertData(String data) {
        try (Connection conn = getConnection()) {
            JSONObject json = new JSONObject(data);
            String idNo = json.getString("IDNo");
            String sqlCheck = "SELECT * FROM " + DATA_TABLE + " WHERE IDNo=?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setString(1, idNo);
                ResultSet rs = pstmtCheck.executeQuery();
                if (rs.next()) {
                    return Response.status(Response.Status.CONFLICT).entity("IDNo already exists").build();
                }
            }

            String sqlInsert = "INSERT INTO " + DATA_TABLE + " (IDNo, name, phoneNumber, address, notice) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setString(1, json.getString("IDNo"));
                pstmt.setString(2, json.getString("name"));
                pstmt.setString(3, json.getString("phoneNumber"));
                pstmt.setString(4, json.getString("address"));
                pstmt.setString(5, json.getString("notice"));
                pstmt.executeUpdate();
                return Response.ok(json.toString()).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error inserting data: " + e.getMessage())
                           .build();
        }
    }

    // 更新成員
    @PUT
    @Path("/members/{idNo}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateData(@PathParam("idNo") String idNo, String data) {
        try (Connection conn = getConnection()) {
            JSONObject json = new JSONObject(data);
            String sql = "UPDATE " + DATA_TABLE + " SET name=?, phoneNumber=?, address=?, notice=? WHERE IDNo=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, json.getString("name"));
                pstmt.setString(2, json.getString("phoneNumber"));
                pstmt.setString(3, json.getString("address"));
                pstmt.setString(4, json.getString("notice"));
                pstmt.setString(5, idNo);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    return Response.ok("Updated successfully").build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity("Member not found").build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error updating data: " + e.getMessage())
                           .build();
        }
    }

    // 刪除成員
    @DELETE
    @Path("/members/{idNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteData(@PathParam("idNo") String idNo) {
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM " + DATA_TABLE + " WHERE IDNo=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, idNo);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    return Response.ok("Deleted successfully").build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity("Member not found").build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error deleting data: " + e.getMessage())
                           .build();
        }
    }

    // 登入檢查
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginCheck(String data) {
        try (Connection conn = getConnection()) {
            JSONObject json = new JSONObject(data);
            String userName = json.getString("userName");
            String password = json.getString("password");

            String sql = "SELECT password FROM " + LOGIN_TABLE + " WHERE account=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getString("password").equals(password)) {
                        return Response.ok("{\"loginResult\":true}").build();
                    } else {
                        return Response.ok("{\"loginResult\":false}").build();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error during login check: " + e.getMessage())
                           .build();
        }
    }

    @POST
    @Path("/customize1")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryViaHTTPPostRequestC1(String data) {
        try {
            disableSSLValidation(); // 忽略 SSL 憑證驗證
            JSONObject requestData = new JSONObject(data);
            String idNo = requestData.getString("IDNo");
    
            // 建立請求 JSON 資料
            JSONObject postData = new JSONObject("{\"TxnType\":\"ZQ952A100v00\",\"SV952A_TYPE\":\"00\"}");
            postData.put("SV952A_ID", idNo + "A");
    
            // 建立連線
            HttpURLConnection conn = createConnection(BASE_URL_C1, postData);
    
            // 讀取伺服器回應
            String responseStr = getResponseFromConnection(conn);
            JSONObject jsonResponse = new JSONObject(responseStr);
    
            // 處理回應資料
            JSONArray arrayBuilder = new JSONArray();
            String statusCode = jsonResponse.getJSONObject("result")
                                            .getJSONObject("data")
                                            .getJSONObject("HEAD")
                                            .getString("H_STUS");
            if ("0".equals(statusCode)) {
                String name = jsonResponse.getJSONObject("result")
                                          .getJSONObject("data")
                                          .getJSONObject("T9H1_DATA")
                                          .optString("NAME", "null");
    
                JSONArray t9h2DataArray = jsonResponse.getJSONObject("result")
                                                     .getJSONObject("data")
                                                     .getJSONArray("T9H2_DATA");
    
                for (int i = 0; i < t9h2DataArray.length(); i++) {
                    JSONObject eachJSONExport = new JSONObject();
                    JSONObject eachJSON = t9h2DataArray.getJSONObject(i);
                    eachJSONExport.put("NAME", name);
                    eachJSONExport.put("ACCOUNT", eachJSON.optString("ACCOUNT", "null"));
                    eachJSONExport.put("OPENDATE", eachJSON.optString("OPENDATE", "null"));
                    eachJSONExport.put("ACBAL", eachJSON.optString("ACBAL", "null"));
                    arrayBuilder.put(eachJSONExport);
                }
            }
    
            // 返回 JSON 結果
            JSONObject result = new JSONObject().put("data", arrayBuilder);
            return Response.ok(result.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error: " + e.getMessage())
                           .build();
        }
    }

    @POST
    @Path("/customize2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryViaHTTPPostRequestC2(String data) {
        try {
            disableSSLValidation(); // 忽略 SSL 憑證驗證
    
            // 解析請求中的數據
            JSONObject requestData = new JSONObject(data);
            String idNo = requestData.getString("IDNo");
            String password = requestData.getString("password");
    
            // 建立 POST 數據
            JSONObject postData = new JSONObject();
            postData.put("loginId", idNo);
            postData.put("loginP_ss", password);
    
            // 建立連線
            HttpURLConnection conn = createConnection(BASE_URL_C2, postData);
    
            // 讀取伺服器回應
            String responseStr = getResponseFromConnection(conn);
            JSONObject jsonResponse = new JSONObject(responseStr);
    
            // 處理回應資料
            JSONArray arrayBuilder = new JSONArray();
            JSONObject jsonExport = new JSONObject();
            String statusCode = jsonResponse.getString("rc2");
            jsonExport.put("rc2", statusCode);
            jsonExport.put("msg2", jsonResponse.getString("msg2"));
    
            if ("M000".equals(statusCode)) {
                String loginID = jsonResponse.getJSONObject("result")
                                             .getJSONObject("data")
                                             .getString("loginID");
                String displayName = jsonResponse.getJSONObject("result")
                                                 .getJSONObject("data")
                                                 .optString("displayName", "null");
    
                jsonExport.put("loginID", loginID);
                jsonExport.put("displayName", displayName);
            } else {
                jsonExport.put("loginID", "null");
                jsonExport.put("displayName", "null");
            }
    
            arrayBuilder.put(jsonExport);
    
            // 返回 JSON 結果
            JSONObject result = new JSONObject().put("data", arrayBuilder);
            return Response.ok(result.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error: " + e.getMessage())
                           .build();
        }
    }
}
