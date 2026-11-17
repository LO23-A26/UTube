package common.dataclasses;

import java.nio.file.Path;

/**
 * Supported video formats.
 *
 * @author Jean Van Gysel
 * @version 1.0
 */
public enum VideoFormat {
    mp4,
    avi;
    
    public static VideoFormat checkFormatFromPath(Path file) {
		if (file == null || file.getFileName() == null) {
			throw new IllegalArgumentException("fichier invalide : " + file);
		}
		String name = file.getFileName().toString();
		int dot = name.lastIndexOf('.');
		if (dot < 0 || dot == name.length() - 1) {
			throw new IllegalArgumentException("le fichier n'a pas d'extension : " + name);
		}
		String extension = name.substring(dot + 1);
		for (VideoFormat format : values()) {
			if (format.name().equalsIgnoreCase(extension)) {
				return format;
			}
		}
		throw new IllegalArgumentException("format non pris en charge : " + name);
	}

	public static boolean isSupported(Path file) {
		try {
			checkFormatFromPath(file);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
    
    
}
