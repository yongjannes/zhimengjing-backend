package com.sf.zhimengjing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Title: zhimengjingApplication
 * @Author 殇枫
 * @Package com.sf.zhimengjing
 * @description: Spring Template 应用主启动类，负责启动 Spring Boot 应用并显示启动信息
 */
@SpringBootApplication
@MapperScan("com.sf.zhimengjing.mapper")
@EnableScheduling
@EnableAsync
public class zhimengjingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(zhimengjingApplication.class);
        app.addListeners(new ArtisticStartupInfoLogger(Instant.now()));
        app.run(args);
    }

    /**
     * @Title: ArtisticStartupInfoLogger
     * @Author 殇枫
     * @Package com.sf.zhimengjing
     * @description: 启动信息打印监听器（居中显示），显示本地访问、外部访问、环境信息和 Knife4j 文档地址
     */
    @Slf4j
    public static class ArtisticStartupInfoLogger implements ApplicationListener<ApplicationReadyEvent> {

        private final Instant startTime;

        public ArtisticStartupInfoLogger(Instant startTime) {
            this.startTime = startTime;
        }

        private static class Ansi {
            static final String RESET = "\u001B[0m";
            static final String BOLD = "\u001B[1m";
            static final String GREEN = "\u001B[32m";
            static final String CYAN = "\u001B[36m";
            static final String YELLOW = "\u001B[33m";
            static final String BLUE = "\u001B[34m";
        }

        @Override
        public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
            Environment env = event.getApplicationContext().getEnvironment();
            logCenteredInfo(env);
        }

        private void logCenteredInfo(Environment env) {
            final String appName = env.getProperty("spring.application.name", "application");
            final String port = env.getProperty("server.port", "8080");
            final String contextPath = env.getProperty("server.servlet.context-path", "");
            final String docPath = "/doc.html";
            final String[] activeProfiles = env.getActiveProfiles();
            final String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");

            String profilesString;
            boolean isProduction = java.util.Arrays.asList(activeProfiles).contains("prod");
            if (isProduction) {
                profilesString = String.format("%s%s[生产环境]%s", Ansi.BOLD, Ansi.GREEN, Ansi.RESET);
            } else if (activeProfiles.length == 0) {
                profilesString = "default";
            } else {
                profilesString = StringUtils.join(activeProfiles, ", ");
            }

            String hostAddress;
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                hostAddress = "localhost";
                log.warn("⚠️ 无法确定主机名，外部地址将使用 `localhost` 作为备用。");
            }

            final String localUrl = String.format("%s://localhost:%s%s", protocol, port, contextPath);
            final String externalUrl = String.format("%s://%s:%s%s", protocol, hostAddress, port, contextPath);
            final String knife4jUrl = String.format("%s://localhost:%s%s%s", protocol, port, contextPath, docPath);

            long durationMs = Duration.between(startTime, Instant.now()).toMillis();

            final String title = String.format("🚀 %s%s%s 已启动成功!%s", Ansi.BOLD, Ansi.GREEN, appName, Ansi.RESET);
            final String timeLine = String.format("⏱️ %s启动耗时:%s %d 毫秒", Ansi.YELLOW, Ansi.CYAN, durationMs);
            final String localLine = String.format("🏠 %s本地访问:%s %s%s", Ansi.YELLOW, Ansi.BLUE, localUrl, Ansi.RESET);
            final String externalLine = String.format("🌐 %s外部访问:%s %s%s", Ansi.YELLOW, Ansi.BLUE, externalUrl, Ansi.RESET);
            final String profileLine = String.format("⚙️ %s环境:%s %s", Ansi.YELLOW, Ansi.CYAN, profilesString);
            final String knife4jLine = String.format("📚 %sKnife4j文档:%s %s%s", Ansi.YELLOW, Ansi.BLUE, knife4jUrl, Ansi.RESET);

            final int longestLine = Stream.of(title, timeLine, localLine, externalLine, profileLine, knife4jLine)
                    .mapToInt(s -> s.replaceAll("\u001B\\[[;\\d]*m", "").length())
                    .max().orElse(80);
            final int bannerWidth = longestLine + 10;

            StringBuilder banner = new StringBuilder("\n\n");
            banner.append(centerText(title, bannerWidth)).append("\n\n");
            banner.append(centerText(timeLine, bannerWidth)).append("\n");
            banner.append(centerText(localLine, bannerWidth)).append("\n");
            banner.append(centerText(externalLine, bannerWidth)).append("\n");
            banner.append(centerText(knife4jLine, bannerWidth)).append("\n");
            banner.append(centerText(profileLine, bannerWidth)).append("\n");

            log.info(banner.toString());
        }

        private String centerText(String text, int width) {
            int textLength = text.replaceAll("\u001B\\[[;\\d]*m", "").length();
            if (textLength >= width) { return text; }
            int padding = (width - textLength) / 2;
            return " ".repeat(padding) + text;
        }
    }
}
