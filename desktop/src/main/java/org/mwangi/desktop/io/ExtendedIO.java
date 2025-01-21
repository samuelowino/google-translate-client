package org.mwangi.desktop.io;

import com.google.gson.Gson;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import okhttp3.Response;
import org.mwangi.desktop.payload.IOSMessage;
import org.mwangi.desktop.payload.Resources;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExtendedIO {
    private static final Logger log=Logger.getLogger(ExtendedIO.class.getSimpleName());
    private static final String serverIp =getProperties().getProperty("server.ip");
    private static final int serverPort = Integer.parseInt(getProperties().getProperty("server.port"));
    public final  static String LOGIN_URL= "http://%s:%d/login".formatted(serverIp, serverPort);
    public final  static  String REGISTER_URL= "http://%s:%d/register".formatted(serverIp, serverPort);
    public  final  static  String  TRANSLATE_URL= "http://%s:%d/translate".formatted(serverIp, serverPort);
    public final static String JOB_URL= "http://%s:%d/translate/".formatted(serverIp, serverPort);
    public  final  static Gson gson=new Gson();
    public  final static String IOS_EXAMPLE= """
            Example ios format:
            "welcome_message" = "Welcome to our app";
            "login_button" = "Login";
            "signup_prompt" = "Don't have an account?";""";
    public final static  String ANDROID_EXAMPLE= """
            Example Android format:
            <resources>
              <string name="welcome_message">Welcome to our app</string>
              <string name="login_button">Login</string>
              <string name="signup_prompt">Don not have an account?</string>
            </resources>""";
    public final static Predicate<String> REQUIRED_IOS_VALIDATION_RULE = input -> !input.isEmpty() &&
            input.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .allMatch(line ->
                            line.matches("^\\s*\"[a-zA-Z_]+\"\\s*=\\s*\"[^\"]+\";\\s*"));
    public final static Predicate<String>  REQUIRED_ANDROID_VALIDATION_RULE = input -> {
        if(input.isEmpty()) return false;
        String outerPattern = "^<resources>\\s*(.+?)\\s*</resources>$";
        String stringPattern = "\\s*<string\\s+name=\"([a-zA-Z_]+)\">([^<]+)</string>\\s*";
        if (!input.replaceAll("\\s+", " ").trim().matches(outerPattern)) {
            return false;
        }
        return input.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .filter(line -> !line.equals("<resources>") && !line.equals("</resources>"))
                .allMatch(line -> line.matches(stringPattern));
    };

    public static Response handleRequest(RequestExecuter executer){
        try{
            return executer.execute();
        } catch (IOException e) {
            log.warning(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public static List<IOSMessage> fromIos(String s){
        return   s.lines()
                .filter(line -> !line.trim().isEmpty())
                .map(x ->{
                    String[] pair=x.split("=");
                    return new IOSMessage(pair[0], pair[1].stripTrailing().replaceAll(";$",""));
                })
                .collect(Collectors.toList());
    }
    public static Resources fromXml(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Resources.class);
            Unmarshaller jaxbContextUnmarshaller = jaxbContext.createUnmarshaller();
            return (Resources) jaxbContextUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            return null;
        }
    }
    public static String getSystemPath(){
        if (System.getProperty("os.name").toLowerCase() instanceof String os && os.contains("win")) {
           return String.valueOf(Paths.get(System.getenv("APPDATA")));
        }
        return String.valueOf(Paths.get(System.getProperty("user.home")));

    }

    public static Properties getProperties(){
        Properties prop =new Properties();
        try{
            prop.load(ExtendedIO.class.getClassLoader().getResourceAsStream("application.properties"));
            return prop;
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        return null;
    }
}
