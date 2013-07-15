import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
public class html_extraction {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	    String temp_str="";   
	    Date dt = new Date();   
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");   
	    temp_str=sdf.format(dt); 
	    System.out.println(temp_str);
	}
}
