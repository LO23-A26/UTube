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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;

/**
 * Class that save client data on user's computer
 *
 * @author Lucas Dompnier
 * @version 3.0
 */
public class ProfileSaver {

    static final String user_dir = System.getProperty("user.home") + "\\AppData\\Local\\Utube";
    static final String profile_dir = user_dir + "\\profile";

    /**
     * Convert a zip file that contains a profile into a usable UserFull
     *
     * @param source_path the zip file that contains the profile
     * @return the user retrieved
     */
    public UserFull importProfile(String source_path) {
        String user_profile_directory = profile_dir + "\\";
        UserFull user;
        ZipFile zip_file;
        try {
            // unzip the file
            unzip(source_path,user_profile_directory);

            // get the name of the folder unzipped
            zip_file = new ZipFile(source_path);
            List<String> zip_folders = zip_file.stream()
                    .filter(ZipEntry::isDirectory)
                    .map(ZipEntry::getName)
                    .toList();
            zip_file.close();

            // restore the user
            user = restoreUniqueProfile(user_profile_directory + zip_folders.getFirst());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    // Method to unzip files
    public static void unzip(String zip_file, String dest_folder) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip_file))) {
            ZipEntry entry;
            byte[] buffer = new byte[1024];
            // loop through zip entry
            while ((entry = zis.getNextEntry()) != null) {
                File new_file = new File(dest_folder + File.separator + entry.getName());

                if (entry.isDirectory()) {
                    new_file.mkdirs();
                } else {
                    new File(new_file.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(new_file)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a zip file based on a user profile
     * @param destination_path path of the zip file
     * @param user the user to zip
     */
    public void exportProfile(String destination_path, UserFull user) {
        // save the profile before export it
        saveProfile(user);

        String user_profile_directory = profile_dir + "\\" + user.getSharedInfo().getUuid();
        String zip_file = destination_path + "\\exported_profile_" + user.getSharedInfo().getUuid() + ".zip";

        try {
            zipFolder(user_profile_directory, zip_file);
            DataClientCore.getLogger().log(INFO,"Folder successfully zipped!");
        } catch (IOException e) {
            DataClientCore.getLogger().log(ERROR,"Folder can't be zipped!");
        }
    }

    /**
     * Method to zip a folder and its contents
     * @param source_folder_path Path of the source folder
     * @param zip_file_path Path of the destination ZIP file
     * @throws IOException If an I/O error occurs
     */
    public static void zipFolder(String source_folder_path, String zip_file_path) throws IOException {
        FileOutputStream fos = new FileOutputStream(zip_file_path);
        ZipOutputStream zip_out = new ZipOutputStream(fos);
        File source_folder = new File(source_folder_path);

        zipFiles(source_folder, source_folder.getName(), zip_out);

        zip_out.close();
        fos.close();
    }


    /**
     * Recursive method to add files and directories to the ZIP file
     * @param file_to_zip The file or directory to be added
     * @param file_name The name of the file or directory
     * @param zip_out The ZipOutputStream to write to
     * @throws IOException If an I/O error occurs
     */
    private static void zipFiles(File file_to_zip, String file_name, ZipOutputStream zip_out) throws IOException {
        if (file_to_zip.isHidden()) {
            return;
        }
        if (file_to_zip.isDirectory()) {
            if (file_name.endsWith("/")) {
                zip_out.putNextEntry(new ZipEntry(file_name));
                zip_out.closeEntry();
            } else {
                zip_out.putNextEntry(new ZipEntry(file_name + "/"));
                zip_out.closeEntry();
            }
            File[] children = file_to_zip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFiles(childFile, file_name + "/" + childFile.getName(), zip_out);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(file_to_zip)) {
            ZipEntry zip_entry = new ZipEntry(file_name);
            zip_out.putNextEntry(zip_entry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zip_out.write(bytes, 0, length);
            }
        }
    }

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
            DataClientCore.getLogger().log(INFO,"Directory created successfully at: " + base_directory);
        } else {
            DataClientCore.getLogger().log(INFO,"Failed to create directory. It may already exist at: " + base_directory);
        }

        // Creating the profile directory if not exists
        String profile_directory = profile_dir;
        if (createDirectory(profile_directory)) {
            DataClientCore.getLogger().log(INFO,"Profile directory created successfully at: " + profile_directory);
        } else {
            DataClientCore.getLogger().log(INFO,"Failed to create profile directory. It may already exist at: " + profile_directory);
        }

        // Creating the user profile directory if not exists
        String user_profile_directory = profile_dir + "\\" + user.getSharedInfo().getUuid();
        if (createDirectory(user_profile_directory)) {
            DataClientCore.getLogger().log(INFO,"Profile directory created successfully at: " + user_profile_directory);
        } else {
            DataClientCore.getLogger().log(INFO,"Failed to create profile directory. It may already exist at: " + user_profile_directory);
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
                DataClientCore.getLogger().log(INFO,subfile.getName() + "successfully deleted");
            }
        }
        if(file.delete()){
            DataClientCore.getLogger().log(INFO,file.getName() + "successfully deleted");
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

    public UserFull restoreUniqueProfile(String dir_name){
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

        return user_parsed;
    }

    /**
     * Restore all the profile that's on the user's computer
     * @return Set of the UserFull restored
     */
    public Set<UserFull> restoreProfile(){

        String base_directory = user_dir;
        if (createDirectory(base_directory)) {
            DataClientCore.getLogger().log(INFO,"UTube home directory created successfully at: " + base_directory);
        } else {
            DataClientCore.getLogger().log(INFO,"Failed to create UTube home directory. It may already exist at: " + base_directory);
        }

        String profile_directory = profile_dir;
        if (createDirectory(profile_directory)) {
            DataClientCore.getLogger().log(INFO,"Profile directory created successfully at: " + profile_directory);
        } else {
            DataClientCore.getLogger().log(INFO,"Failed to create profile directory. It may already exist at: " + profile_directory);
        }

        Set<UserFull> users;
        users = listDirectory(profile_directory).stream().map(this::restoreUniqueProfile).collect(Collectors.toSet());

        // List all the profile files

        return users;
    }

}
