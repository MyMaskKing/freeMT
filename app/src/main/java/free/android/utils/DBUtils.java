package free.android.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtils {
    private static String driver = "com.mysql.cj.jdbc.Driver";// MySql驱动

    private static String url = "jdbc:mysql://127.0.0.1:2022/freePj?characterEncoding=utf-8&serverTimezone=UTC&useSSL=false";

    private static String user = "root";// 用户名

    private static String password = "root";// 密码

    Connection conn = null;
    public DBUtils(){
        try {
            // DBの接続がNULLの場合、新規する
            if (conn == null) {
                Class.forName(driver);
                conn = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
