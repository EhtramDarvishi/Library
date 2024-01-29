package library.Entity;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class Config {

    private static int amountDivision = 1;
    private static String connectionString = null;
    private static String userName = null;
    private static String password = null;

    public static void newInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static String getDbPassword() {
        return password;
    }

    static String getDbUserName() {
        return userName;
    }

    static String getConnectionString() {
        return connectionString;
    }

    static String getJdbcDriver() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean fileExists(String fileName) {
        try {
            File f = new File("./Database.json");
            String content = readFile(f.getPath(), Charset.defaultCharset());
            JSONObject dbConfig = new JSONObject(content);
            connectionString = dbConfig.getString("jdbcURL");
            userName = dbConfig.getString("userName");
            password = dbConfig.getString("password");
        } catch (Exception e) {
        }
        File f = new File(fileName);
        return f.exists();
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
