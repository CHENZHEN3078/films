package jdbc;
import java.io.BufferedReader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class tools {
	
	Connection conn;
	
	public void initConnection() {
	try {
		Class.forName("com.mysql.cj.jdbc.Driver");
	} catch (Exception e) {
		e.printStackTrace();
	}
	try {
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/filmdata?serverTimezone=UTC&useSSL=false","root","chenzhen2003");
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	
	
	public void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
				conn=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void write(String s,FileWriter fwriter ) {
		PrintWriter pWriter=new PrintWriter(fwriter);
		pWriter.println(s);
		pWriter.flush();
	}
	
	public static void writeToFile(List<Film> films,FileWriter fwriter ) {
		PrintWriter pWriter=new PrintWriter(fwriter);
		pWriter.println("<div>");
		for (Film item :films) {
			pWriter.println(item.getCover()+"<br><br>");
			pWriter.println("<h3>"+item.getName()+"</h3><br>");
			pWriter.println("评分: "+item.getGrade()+"<br>");
			pWriter.println("类型: "+item.getType()+"<br>");
			pWriter.println("其他: "+item.getLoca()+"<br>");
			//pWriter.println("上映时间: "+item.getTime()+"<br>");
			pWriter.println("导演:"+"<br><br>");
			pWriter.println(item.getImg());
			pWriter.println("<br><hr><br><br>");
		}
		pWriter.println("</div>");
		pWriter.flush();
	}
	

	public void add(String name, String grade) {
		try {
			String sql = "insert into film (name, grade) values(?,?) ";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name); 
			ps.setString(2, grade); 
			 
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void paging(int page) {
		try {
			int offset=10 * (page - 1);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT name,grade\r\n"
					+ "FROM film\r\n"
					+ "LIMIT 10 OFFSET "+ offset+";");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static String getHtml(int a) {
		StringBuffer buffer=new StringBuffer();
		InputStream in=null;
		InputStreamReader inReader=null;
		BufferedReader bReader=null;
		try {
			URL url=new URL("https://ssr1.scrape.center/detail/"+a);
			URLConnection conn=url.openConnection();
			in=conn.getInputStream();
			inReader=new InputStreamReader(in,"utf-8");
			bReader=new BufferedReader(inReader);
			String line="";
			while((line=bReader.readLine())!=null) {
				buffer.append(line);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				in.close();
				inReader.close();
				bReader.close();
			}catch(IOException e) {
				e.printStackTrace();
				}
		}
		return buffer.toString();
	}	

	
	
	public static List<Film> parseHtml(String html){
		List<Film> films=new ArrayList<>();
		Document document=Jsoup.parse(html);
		Element div=  document.getElementById("detail");
		Elements tables =div.getElementsByClass("m-t");
		Film item=null;
		
		for (int i=0;i<tables.size();i++) {
			item=new Film();
			Element table=tables.get(i);
			
			String name =table.getElementsByClass("router-link-exact-active router-link-active").text();
			String time =table.getElementsByClass("m-v-sm info").text();
			String loca =table.getElementsByClass("m-v-sm info").text();
			String type =table.getElementsByClass("categories").text();
			String grade =table.getElementsByClass("score m-t-md m-b-n-sm").text();
			String dir =table.getElementsByClass("name text-center m-b-none m-t-xs").text();
			String cover =table.getElementsByClass("cover").toString();
			String img =table.getElementsByClass("director el-col el-col-4").toString();
			
			item.setImg(img);
			item.setName(name);
			item.setGrade(grade);
			item.setType(type);
			item.setTime(time);
			item.setLoca(loca);
			item.setDir(dir);
			item.setCover(cover);
			
			
			films.add(item);
		}
		return films;
	}
	
}








