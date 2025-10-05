package com.sf.zhimengjing.common.util;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;

/**
 * 邮件发送工具类
 *
 * @author zhimengjing
 */
@Component
@Slf4j
public class EmailApi {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from; // 发件人

    /**
     * 发送纯文本的邮件
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @return 是否成功
     */
    @SneakyThrows(Exception.class)
    public boolean sendGeneralEmail(String subject, String content, String... to) {
        try {
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            // 设置收件人
            message.setTo(to);
            // 设置邮件主题
            message.setSubject(subject);
            // 设置邮件内容
            message.setText(content);

            // 发送邮件
            mailSender.send(message);
            log.info("发送文本邮件成功，收件人：{}", String.join(",", to));
            return true;
        } catch (Exception e) {
            log.error("发送文本邮件失败，收件人：{}，错误信息：{}", String.join(",", to), e.getMessage());
            return false;
        }
    }

    /**
     * 发送html格式的邮件
     * @param subject 主题
     * @param content HTML内容
     * @param to 收件人
     * @return 是否成功
     */
    @SneakyThrows(Exception.class)
    public boolean sendHtmlEmail(String subject, String content, String... to) {
        try {
            // 创建邮件消息
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            // 设置收件人
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content, true);

            // 发送邮件
            mailSender.send(mimeMessage);
            log.info("发送HTML邮件成功，收件人：{}", String.join(",", to));
            return true;
        } catch (Exception e) {
            log.error("发送HTML邮件失败，收件人：{}，错误信息：{}", String.join(",", to), e.getMessage());
            return false;
        }
    }


    /**
     * 异步发送html格式的邮件
     * @param subject 主题
     * @param content HTML内容
     * @param to 收件人
     */
    @Async
    public void sendHtmlEmailAsync(String subject, String content, String... to) {
        try {
            // 创建邮件消息
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            // 设置收件人
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content, true);

            // 发送邮件
            mailSender.send(mimeMessage);
            log.info("异步发送HTML邮件成功，收件人：{}", String.join(",", to));
        } catch (Exception e) {
            log.error("异步发送HTML邮件失败，收件人：{}，错误信息：{}", String.join(",", to), e.getMessage());
        }
    }
    /**
     * 发送带附件的邮件
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @param filePaths 附件路径
     * @return 是否成功
     */
    @SneakyThrows(Exception.class)
    public boolean sendAttachmentsEmail(String subject, String content, String[] to, String[] filePaths) {
        try {
            // 创建邮件消息
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            // 设置收件人
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content, true);

            // 添加附件
            if (filePaths != null && filePaths.length > 0) {
                for (String filePath : filePaths) {
                    FileSystemResource file = new FileSystemResource(new File(filePath));
                    helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                }
            }
            // 发送邮件
            mailSender.send(mimeMessage);
            log.info("发送附件邮件成功，收件人：{}", String.join(",", to));
            return true;
        } catch (Exception e) {
            log.error("发送附件邮件失败，收件人：{}，错误信息：{}", String.join(",", to), e.getMessage());
            return false;
        }
    }

    /**
     * 发送带静态资源的邮件
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @param rscPath 静态资源路径
     * @param rscId 静态资源id
     * @return 是否成功
     */
    @SneakyThrows(Exception.class)
    public boolean sendInlineResourceEmail(String subject, String content, String to, String rscPath, String rscId) {
        try {
            // 创建邮件消息
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            // 设置发件人
            helper.setFrom(from);
            // 设置收件人
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);

            // html内容图片
            String contentHtml = "<html><body>这是邮件的内容，包含一个图片：<img src='cid:" + rscId + "'>" + content + "</body></html>";

            helper.setText(contentHtml, true);
            // 指定静态资源地址
            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);

            mailSender.send(mimeMessage);
            log.info("发送静态资源邮件成功，收件人：{}", to);
            return true;
        } catch (Exception e) {
            log.error("发送静态资源邮件失败，收件人：{}，错误信息：{}", to, e.getMessage());
            return false;
        }
    }
}