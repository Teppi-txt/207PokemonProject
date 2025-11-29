package frameworks_and_drivers;

import entities.User;
import use_case.open_pack.OpenPackUserDataAccessInterface;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUserDataAccess implements OpenPackUserDataAccessInterface {

    private final Path filepath;
    private User cachedUser = null;

    public JsonUserDataAccess(String filename) {
        this.filepath = Path.of(filename);
        load();
    }

    private void load() {
        if (!Files.exists(filepath)) {
            System.out.println("No user data file found. Creating empty user.");
            return;
        }

        try {
            String jsonText = Files.readString(filepath);
            JSONObject json = new JSONObject(jsonText);
            this.cachedUser = User.fromJSON(json);

            System.out.println("Loaded user from JSON.");
        }
        catch (Exception e) {
            System.err.println("Failed to load user JSON: " + e.getMessage());
        }
    }

    /**
     * Saves the user to user.json
     */
    @Override
    public void save(User user) {
        try {
            String json = user.toJSONString();
            Files.writeString(filepath, json);

            this.cachedUser = user;
            System.out.println("User saved to disk.");
        }
        catch (IOException e) {
            throw new RuntimeException("Could not save user JSON", e);
        }
    }

    @Override
    public User get() {
        return cachedUser;
    }
}