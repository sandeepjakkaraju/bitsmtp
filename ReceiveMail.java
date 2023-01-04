import java.io.IOException;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import com.sun.mail.pop3.POP3Store;
import javax.mail.*;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
import javax.mail.internet.MimeBodyPart;
import java.util.*;
import java.io.*;
import java.util.stream.*;

public class ReceiveMail{

 public static void receiveEmail(String pop3Host, String storeType, String user, String password) throws Exception {
  try {
   Properties properties = new Properties();
   properties.put("mail.pop3.host", pop3Host);
   Session emailSession = Session.getDefaultInstance(properties);

   POP3Store emailStore = (POP3Store) emailSession.getStore(storeType);
   emailStore.connect(user, password);

   Folder emailFolder = emailStore.getFolder("INBOX");
   emailFolder.open(Folder.READ_ONLY);

	List<String[]> dataLines = new ArrayList<>();
   Message[] messages = emailFolder.getMessages();
   for (int i = 0; i < messages.length; i++) {
	Message message = messages[i];
	System.out.println("---------------------------------");
	System.out.println("Email Number " + (i + 1));
	System.out.println("Subject: " + message.getSubject());
	System.out.println("From: " + message.getFrom()[0]);
	System.out.println("Text: " + message.getContent().toString());
	System.out.println("Body: " + getTextFromMessage(message));
	
	Multipart multiPart = null;
	String allAttFile = "";
	String totAttCount ="";
try{
	multiPart = (Multipart) message.getContent();
	System.out.println("Attachement Count: " + multiPart.getCount());
	totAttCount ="\""+multiPart.getCount()+"\"";

	for (int ii = 0; ii < multiPart.getCount(); ii++) {
	    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(ii);
		System.out.println("AttachementFilename-: "+i+part.getFileName());
		allAttFile = part.getFileName()+";";
	   }

}catch(Exception e){
}
	String id ="\""+(i+1)+"\"";
	String fromEmail = "\""+message.getFrom()[0]+"\"";
	String subject = "\""+message.getSubject()+"\"";
	String body =  "\""+getTextFromMessage(message)+"\"";
	allAttFile = "\""+allAttFile+"\"";


	dataLines.add(new String[] 
  { id,fromEmail,subject,body,totAttCount,allAttFile });

  }


File csvOutputFile = new File("AllMailsOut.csv");
    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
        dataLines.stream()
          .map(it->Stream.of(it).map(t->t).collect(Collectors.joining(",")))
          .forEach(pw::println);
    }
//    assertTrue(csvOutputFile.exists());


   emailFolder.close(false);
   emailStore.close();

  } catch (NoSuchProviderException e) {e.printStackTrace();} 
  catch (MessagingException e) {e.printStackTrace();}
  catch (IOException e) {e.printStackTrace();}
 }



 public static void main(String[] args) throws Exception {

  String host = "webmail.iinterchange.in";//change accordingly
  String mailStoreType = "pop3";
  String username= "depot@iinterchange.in";
  String password= "3R1$toped";//change accordingly

  receiveEmail(host, mailStoreType, username, password);

 }

private static String getTextFromMessage(Message message) throws Exception {
    if (message.isMimeType("text/plain")){
        return message.getContent().toString();
    }else if (message.isMimeType("multipart/*")) {
        String result = "";
        MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i ++){
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")){
                result = result + "\n" + bodyPart.getContent();
                break;  //without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")){
                String html = (String) bodyPart.getContent();
                result = result + "\n" + Jsoup.parse(html).text();

            }
        }
        return result;
    }
    return "";
}

}
