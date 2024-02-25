package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



    public class DataSource {
        private Connection cnx;
        private  String url ="jdbc:mysql://localhost:3306/matchmate";
        private  String login = "root";
        private  String pwd ="";
        public static utils.DataSource Instance;



        private DataSource() {
            try {
                cnx = DriverManager.getConnection(url, login, pwd);
                System.out.println("SUCCESS");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static utils.DataSource getInstance()
        {
            if(Instance==null)
                Instance=new utils.DataSource();
            return  Instance;

        }

        public  Connection getCnx() {
            return cnx;
        }
    }


