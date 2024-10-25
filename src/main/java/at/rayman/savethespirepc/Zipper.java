package at.rayman.savethespirepc;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private static final int BUFFER_SIZE = 6 * 1024;

    public static Result zip() {
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(Constants.ZIP_PATH)))) {
            zipDirectory(new File(Constants.PREFERENCES_PATH), Constants.PREFERENCES, zos);
            zipDirectory(new File(Constants.RUNS_PATH), Constants.RUNS, zos);
            zipDirectory(new File(Constants.SAVES_PATH), Constants.SAVES, zos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.error("File not found: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("IOException: " + e.getMessage());
        }
        return Result.success("Zip saved to: " + Constants.ZIP_PATH);
    }

    private static void zipDirectory(File directory, String path, ZipOutputStream zos) throws IOException {
        if (!directory.isDirectory()) {
            throw new IOException("Source path must be a directory.");
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String filePath = path + file.getName();
                if (file.isDirectory()) {
                    zipDirectory(file, filePath + "/", zos);
                } else {
                    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE)) {
                        zos.putNextEntry(new ZipEntry(filePath));
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int len;
                        while ((len = bis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}