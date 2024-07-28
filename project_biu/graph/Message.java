package graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    /**
 * Constructs a new Message object with the given string data.
 *
 * @param data The string data to be stored in the Message object.
 *             If the data cannot be parsed as a double, NaN (Not a Number) will be stored.
 *
 * @throws NullPointerException If the provided data is null.
 */
public Message(String data) {
    if (data == null) {
        throw new NullPointerException("Data cannot be null");
    }

    this.date = new Date();
    this.data = data.getBytes();
    this.asText = data;
    double temp;
    try {
        temp = Double.parseDouble(data);
    } catch (NumberFormatException e) {
        temp = Double.NaN; // If it's not a number
    }
    this.asDouble = temp;
}

    public Message(byte[] data) {
        this(new String(data));
    }

    public Message (double data) {
        this(String.valueOf(data));
    }

}
