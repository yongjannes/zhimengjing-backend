package com.sf.zhimengjing.common.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.sf.zhimengjing.common.config.properties.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Title: OssClientConfig
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.config
 * @description: OSS 客户端配置类，根据配置文件自动选择阿里云或腾讯云客户端，支持条件装配。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OssClientConfig {

    private final OssProperties ossProperties;

    /**
     * 配置阿里云OSS客户端
     * 当 oss.provider=aliyun 时生效，创建阿里云OSS客户端 Bean。
     *
     * @return OSS 客户端实例
     */
    @Bean
    @ConditionalOnProperty(name = "oss.provider", havingValue = "aliyun")
    public OSS aliyunOssClient() {
        OssProperties.AliyunConfig aliyun = ossProperties.getAliyun();

        log.info("初始化阿里云OSS客户端 - Endpoint: {}, Bucket: {}",
                aliyun.getEndpoint(), aliyun.getBucketName());

        com.aliyun.oss.ClientBuilderConfiguration clientConfig = new com.aliyun.oss.ClientBuilderConfiguration();

        // 设置连接超时时间
        if (aliyun.getConnectTimeout() != null) {
            clientConfig.setConnectionTimeout(aliyun.getConnectTimeout());
        }

        // 设置 Socket 超时时间
        if (aliyun.getSocketTimeout() != null) {
            clientConfig.setSocketTimeout(aliyun.getSocketTimeout());
        }

        // 设置最大连接数
        if (aliyun.getMaxConnections() != null) {
            clientConfig.setMaxConnections(aliyun.getMaxConnections());
        }

        // 设置失败请求重试次数
        clientConfig.setMaxErrorRetry(3);

        log.info("阿里云OSS客户端配置 - ConnectTimeout: {}ms, SocketTimeout: {}ms, MaxConnections: {}",
                aliyun.getConnectTimeout(), aliyun.getSocketTimeout(), aliyun.getMaxConnections());

        return new OSSClientBuilder().build(
                aliyun.getEndpoint(),
                aliyun.getAccessKeyId(),
                aliyun.getAccessKeySecret(),
                clientConfig
        );
    }

    /**
     * 配置腾讯云COS客户端
     * 当 oss.provider=tencent 时生效，创建腾讯云COS客户端 Bean。
     *
     * @return COS 客户端实例
     */
    @Bean
    @ConditionalOnProperty(name = "oss.provider", havingValue = "tencent")
    public COSClient tencentCosClient() {
        OssProperties.TencentConfig tencent = ossProperties.getTencent();

        log.info("初始化腾讯云COS客户端 - Region: {}, Bucket: {}",
                tencent.getRegion(), tencent.getBucketName());

        // 初始化身份信息
        COSCredentials cred = new BasicCOSCredentials(tencent.getSecretId(), tencent.getSecretKey());

        // 设置地域
        Region region = new Region(tencent.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);

        // 配置连接参数
        if (tencent.getConnectionTimeout() != null) {
            clientConfig.setConnectionTimeout(tencent.getConnectionTimeout());
        }

        if (tencent.getSocketTimeout() != null) {
            clientConfig.setSocketTimeout(tencent.getSocketTimeout());
        }

        if (tencent.getMaxConnections() != null) {
            clientConfig.setMaxConnectionsCount(tencent.getMaxConnections());
        }

        // 设置请求失败重试次数
        clientConfig.setMaxErrorRetry(3);

        // 使用 HTTPS 协议
        clientConfig.setHttpProtocol(com.qcloud.cos.http.HttpProtocol.https);

        log.info("腾讯云COS客户端配置 - ConnectionTimeout: {}ms, SocketTimeout: {}ms, MaxConnections: {}",
                tencent.getConnectionTimeout(), tencent.getSocketTimeout(), tencent.getMaxConnections());

        return new COSClient(cred, clientConfig);
    }
}
