package jdbc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class test {
public static void main(String[] args) {
tools t=new tools();
t.initConnection();
FileWriter fwriter;

try {
	
fwriter = new FileWriter("D:/output.txt");
tools.write("<html lang='en'> <head> <meta charset='UTF-8'> <title>Title</title> </head> <body><br><br> ", fwriter);

for (int a=1;a<=100;a++) {
	
String html=tools.getHtml(a);
List<Film> films=tools.parseHtml(html);

for (Film item :films) {
	t.add(item.getName(),item.getGrade());
	System.out.println(item.getName()+"\t"+item.getGrade());
}
tools.writeToFile(films, fwriter);
}

tools.write("<script>\r\n"
		+ "	var cover =document.getElementsByClassName(\"cover\");\r\n"
		+ "	var image =document.getElementsByClassName(\"image\");\r\n"
		+ "	for (let i=0;i<=cover.length;i++){\r\n"
		+ "image[i].width='90'\r\n"
		+ "image[i].height='120'\r\n"
		+ "cover[i].width='210'\r\n"
		+ "cover[i].height='280'\r\n"
		+ "}\r\n"
		+ "</script>", fwriter);
tools.write("</body> </html>", fwriter);


} catch (IOException e) {
	e.printStackTrace();
}
t.closeConnection();
	}
	
}
