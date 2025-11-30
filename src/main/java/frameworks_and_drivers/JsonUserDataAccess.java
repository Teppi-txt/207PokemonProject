package frameworks_and_drivers;

import entities.User;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUserDataAccess {

    private final Path filePath;

    public JsonUserDataAccess(String filename) {
        this.filePath = Path.of(filename);
    }

    public User loadUser() {
        try {
            if (!Files.exists(filePath)) {
                return null;
            }

            String text = Files.readString(filePath);
            JSONObject json = new JSONObject(text);
            return User.fromJSON(json);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void saveUser(User user) {
        try {
            Files.writeString(filePath, user.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User get() {
        return loadUser();
    }

    public void save(User newUser) {
        saveUser(newUser);
    }
}
