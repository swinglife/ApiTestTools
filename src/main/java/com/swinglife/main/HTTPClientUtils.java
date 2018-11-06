package com.swinglife.main;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.List;

/****
 * HTTP 客户端工具类
 *
 * @author Swinglife
 */
public class HTTPClientUtils {


    private static final String APPKEY = "RC-App-Key";
    private static final String NONCE = "RC-Nonce";
    private static final String TIMESTAMP = "RC-Timestamp";
    private static final String SIGNATURE = "RC-Signature";

    //上次调用URL
    private static String oldUrlName = "";
    //请求次数
    private static int requestCount = 0;

//    /**
//     * 上传文件到nimg
//     *
//     * @param file
//     *            上传的文件
//     * @return 响应结果
//     */
//    public String httpClientUploadFile(MultipartFile file) {
//        final String remote_url = "http://39.108.210.116:9097/01/upload";
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        String result = "";
//        try {
//            String fileName = file.getOriginalFilename();
//            HttpPost httpPost = new HttpPost(remote_url);
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.addBinaryBody("userfile", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
//            HttpEntity entity = builder.build();
//            httpPost.setEntity(entity);
//            HttpResponse response = httpClient.execute(httpPost);
//            HttpEntity responseEntity = response.getEntity();
//            if (responseEntity != null) {
//                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }




    // 设置body体
    public static void setBodyParameter(StringBuilder sb, HttpURLConnection conn)
            throws IOException {
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(sb.toString());
        out.flush();
        out.close();
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }



    /****
     * POST提交表单
     *
     * @param url
     * @param formparams
     */
    public String httpPostByParams(String url, List<NameValuePair> formparams,String unicode) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        System.out.println("httpPost:"+url);
        // 创建参数队列
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            //uefEntity = new UrlEncodedFormEntity(formparams, "GB2312");
            httppost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=gb2312");
            httppost.setEntity(uefEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                    if(unicode==null||unicode.length()<=0){
                        return EntityUtils.toString(entity);
                    }else {
                        return EntityUtils.toString(entity,unicode);
                    }
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void requestCount(String url){
        if(url.equals(oldUrlName)){
            requestCount++;
        }else{
            requestCount = 1;
        }
        oldUrlName = url;
        if(requestCount>=2){
        }
    }

    /****
     * GET 访问请求
     *
     * @param url
     * @return
     */
    public String httpGet(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        long startTime=System.currentTimeMillis();   //获取开始时间
        requestCount(url);
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                    String result = EntityUtils.toString(entity);
                    long endTime=System.currentTimeMillis(); //获取结束时间
                    return result;
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /****
     * 执行post
     *
     * @param url
     * @param entity
     * @return
     */
    public String excPost(String url, StringEntity entity) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity httpEntity = response.getEntity();
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                    return EntityUtils.toString(httpEntity);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /****
     * POST提交JSON对象
     *
     * @param url
     * @param data
     * @return
     */
    public String httpPost(String url, JSONObject data) {
        StringEntity entity = new StringEntity(data.toString(), "utf-8");// 解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        return excPost(url, entity);
    }

    /****
     * POST提交表单
     *
     * @param url
     * @param formparams
     */
    public String httpPost(String url, List<NameValuePair> formparams) {
        long startTime=System.currentTimeMillis();   //获取开始时间
        requestCount(url);
        // 创建参数队列
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            String result = this.excPost(url,uefEntity);
            long endTime=System.currentTimeMillis(); //获取结束时间
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static void main(String args[]){
        HTTPClientUtils httpClientUtils = new HTTPClientUtils();
//        List list = new ArrayList();
//        list.add(new BasicNameValuePair("account","test"));
//        list.add(new BasicNameValuePair("password","123456"));
//       String result =  httpClientUtils.httpPost("http://120.55.37.207:8080/system/tologin",list);
//        System.out.println(result);
        httpClientUtils.httpGet("http://120.55.37.207:8080/system/tologin");
    }

//
//
//    public static void main(String[] args) throws IOException {
//        HttpClient client = new HttpClient();
//        HttpMethod method = new GetMethod("http://www.baidu.com");
//        int statusCode = 0;
//        try {
//            statusCode = client.executeMethod(method);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            System.out.printf("访问失败");
//        }
//
//        System.out.println("网页访问返回代码:"+statusCode);
//        //判断是否返回200（200代表成功）
//        if(statusCode!= HttpStatus.SC_OK){
//            System.out.printf("网址失败");
//        }else {
//            System.out.printf("网址正确");
//        }
//    }

}
