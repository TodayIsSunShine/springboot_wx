package com.xiangzi.springboot_wx.utils;

/**
 * OkHttp简单封装的工具类
 * <p><h2>Descriptions</h2>
 * <h3>MiguActivity</h3>
 * <h3>Package</h3> com.aspirecn.activity.util
 * </p>
 * <p><h2>Change History</h2>
 * 2019/3/24 17:24 | Dfei | created
 * </p>
 *
 * @author Dfei
 * @version 1.0.0
 */

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.ToString;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class OkHttpHelper {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpHelper.class);
    private final static String GET = "GET";
    private final static String POST = "POST";
    private final static String UTF8 = "UTF-8";
    private final static String GBK = "GBK";
    private final static String DEFAULT_CHARSET = UTF8;
    private final static String DEFAULT_METHOD = GET;
    //TODO 使用OKHTTP的media
    private final static String DEFAULT_MEDIA_TYPE = org.springframework.http.MediaType.APPLICATION_JSON.toString();
    private final static boolean DEFAULT_LOG = false;

    private static final byte[] LOCKER = new byte[0];
    private static OkHttpHelper mInstance;
    private static OkHttpHelper mInstanceSSL;
    private OkHttpClient.Builder clientBuilder;

    private OkHttpHelper() {
        clientBuilder = new OkHttpClient.Builder();
        clientBuilder.readTimeout(20, TimeUnit.SECONDS);//读取超时
        clientBuilder.connectTimeout(6, TimeUnit.SECONDS);//连接超时
        clientBuilder.writeTimeout(60, TimeUnit.SECONDS);//写入超时
        clientBuilder.connectionPool((new ConnectionPool(30,
                7, TimeUnit.MINUTES)));
        //支持HTTPS请求，跳过证书验证
        clientBuilder.sslSocketFactory(createSSLSocketFactory());
        clientBuilder.hostnameVerifier((hostname, session) -> true);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(logging);
    }

    private OkHttpHelper(OkHttpClient.Builder clientBuilder) {

        this.clientBuilder = clientBuilder;
        clientBuilder.readTimeout(20, TimeUnit.SECONDS);//读取超时
        clientBuilder.connectTimeout(6, TimeUnit.SECONDS);//连接超时
        clientBuilder.writeTimeout(60, TimeUnit.SECONDS);//写入超时
        clientBuilder.connectionPool((new ConnectionPool(30,
                7, TimeUnit.MINUTES)));
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(logging);
    }


    /**
     * 单例模式获取OkHttpHelper
     *
     * @return OkHttpHelper
     */
    static OkHttpHelper getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                mInstance = new OkHttpHelper();
            }
        }
        return mInstance;
    }

    public static OkHttpHelper getSSLInstance() throws MineSSLFactory.MineSSLFactoryException {


        SSLSocketFactory factory = MineSSLFactory.getSocketFactory();
        if (mInstanceSSL == null) {
            synchronized (LOCKER) {


                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .allEnabledTlsVersions()
                        .allEnabledCipherSuites()
                        .build();
                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

                clientBuilder.sslSocketFactory(factory);
                clientBuilder.hostnameVerifier((hostname, session) -> true);
                mInstanceSSL = new OkHttpHelper(clientBuilder);
            }
        }

        return mInstanceSSL;
    }

    public static void main(String[] args) throws IOException, NotSupportedException {
        Map<String, String> map = new HashMap<>();
        map.put("k", "v");
        OkHttpHelper helper = OkHttpHelper.getInstance();
        helper.execute(OkHttp.builder().url("http://httpbin.org/get?k1=v1").method(GET)
                .headerMap(map).queryMap(map).requestLog(true).responseLog(true).build());


        helper.execute(OkHttp.builder().url("http://httpbin.org/post?k1=v1").method(POST)
                .data("{\"id\":\"1\"}").mediaType(org.springframework.http.MediaType.APPLICATION_JSON.toString())
                .headerMap(map).queryMap(map).requestLog(true).responseLog(true).build());
    }

    /**
     * get请求
     *
     * @param url 请求的url
     * @return response str
     * @throws IOException           IO
     * @throws NotSupportedException 不支持方法[支持get,post]
     */
    public String get(String url) throws IOException, NotSupportedException {
        return execute(OkHttp.builder().url(url).build());
    }

    /**
     * 带查询参数的GET查询
     *
     * @param url      url
     * @param queryMap url 查询参数
     * @return response str
     * @throws IOException           IO
     * @throws NotSupportedException 不支持方法[支持get,post]
     */
    public String get(String url, Map<String, String> queryMap)
            throws IOException, NotSupportedException {
        return execute(OkHttp.builder().url(url).queryMap(queryMap).build());
    }

    /**
     * application/json post请求
     *
     * @param url url
     * @param obj 请求内容
     * @return response str
     * @throws IOException           IO
     * @throws NotSupportedException 不支持方法[支持get,post]
     */
    public String postJson(String url, Object obj)
            throws IOException, NotSupportedException {
        return execute(OkHttp.builder().url(url).method(POST).data(JSON.toJSONString(obj))
                .mediaType(org.springframework.http.MediaType.APPLICATION_JSON.toString()).build());
    }

    /**
     * @param url     url
     * @param formMap b表单数据
     * @return response str
     * @throws IOException           IO
     * @throws NotSupportedException 不支持方法[支持get,post]
     */
    public String postForm(String url, Map<String, String> formMap)
            throws IOException, NotSupportedException {
        String data = "";
        if (formMap != null && formMap.size() > 0) {
            data = formMap.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(),
                    entry.getValue())).collect(Collectors.joining("&"));
        }

        return execute(OkHttp.builder().url(url).method(POST).data(data)
                .mediaType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED.toString()).build());
    }

    /**
     * @param url       url
     * @param head      请求头
     * @param data      请求体
     * @param mediaType content-type类型
     * @return response str
     * @throws IOException           IO
     * @throws NotSupportedException 不支持方法[支持get,post]
     */
    public String post(String url, Map<String, String> head, String data, String mediaType)
            throws IOException, NotSupportedException {
        return execute(OkHttp.builder().headerMap(head).url(url).method(POST).data(data)
                .mediaType(mediaType).build());
    }

    /**
     * 通用执行方法
     */
    private String execute(OkHttp okHttp) throws IOException, NotSupportedException {
        if (StringUtils.isEmpty(okHttp.requestCharset)) {
            okHttp.requestCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isEmpty(okHttp.method)) {
            okHttp.method = DEFAULT_METHOD;
        }
        if (StringUtils.isEmpty(okHttp.mediaType)) {
            okHttp.mediaType = DEFAULT_MEDIA_TYPE;
        }
        if (okHttp.requestLog) {//记录请求日志
            if (logger.isInfoEnabled()) logger.info(okHttp.toString());
        }

        String url = okHttp.url;

        Request.Builder builder = new Request.Builder();

        if (okHttp.queryMap != null && okHttp.queryMap.size() > 0) {
            String queryParams = okHttp.queryMap.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
            url = String.format("%s%s%s", url, url.contains("?") ? "&" : "?", queryParams);
        }

        builder.url(url);

        if (okHttp.headerMap != null && okHttp.headerMap.size() > 0) {
            okHttp.headerMap.forEach(builder::addHeader);
        }

        String method = okHttp.method.toUpperCase();
        String mediaType = String.format("%s;charset=%s", okHttp.mediaType, okHttp.requestCharset);

        if (GET.equals(method)) {
            builder.get();
        } else if (POST.equals(method)) {

            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), okHttp.data);
            builder.method(method, requestBody);
        } else {
            throw new NotSupportedException(String.format("http method:%s not support!", method));
        }
        OkHttpClient client = clientBuilder.build();

        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {

            String resultStr = "";
            if (response.body() != null)
                resultStr = response.body().string();

            if (StringUtils.isEmpty(resultStr)) resultStr = "";

            return resultStr;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return reponse 密钥
     */
    public SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ssfFactory;
    }

    @Builder
    @ToString(exclude = {"requestCharset", "requestLog", "responseLog"})
    private static class OkHttp {

        private String url;
        private String method = DEFAULT_METHOD;
        private String data;
        private String mediaType = DEFAULT_MEDIA_TYPE;
        private Map<String, String> queryMap;
        private Map<String, String> headerMap;
        private String requestCharset = DEFAULT_CHARSET;
        private boolean requestLog = DEFAULT_LOG;
        private boolean responseLog = DEFAULT_LOG;
    }

    /**
     * 用于信任所有证书
     */
    class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    class NotSupportedException extends Throwable {
        public NotSupportedException(String format) {
        }
    }
}