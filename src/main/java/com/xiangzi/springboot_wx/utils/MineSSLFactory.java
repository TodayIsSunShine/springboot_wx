package com.xiangzi.springboot_wx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * 创建自定义SSLFactory
 *
 * <p><h2>Descriptions</h2>
 * <h3>MiguActivity</h3>
 * <h3>Package</h3> com.aspirecn.activity.util
 * </p>
 * <p><h2>Change History</h2>
 * 2019/3/24 15:14 | Dfei | created
 * </p>
 *
 * @author Dfei
 * @version 1.0.0
 */
@SuppressWarnings("unused")
public class MineSSLFactory {

    //TODO 路径,密码写在配置文件里
    private static final Logger logger = LoggerFactory.getLogger(MineSSLFactory.class);
    //private static final String SERVICE_KEY_STORE_PATH = "E:\\migu_activity\\migu.keystore"; //服务器端授信证书位置
    private static final String SERVICE_KEY_STORE_PASSWD = "2f3jhtFW"; //服务器授信证书密码
    //private static final String CLIENT_KEY_STORE_PATH = "E:\\migu_activity\\migu.jks";
    private static final String CLIENT_KEY_STORE_PASSWD = "2f3jhtFW"; //客户端授信证书密码


    /**
     * 获取用户私钥
     *
     * @return response str
     * @throws MineSSLFactoryException error
     */
    public static SSLSocketFactory getSocketFactory() throws MineSSLFactoryException {

        try {

            // 加载服务端证书，由服务端密钥生成
            KeyStore servicekeyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            servicekeyStore.load(Constants.SERVICE_KEY_STORE_PATH.getInputStream(), SERVICE_KEY_STORE_PASSWD.toCharArray());
            // 实例化密钥库
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(servicekeyStore, SERVICE_KEY_STORE_PASSWD.toCharArray());

            //加载客户端证书
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(Constants.CLIENT_KEY_STORE_PATH.getInputStream(), CLIENT_KEY_STORE_PASSWD.toCharArray());

            //初始化信任库
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(clientKeyStore);

            // 实例化SSL上下文
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (KeyStoreException e) {
            logger.error("设置证书类型失败: -> " + e.getMessage(), e);
            throw new MineSSLFactoryException(e.getMessage(), e);
        } catch (IOException e) {
            logger.error("未获取到服务器端证书文件: -> " + e.getMessage(), e);
            throw new MineSSLFactoryException(e.getMessage(), e);
        } catch (CertificateException e) {
            logger.error("加载授信证书异常:->" + e.getMessage(), e);
            throw new MineSSLFactoryException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("加载授信证书异常:->" + e.getMessage(), e);
            throw new MineSSLFactoryException(e.getMessage(), e);
        } catch (UnrecoverableKeyException e) {
            logger.error("实例化秘钥库异常:->" + e.getMessage(), e);
            throw new MineSSLFactoryException(e.getMessage(), e);
        } catch (KeyManagementException e) {
            logger.error(e.getMessage(), e);
            throw new MineSSLFactoryException(e.getMessage(), e);
        }
    }

    public static void main(String... args) throws MineSSLFactoryException {

        System.out.println(MineSSLFactory.getSocketFactory());
    }

    public static class MineSSLFactoryException extends Throwable {
        public MineSSLFactoryException() {
        }

        public MineSSLFactoryException(String message) {
            super(message);
        }

        public MineSSLFactoryException(String message, Throwable th) {
            super(message, th);
        }

        public MineSSLFactoryException(Throwable th) {
        }
    }
}
