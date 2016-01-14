package example;

/**
 * Created by magicmicky on 10/12/15.
 */
public class ConfigMessage extends TypedMessage{
    public int responseLength, responseTime;

    public ConfigMessage(String type, int delay, int messageSize) {
        super(type);
        this.responseTime= delay;
        this.responseLength = messageSize;
    }
}
