package com.mytime.util;

import android.text.TextUtils;

import com.sun.mail.util.MailSSLSocketFactory;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * JavaMail发送邮件: java版本-与web无关
 * 前提是QQ邮箱里帐号设置要开启POP3/SMTP协议
 *
 *
 */

public class MailUtil {

    private Properties prop;
    private StringBuilder code;
    private final String from;
    private final String to;
    private final String STMPCode = "JCMPYSEPLLHAYLZH";

    //验证邮箱是否合法
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    public MailUtil(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public void setProperties(){
        this.prop = new Properties();
        // 开启debug调试，以便在控制台查看
        this.prop.setProperty("mail.debug", "true");
        // 设置邮件服务器主机名
        this.prop.setProperty("mail.host", "smtp.163.com");
        // 发送服务器需要身份验证
        this.prop.setProperty("mail.smtp.auth", "true");
        // 发送邮件协议名称
        this.prop.setProperty("mail.transport.protocol", "smtp");
    }


    // 开启SSL加密，否则会失败
    public void setSSLSocket() throws GeneralSecurityException {
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        this.prop.put("mail.smtp.ssl.enable", "true");
        this.prop.put("mail.smtp.ssl.socketFactory", sf);
    }

    public void sendMessage() throws Exception {
        // 创建session
        Session session = Session.getInstance(prop);
        // 通过session得到transport对象
        Transport ts = session.getTransport();
        // 连接邮件服务器：邮箱类型，帐号，POP3/SMTP协议授权码 163使用：smtp.163.com
        ts.connect("smtp.163.com", this.from,STMPCode );
        // 创建邮件
        Message message = createSimpleMail(session,this.from,this.to);
        // 发送邮件
        ts.sendMessage(message, message.getAllRecipients());
        ts.close();
    }

    public void init() throws GeneralSecurityException {
        setProperties();
        setSSLSocket();
        setCode();
    }

    /**
     * @Method: createSimpleMail
     * @Description: 创建一封只包含文本的邮件
     */
    public MimeMessage createSimpleMail(Session session, String from, String to) throws Exception {
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        message.setFrom(new InternetAddress(from));
        // 指明邮件的收件人，发件人和收件人如果是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        // 邮件的标题
        message.setSubject("账户注册");
        // 邮件的文本内容
        message.setContent("欢迎您注册【MyTime】,账号注册验证码为(一分钟有效):"+this.code+",请勿回复此邮箱", "text/html;charset=UTF-8");

        // 返回创建好的邮件对象
        return message;
    }

    public void setCode(){
        //  获取6为随机验证码
        String[] letters = new String[] {
//                   "q","w","e","r","t","y","u","i","o","p","a","s","d","f","g","h","j","k","l","z","x","c","v","b","n","m",
//                   "A","W","E","R","T","Y","U","I","O","P","A","S","D","F","G","H","J","K","L","Z","X","C","V","B","N","M",
                "0","1","2","3","4","5","6","7","8","9"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(letters[(int) Math.floor(Math.random() * letters.length)]);
        }
        this.code = stringBuilder;
    }


    public StringBuilder getCode() {
        return code;
    }
}