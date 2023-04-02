
import java.util.Properties;
import javax.mail.Flags;  

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CheckingMails {

   public static void check(String host, String storeType, String user,
      String password) 
   {
      Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        //props.put("mail.smtp.ssl.trust", "*");
        props.setProperty("mail.smtp.starttls.required", "true");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        try {
                Session session = Session.getDefaultInstance(props, null);
                Store store = session.getStore("imaps");


                store.connect("outlook.office365.com", "vivademojssk@outlook.com", "Kav$jack100");

                System.out.println(store);

      //create the folder object and open it
      Folder emailFolder = store.getFolder("innybox");
      emailFolder.open(Folder.READ_WRITE);

      // retrieve the messages from the folder in an array and print it
      Message[] messages = emailFolder.getMessages();
      System.out.println("messages.length---" + messages.length);



     Folder inbox = store.getFolder("outybox");
      inbox.open(Folder.READ_WRITE);

      for (int i = 0, n = messages.length; i < n; i++) {
         Message message = messages[i];
         System.out.println("---------------------------------");
         System.out.println("Email Number " + (i + 1));
         System.out.println("Subject: " + message.getSubject());
         System.out.println("From: " + message.getFrom()[0]);
         System.out.println("Text: " + message.getContent().toString());
 

 // Define the endpoint URL and parameters
        String endpointUrl = "http://localhost:5000/predict";
     

        // Create the URL object with the endpoint URL and parameters
        URL url = new URL(endpointUrl);

        // Create the HttpURLConnection object and set the request method to POST
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        // Set the request headers if necessary
        conn.setRequestProperty("Content-type", "application/json");

        // Set the request body if necessary
        String requestBody = message.getSubject() +" "+ message.getContent().toString();
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
        os.write(requestBodyBytes);
        os.flush();
        os.close();

        // Check the response code and retrieve the response body
        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder responseBody = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            responseBody.append(inputLine);
        }
        in.close();

        // Print the response body
        System.out.println(responseBody.toString());


        if(responseBody.toString().equals("1")){

          System.out.print(" ####################### ");
          System.out.print(" PREDICTED NON SPAM --- MOVING TO OUT BOX");
          System.out.print(" ####################### ");


         Message[] ma = {message};
        emailFolder.copyMessages(ma,inbox);
        emailFolder.setFlags(ma, new Flags(Flags.Flag.DELETED), true);
 

      } else{
         System.out.print(" ####################### ");
          System.out.print(" PREDICTED  SPAM --- DOING NOTHING");
          System.out.print(" ####################### ");

      }
}
 

      //close the store and folder objects
      inbox.close(false);
      emailFolder.close(false);
      store.close();

      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (Exception e) {   
      e.printStackTrace();
      }
   }

   public static void main(String[] args) {

      String host = "imap.gmail.com";// change accordingly
      String mailStoreType = "imap";
      String username = "iicdepotmanager@gmail.com";// change accordingly
      String password = "$depo100";// change accordingly

      check(host, mailStoreType, username, password);

   }

}
