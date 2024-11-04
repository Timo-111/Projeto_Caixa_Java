package projetocaixagui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class conexaoMysql {

    public static String URL = "jdbc:mysql://localhost:3306/javaproj"; //javaproj e o nome do database que eu criei pro projeto
    public static String USER = "root";
    public static String PWD = ""; //a senha tava como root, mas o padrao do xampp e senha vazia

    //objetos de conexão
    private Connection dbconn = null;
    private Statement sqlmgr = null;
    private ResultSet resultsql = null;

    public boolean OpenDatabase(){
        try{
			//declarar a biblioteca jdbc, importante pra ligar o java com o sql
          Class.forName("com.mysql.cj.jdbc.Driver");//com.mysql.jdbc.Driver is deprecated, use com.mysql.cj.jdbc.Driver
  
            
          dbconn = DriverManager.getConnection(URL, USER, PWD);
          System.out.println("Conectado com sucesso\n" + URL);
          sqlmgr = dbconn.createStatement();//cria objeto para SQLs
          System.out.println(sqlmgr);
          return true;

        }catch(Exception Error){
            System.out.println("Erro on connect: \n"+Error); //acho melhor printar o erro sem o .getMessage() porque descreve mais
        }
        return false;
    }
    
    public void CloseDatabase() throws SQLException{
    
        sqlmgr.close();
        dbconn.close();

    }
    // função final
    //Retorna o total de registros afetados ou -1 caso ocorra algum erro
    public int ExecuteQuery(String sql){
        try{
         //return sqlmgr.executeUpdate(sql); //Insert /Delete /Update /Create
         //resultsql = sqlmg.executeUpdate(sql);
         
         resultsql = sqlmgr.executeQuery(sql);
         
         if (resultsql != null) {
             //resultsql = sqlmgr.getResultSet();
             System.out.println("\n"+resultsql.toString()+"\n");
             
           // System.out.println(resultsql.getString(1).toString());
           String ris = null;
           int id = 0;
           
           ResultSetMetaData info = resultsql.getMetaData();
           
           boolean first=false;
           
           if (first == false && !resultsql.isBeforeFirst()) {
                    return -1; //retorna -1 se vazio
                }
           
            while (resultsql.next()) {
                
                for (int i=1;i<info.getColumnCount()+1;i++) {
                /*ris=resultsql.getString(info.getColumnName(i));
                id=resultsql.getInt("id_categoria");
                System.out.println(ris);
                System.out.println(id);*/
                //System.out.println(info.getColumnName(i));
                System.out.println(resultsql.getString(info.getColumnName(i)));// getColumnName(i));
				
				//quando voce faz consulta com sql, pode pedir pra converter tudo pra string que da certo, nao precisa saber os tipos de dados ao certo, claro que so quando for fazer o total etc
                }
            }
            
            
             /*List rows = new ArrayList();
             while (resultsql.next()) {
                 String[] row = new String[2];
                 for (int i=1;i<=2;i++) {
                     row[i-1]=resultsql.getString(i);
                 }
                 rows.add(row);
             }*/
             //System.out.println(rows.toString());
             return 0;
         }
        }catch(Exception Error){
            System.out.println("Erro on query: \n"+Error);
        }
       return -1;
    }
    
    public int ExecuteUpdate(String sql){
        try{
         //return sqlmgr.executeUpdate(sql); //Insert /Delete /Update /Create
         //resultsql = sqlmg.executeUpdate(sql);
         
         int resultint = sqlmgr.executeUpdate(sql);
            
         if (resultint!=-1) {
             return 0;
         }
        }catch(Exception Error){
            System.out.println("Erro on query: \n"+Error);
        }
       return -1;
    }

}
