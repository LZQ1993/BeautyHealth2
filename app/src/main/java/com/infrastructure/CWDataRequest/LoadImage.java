package com.infrastructure.CWDataRequest;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

 
public class LoadImage {
 
    private static final int SUCCES = 1;//下载成功
    private static final int ERRO = -1;//下载失败
    private static final int EXIST = 0;//文件已存在
    private static int result = EXIST;//返回的下载结果
    private String localPath;//本地路径
    
    public LoadImage(String localPath) {
        this.localPath = localPath;
       
    }
 
    /**
     * @param urlPathPrefix       网址前缀
     * @param filename            文件名
     * @param size                文件大小
     * @return
     * @throws IOException
     */
    public int download(String urlPathPrefix, String filename, long size) throws IOException{
    	
    	String filePath = localPath + File.separator + filename.substring(filename.lastIndexOf("/") + 1);
        //判断filePath路径下有没有此图片存在，大小是否相同，如果不存在，则发送Http请求下载图片，存在则不下载
        if(isFileExist(filePath) && isSizeSame(filePath, size)){
            return result = EXIST;
        }else{
            //从URL中取得输入流
        	String filename1=filename.substring(2).trim();
            InputStream is = getHttpInputStream(urlPathPrefix + filename1);
            //创建新文件
            File file = createFile(filePath);
            //下载图片
            if(is != null) downLoadImage(file, is);  
        }
        return result;
    }
 
    /**
     * 下载图片
     * @param file  文件
     * @param is    从URL取得的输入流
     */
    private void downLoadImage(File file, InputStream is){
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int len = 0;
            while((len = is.read(buffer)) != -1){
                fs.write(buffer, 0, len);
            }
            fs.flush();
            result = SUCCES;
        } catch (Exception e) {
            result = ERRO;
            e.printStackTrace();
        }finally{
            try {
                if(fs != null){
                    fs.close();
                }
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * 根据URL取得输入流
     * @param urlPath   网络路径
     * @return
     * @throws IOException
     */
    private InputStream getHttpInputStream(String urlPath) throws IOException{
        URL url = new URL(urlPath);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        if(conn.getResponseCode() == HttpStatus.SC_OK) {
            return conn.getInputStream();
        }
        return null;
		
        
    }
 
    /**
     * 判断文件是否已经存在
     * @param fileName     文件路径
     * @return
     */
    private boolean isFileExist(String fileName){
        File file = new File(fileName);
        return file.exists();
    }
 
    /**
     * 在指定路径下创建新文件
     * @param fileName      文件路径
     * @return
     * @throws IOException
     */
    private File createFile(String fileName) throws IOException{
        File file = new File(fileName);
        if(!file.createNewFile()){
            file.delete();
            file.createNewFile();
        }
        return file;
    }
     
    /**
     * 若文件已存在，判断文件大小是否正确
     * @param filePath         图片路径
     * @param size             文件大小
     * @return
     */
    public boolean isSizeSame(String filePath, long size){
        File file = new File(filePath);
        if(file.length() == size){
        	return true;
        }else{
        	return false;
        }
        
    }
   
}