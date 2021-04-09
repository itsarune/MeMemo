package persistence;

import org.json.JSONObject;

/**
 * Writable guarantees that an object can be written as a JSONObject.
 *
 * Taken from JsonSerializationDemo.
 */
public interface Writable {
    //EFFECTS: converts this object into a JSONObject
    JSONObject toJson();
}
