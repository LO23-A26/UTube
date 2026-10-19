package main.common.dataclasses;

import java.util.Set;

/**
 * @author Lucas Dompnier
 * @version 1.0
 */
class ContactList {

    private String list_name;
    private Set<UserLight> contacts;

    public ContactList(String list_name) {
        this.list_name = list_name;
    }

    public ContactList(String list_name, Set<UserLight> contacts) {
        this.list_name = list_name;
        this.contacts = contacts;
    }

    /**
     * Add contact to the list of contacts
     *
     * @param user: user to add
     */
    public void addContact(UserLight user) {
        this.contacts.add(user);
    }

    public String getListName() {
        return list_name;
    }

    public void setListName(String list_name) {
        this.list_name = list_name;
    }

    public Set<UserLight> getContacts() {
        return contacts;
    }

    public void setContacts(Set<UserLight> contacts) {
        this.contacts = contacts;
    }
}