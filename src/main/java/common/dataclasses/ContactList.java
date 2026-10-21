package common.dataclasses;
import java.util.UUID;
import java.util.Set;

/**
 * @author Lucas Dompnier
 * @version 1.0
 */
public class ContactList {

    private String list_name;
    private Set<UserLight> contacts;
    private UUID list_id; //Ajout d'un list_id pour coller avec IhmMainCallsDataClient deleteContactList. Usage de list_name pour SetVideoAcces. A harmoniser
    private AccessRight right;
    //1 AccessRight par liste à corriger aprsè merge => 1 accesright par vidéo, attribué à une liste

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
