package client.data;

import common.dataclasses.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that save client data on user's computer
 *
 * @author Lucas Dompnier
 * @version 1.0
 */
public class ProfileSaver {

    static final String user_dir = System.getProperty("user.home") + "\\AppData\\Local\\Utube";
    static final String profile_dir = user_dir + "\\profile";

    /**
     * Create a directory in user's computer
     * @param directory_path: path of the directory
     */
    public boolean createDirectory(String directory_path){
        // Defining the directory of the app
        File directory = new File(directory_path);
        // Attempt to create the directory
        return directory.mkdir();
    }

    /**
     * Save the profile of the user in his computer
     * @param user: user object to save
     */
    public void saveProfile(UserFull user){

        // Creating the base directory if not exists
        String base_directory = user_dir;
        if (createDirectory(base_directory)) {
            System.out.println("Directory created successfully at: " + base_directory);
        } else {
            System.out.println("Failed to create directory. It may already exist at: " + base_directory);
        }

        // Creating the profile directory if not exists
        String profile_directory = profile_dir;
        if (createDirectory(profile_directory)) {
            System.out.println("Profile directory created successfully at: " + profile_directory);
        } else {
            System.out.println("Failed to create profile directory. It may already exist at: " + profile_directory);
        }

        // Saving the user into a file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(profile_dir + "\\" + user.getSharedInfo().getUuid() + ".ser");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // closing stream
        try {
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Delete file profile with the user
     * @param user: the user to delete
     */
    public void deleteProfile(UserLight user){
        // Delete the File
        try {
            Files.delete(Path.of(profile_dir + "\\" + user.getUuid() + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("File deleted successfully");
    }

    /**
     * Lists all the files in a directory
     * @param dir : directory where we want to list the files
     * @return set of the name of the files
     */
    public Set<String> listFiles(String dir) {
        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Restore all the profile that's on the user's computer
     * @return Set of the UserFull restored
     */
    public Set<UserFull> restoreProfile(){

        String base_directory = user_dir;
        if (createDirectory(base_directory)) {
            System.out.println("UTube home directory created successfully at: " + base_directory);
        } else {
            System.out.println("Failed to create UTube home directory. It may already exist at: " + base_directory);
        }

        String profile_directory = profile_dir;
        if (createDirectory(profile_directory)) {
            System.out.println("Profile directory created successfully at: " + profile_directory);
        } else {
            System.out.println("Failed to create profile directory. It may already exist at: " + profile_directory);
        }

        Set<UserFull> users = new HashSet<UserFull>();

        // List all the profile files
        for(String file_name : listFiles(profile_directory)){

            // Open the file
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(profile_directory + "\\" + file_name);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Convert the file into usable UserFull object
            UserFull user_parsed = null;
            try {
                user_parsed = (UserFull)ois.readObject(); // down-casting object
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Closing stream
            try {
                ois.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            users.add(user_parsed);
        }

        return users;
    }

}
