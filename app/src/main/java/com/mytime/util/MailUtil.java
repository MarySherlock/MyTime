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



public class MailUtil {

    private Properties prop;
    private StringBuilder code;
    private final String from;
    private final String to;
    private final String STMPCode = "JCMPYSEPLLHAYLZH";

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
        this.prop.setProperty("mail.debug", "true");
        this.prop.setProperty("mail.host", "smtp.163.com");
        this.prop.setProperty("mail.smtp.auth", "true");
        this.prop.setProperty("mail.transport.protocol", "smtp");
    }


    public void setSSLSocket() throws GeneralSecurityException {
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        this.prop.put("mail.smtp.ssl.enable", "true");
        this.prop.put("mail.smtp.ssl.socketFactory", sf);
    }

    public void sendMessage() throws Exception {
        Session session = Session.getInstance(prop);
        Transport ts = session.getTransport();
        ts.connect("smtp.163.com", this.from,STMPCode );
        Message message = createSimpleMail(session,this.from,this.to);
        ts.sendMessage(message, message.getAllRecipients());
        ts.close();
    }

    public void init() throws GeneralSecurityException {
        setProperties();
        setSSLSocket();
        setCode();
    }


    public MimeMessage createSimpleMail(Session session, String from, String to) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("账户注册");
        message.setContent("欢迎您注册【MyTime】,账号注册验证码为(一分钟有效):"+this.code+",请勿回复此邮箱", "text/html;charset=UTF-8");
        return message;
    }

    public void setCode(){
        String[] letters = new String[] {
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