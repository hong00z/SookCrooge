package com.example.sookcrooge

import android.util.Log
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(receiverEmail:String, verificationCode: String) {
    private val receiverEmail:String
    private val verificationCode: String
    init{
        this.receiverEmail = receiverEmail
        this.verificationCode = verificationCode
    }

    fun sendMail()
    {
        try {
            val user="rabbit4935@gmail.com"
            val password="ohsr wodm xcbl wnah"
            val props = Properties()
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.port"] = "587"
            props["mail.smtp.ssl.trust"] = "*";

            val session = Session.getInstance(props, object: javax.mail.Authenticator() {
                    override  fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(user, password);
                    }
                })

            val message = MimeMessage(session)
            message.setFrom(InternetAddress(user))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail))
            message.subject = "숙크루지 인증 메일입니다."
            message.setText("비밀번호 변경을 위한 인증 번호는 "+ verificationCode+"입니다.\n")

            Transport.send(message)
        }
        catch (e: Exception)
        {
            Log.d("error", e.toString())
        }
    }
}