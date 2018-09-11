package ua.sam.ignite.shared.data;

import java.util.Map;

public class ContactWithoutBinary {

    private int id;
    private Map<String, CustomInfo> customFields;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, CustomInfo> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, CustomInfo> customFields) {
        this.customFields = customFields;
    }
}
