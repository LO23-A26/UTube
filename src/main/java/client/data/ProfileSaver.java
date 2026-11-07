package client.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;

/**
 * Class that save client data on user's computer
 *
 * @author Lucas Dompnier
 * @version 2.0
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

        // Creating the user profile directory if not exists
        String user_profile_directory = profile_dir + "\\" + user.getSharedInfo().getUuid();
        if (createDirectory(user_profile_directory)) {
            System.out.println("Profile directory created successfully at: " + user_profile_directory);
        } else {
            System.out.println("Failed to create profile directory. It may already exist at: " + user_profile_directory);
        }

        // Copy the avatar to the directory
        File avatar_file = new File(user.getSharedInfo().getAvatarPath());
        String new_avatar_path = user_profile_directory + "\\" + avatar_file.getName();
        try {
            Files.copy(Path.of(user.getSharedInfo().getAvatarPath()),
                    Path.of(new_avatar_path),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        user.getSharedInfo().setAvatarPath(new_avatar_path);

        // Saving the user into a file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(user_profile_directory + "\\user.ser");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectOutputStream oos;
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

    public static void deleteDirectory(File file)
    {
        File[] files = file.listFiles();

        // Check whether the directory can be accessed
        if (files == null) {
            return;
        }

        // Traverse all files and subdirectories
        for (File subfile : files) {

            // If it is a subdirectory, recursively delete
            // its contents
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }

            // Delete the file or empty directory
            if(subfile.delete()){
                System.out.println(subfile.getName() + "successfully deleted");
            }
        }
        if(file.delete()){
            System.out.println(file.getName() + "successfully deleted");
        }
    }

    /**
     * Delete file profile with the user
     * @param user: the user to delete
     */
    public void deleteProfile(UserLight user) {
        // Delete the user's directory
        deleteDirectory(new File(profile_dir + "\\" + user.getUuid()));
    }

    /**
     * Lists all the files in a directory
     * @param dir : directory where we want to list the files
     * @return set of the name of the files
     */
    public Set<String> listDirectory(String dir) {
        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(File::isDirectory)
                .map(File::getAbsolutePath)
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

        Set<UserFull> users = new HashSet<>();

        // List all the profile files
        for(String dir_name : listDirectory(profile_directory)){
            // Open the file
            FileInputStream fis;
            try {
                fis = new FileInputStream(dir_name + "\\user.ser");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Convert the file into usable UserFull object
            UserFull user_parsed;
            try {
                user_parsed = (UserFull)ois.readObject(); // down-casting object
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // set manually the avatar image
            user_parsed.getSharedInfo().setAvatar(user_parsed.getSharedInfo().getAvatarPath());

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
