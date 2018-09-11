package ua.sam.ignite.shared.data;

import org.apache.ignite.binary.BinaryObject;

import java.util.Map;

public class ContactWithBinary {

    private int id;
    private Map<String, BinaryObject> customFields;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, BinaryObject> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, BinaryObject> customFields) {
        this.customFields = customFields;
    }

}
