package javaapplication1; 
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter; 
import java.util.Iterator;
import java.util.LinkedHashMap; 
import java.util.Map; 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.URL;
import java.net.HttpURLConnection;

public class CodingChallenge extends Thread { 
 
    public void run() {

        // declare all variables
        URL url;
        String path;
        int size;
        boolean validate_url;
        int invalid_url_count = 0;
        int invalid_size_count = 0;
        long file_size;
        Map map = new LinkedHashMap(2);
        JSONObject jo = new JSONObject(); 

        try {
            JSONArray data = (JSONArray) parser.parse(new FileReader("input.json"));

            for (Object o : data)
            {
                // read from existing json
                JSONObject site = (JSONObject) o;

                // add data to variables
                url = URL(site.get("url"));
                System.out.println(url);

                path = site.get("path");
                System.out.println(path);

                size = (int) site.get("size");
                System.out.println(size);

                // check if url is valid
                validate_url = doesURLExist(url);
                if (validate_url == false) {
                    System.out.println(url + " is not a valid url!");
                    invalid_url_count++;
                }
                
                // check if declared file size is correct
                file_size = getFileSize(url);
                if (file_size != size) { // int automatically gets converted to long
                    System.out.println(url + " has file size " + file_size +" rather than " + size);
                    invalid_size_count++;
                }

                // write to new json
                  
                // for fields within path, add them to a LinkedHashMap
                map.clear();
                map.put("url", url); 
                map.put("size", size); 
                  
                // add the data to a JSONObject 
                jo.put(path, map);   
            }
        // catch possible errors
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // writing JSON to file:"Result.json" 
        PrintWriter pw = new PrintWriter("result.json"); 
        pw.write(jo.toJSONString()); 
          
        pw.flush(); 
        pw.close(); 

        // print out result
        if (invalid_url_count > 0) {
            System.out.println ("There are " + invalid_url_count + " invalid URLs.");
        } else {
            System.out.println ("Congrats! There are no invalid URLs.");
        }
        if (invalid_size_count > 0) {
            System.out.println ("There are " + invalid_url_count + " files with the incorrect size.");
        } else {
            System.out.println ("Congrats! There are no files with the incorrect size.");
        }
    }

    public static boolean doesURLExist(URL url) throws IOException
    {
        // check the current URL
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("HEAD");

        // pretend to be a browser to prevent programmatic access errors 
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
        int responseCode = httpURLConnection.getResponseCode();

        // return whether or not response code is 200
        return responseCode == HttpURLConnection.HTTP_OK;
    }

    public static long getFileSize(URL url) {
      HttpURLConnection connection = null;
      try {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        return connection.getContentLengthLong(); // "long" includes files > 2 GB
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }
}