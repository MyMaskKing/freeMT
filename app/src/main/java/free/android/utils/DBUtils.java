package free.android.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

/**
 * 数据库工具类：连接数据库用、获取数据库数据用
 * 相关操作数据库的方法均可写在该类
 */
public class DBUtils {

    private static String driver = "com.mysql.cj.jdbc.Driver";// MySql驱动

//    private static String url = "jdbc:mysql://localhost:3306/map_designer_test_db";

    private static String user = "root";// 用户名

    private static String password = "root";// 密码

    private static Connection getConn(){
        String dbName = "freePj";
        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
            String ip = "192.168.1.7";// 写成本机地址，不能写成localhost，同时手机和电脑连接的网络必须是同一个

            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:2022/freePj?characterEncoding=utf-8&serverTimezone=UTC&useSSL=false",
                    user, password);
            System.out.print("==================================");
            System.out.print(connection);
        }catch (Exception e){
            e.printStackTrace();
        }

        return connection;
    }

    public static void getInfoByName() {

        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn();
    }


}
