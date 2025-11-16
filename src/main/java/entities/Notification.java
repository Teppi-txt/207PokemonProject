package entities;

public class Notification {
    private final int id;
    private final int userId;
    private final String message;

    public Notification(int id, int userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;

    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;

    }
}


