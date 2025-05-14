package com.example.ecotrack;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private String email;      // Sender's Yahoo email
    private String password;   // Application-specific password for the Yahoo account
    private String recipient;  // Recipient's email address
    private String subject;    // Email subject
    private String message;    // Email content

    // Constructor to initialize necessary parameters
    public JavaMailAPI(Context context,
                       String email,
                       String password,
                       String recipient,
                       String subject,
                       String message) {
        this.context = context;
        this.email = email;
        this.password = password;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            // 1. Configure mail server properties for Yahoo SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.mail.yahoo.com"); // Yahoo SMTP server
            props.put("mail.smtp.port", "587");                // Port for TLS
            props.put("mail.smtp.auth", "true");               // Enable authentication
            props.put("mail.smtp.starttls.enable", "true");    // Enable TLS

            // 2. Create a mail session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

            // 3. Construct the email message
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(email));        // Sender's email
            mimeMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));              // Recipient's email
            mimeMessage.setSubject(subject);                        // Email subject
            mimeMessage.setText(message);                           // Email content (plain text)

            // 4. Send the email
            Transport.send(mimeMessage);

            return true; // Email sent successfully
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Failed to send email
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        // Notify the user about the email status
        if (success) {
            Toast.makeText(context, "Email sent successfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to send email.", Toast.LENGTH_LONG).show();
        }
    }
}
