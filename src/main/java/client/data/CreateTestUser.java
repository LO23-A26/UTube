package client.data;

import common.dataclasses.UserFull;

import java.time.LocalDate;

public class CreateTestUser {
    public static void main(String[] args){
        UserFull user = new UserFull("test", "test", "Jean", "Dupont", LocalDate.now());
        ProfileSaver saver = new ProfileSaver();
        saver.saveProfile(user);
    }
}
