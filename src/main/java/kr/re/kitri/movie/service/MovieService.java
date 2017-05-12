package kr.re.kitri.movie.service;

import kr.re.kitri.movie.model.Item;
import kr.re.kitri.movie.util.MovieConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by danawacomputer on 2017-05-12.
 */
public class MovieService {

    public static String CLIENT_ID = "TVcdGN3l5f8ZIswuZ4tX";//애플리케이션 클라이언트 아이디값";
    public static String CLIENT_SECRET = "4F9aWQDSdZ";//애플리케이션 클라이언트 시크릿값";

    public MovieService() {
        try {
            Class.forName(MovieConstants.DRIVER_POSTGRES);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getKeywordFromUser() {

        System.out.print("키워드를 입력하세요 $> ");
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    public String getJsonFromNaverMovieApi(String keyword) {
        try {
            String text = URLEncoder.encode(keyword, "UTF-8");
            String apiURL =
                    "https://openapi.naver.com/v1/search/movie.json?query=" + text + "&display=100";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }

    }


    public String getLastBuildDateFromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getString("lastBuildDate");
    }

    public int getTotalFromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getInt("total");
    }

    public List<Item> getItemListFromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");

        List<Item> list = new ArrayList<>();

        Item item = null;
        for (Object i : items) {
            item = new Item();
            JSONObject itemObj = (JSONObject)i;
            item.setTitle(itemObj.getString("title"));
            item.setLink(itemObj.getString("link"));
            item.setImage(itemObj.getString("image"));
            item.setSubtitle(itemObj.getString("subtitle"));
            item.setPubDate(LocalDate.of(itemObj.getInt("pubDate"), 1, 1));
            item.setDirector(itemObj.getString("director"));
            item.setActor(itemObj.getString("actor"));
            item.setUserRating(itemObj.getDouble("userRating"));

            list.add(item);
        }

        return list;
    }

    public void insertDataToPostgres(String lastBuildDate, int total, String keyword, List<Item> list) {

        String insertSearch =
                "INSERT INTO search (last_build_date, total, keyword)\n" +
                "VALUES (?, ?, ?);";
        String insertItems =
                "INSERT INTO items \n" +
                        "(title, link, image, subtitle, pub_date, director, actor, user_rating, search_id)\n" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = this.getConnection();

        if (conn == null) {
            System.out.println("커넥션 연결 실패 프로그램 종료합니다.");
            return;
        }

        try {
            PreparedStatement pstmt1 = conn.prepareStatement(insertSearch);
            pstmt1.setString(1, lastBuildDate);
            pstmt1.setInt(2, total);
            pstmt1.setString(3, keyword);
            pstmt1.executeUpdate();
            System.out.println("insert search table ok..");

            int searchId = this.getSearchId(lastBuildDate, total, keyword);

            PreparedStatement pstmt2 = conn.prepareCall(insertItems);
            for (Item item : list) {
                pstmt2.setString(1, item.getTitle());
                pstmt2.setString(2, item.getLink());
                pstmt2.setString(3, item.getImage());
                pstmt2.setString(4, item.getSubtitle());
                pstmt2.setDate(5, Date.valueOf(item.getPubDate()));
                pstmt2.setString(6, item.getDirector());
                pstmt2.setString(7, item.getActor());
                pstmt2.setDouble(8, item.getUserRating());
                pstmt2.setDouble(9, searchId);
                pstmt2.executeUpdate();
                System.out.println("insert item table ok..");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private int getSearchId(String lastBuildDate, int total, String keyword) throws SQLException {
        String query = "SELECT search_id\n" +
                "FROM   search\n" +
                "WHERE last_build_date = ?\n" +
                "AND   keyword = ?\n" +
                "AND   total = ?";

        Connection conn = this.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, lastBuildDate);
        pstmt.setString(2, keyword);
        pstmt.setInt(3, total);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    private Connection getConnection() {

        try {
            return DriverManager.getConnection(
                    MovieConstants.DB_URL, MovieConstants.USERNAME, MovieConstants.PASSWORD
            );
        } catch (SQLException e) {
            return null;
        }
    }
}
