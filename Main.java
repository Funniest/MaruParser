/**
 * Created by Minsungkim on 2017-03-26.
 */

import org.jsoup.Jsoup; //import Jsoup
import org.jsoup.nodes.Document;  //import Jsoup
import org.jsoup.select.Elements;  //import Jsoup
import org.jsoup.nodes.Element; //import Jsoup

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class Main {
    public static void getImage(String link, String name, String path){
        String originUrl = "http://wasabisyrup.com";

        try {
            Document doc = Jsoup.connect(link).get(); //웹에서 내용을 가져온다.
            Elements contents = doc.select("img[data-src]"); //내용 중에서 원하는 부분을 가져온다.

            int pageCount = 0;
            for (Element content : contents){
                String linkSrc = content.attr("data-src");
                linkSrc = originUrl + linkSrc;
                System.out.println("[" + pageCount + "] Image URL : " + linkSrc);

                //image download
                URL url = new URL(linkSrc);
                System.setProperty("http.agent", "Chrome");
                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");

                httpcon.setConnectTimeout( 2000 );
                httpcon.setReadTimeout( 2000 );

                httpcon.setUseCaches(true);
                httpcon.setRequestMethod( "GET" );

                httpcon.setRequestProperty("Connection", "Keep-Alive" );
                httpcon.setRequestProperty("Content-Type", "image/png");

                httpcon.connect();

                int response = httpcon.getResponseCode();
                if( response == HttpURLConnection.HTTP_OK ){
                    InputStream is = httpcon.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(path + String.valueOf(pageCount) + ".jpg");

                    if ("gzip".equals(httpcon.getContentEncoding())){
                        System.out.println("zipped image");
                        is = new GZIPInputStream(is);
                    }

                    // opens an output stream to save into file
                    int bytesRead = -1;
                    byte[] buffer = new byte[2048];
                    while ((bytesRead = is.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    outputStream.close();

                }

                pageCount++;
            }
        } catch (IOException e) { //Jsoup의 connect 부분에서 IOException 오류가 날 수 있으므로 사용한다.
            e.printStackTrace();
        }

        System.out.println("   ========      ========      ========   ");
    }

    public static void main(String []args){
        File mainFolder;
        String mkFolder = "OnePanchMen";
        String target = "http://marumaru.in/b/manga/65484";

        try{
            mainFolder = new File(mkFolder);

            if(!mainFolder.exists()){
                mainFolder.mkdir();
                System.out.println("Create Folder! is " + mainFolder.getPath());
            }

            mkFolder = mainFolder.getPath();

            Document doc = Jsoup.connect(target).get(); //마루마루의 보고 싶은 만화 URL
            Elements contents = doc.select("a[target=\"_blank\"]"); //내용 중에서 원하는 부분을 가져온다.

            for(Element content : contents){
                String name = content.text();
                String link = content.attr("href");
                System.out.println("Name : " + name + ", URL : " + link);

                mainFolder = new File(mkFolder + "\\" + name);
                if(!mainFolder.exists()){
                    mainFolder.mkdir();
                    //Get Image.
                    getImage(link, name, mainFolder.getPath()+"\\");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
