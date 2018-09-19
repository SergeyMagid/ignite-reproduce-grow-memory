package ua.sam.ignite.shared.data;

import org.apache.ignite.binary.BinaryObject;

import java.util.Map;

public class ContactDto {

    private int contactId;

    private String firstName;

    private Map<Integer, BinaryObject> staticFields;

    private Map<Integer, Integer> fieldsDropdown;

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Map<Integer, BinaryObject> getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(Map<Integer, BinaryObject> staticFields) {
        this.staticFields = staticFields;
    }

    public Map<Integer, Integer> getFieldsDropdown() {
        return fieldsDropdown;
    }

    public void setFieldsDropdown(Map<Integer, Integer> fieldsDropdown) {
        this.fieldsDropdown = fieldsDropdown;
    }
}
