package example;

import java.util.logging.Level;

/**
 * Created by magicmicky on 13/01/16.
 */
public class TypedMessage {
    public String type;
    public TypedMessage(String type) {
        this.type = type;
        java.util.logging.Logger.getLogger("OMG").log(Level.INFO, "TypedMessage created");

    }
}
