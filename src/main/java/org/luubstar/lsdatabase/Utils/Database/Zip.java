package org.luubstar.lsdatabase.Utils.Database;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {

    public static File extractZip(String path) throws IOException {
        File dest = Files.createTempDirectory("LSDatabase").toFile();
        dest.deleteOnExit();

        ZipInputStream zip = new ZipInputStream(new FileInputStream(path));
        ZipEntry entry = zip.getNextEntry();

        while(entry != null){
            createFile(dest, entry, zip);
            entry = zip.getNextEntry();
        }

        zip.closeEntry();
        zip.close();
        return dest;
    }

    private static void createFile(File dest, ZipEntry entry, ZipInputStream zip) throws IOException {
        File newFile = newFile(dest, entry);
        byte[] buffer = new byte[1024];
        if (entry.isDirectory()) {
            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                throw new IOException("Error creando el directorio " + newFile);
            }
        } else {

            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
                throw new IOException("Error creando el directorio " + parent);
            }

            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zip.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("La entrada está fuera de la localización indicada:  " + zipEntry.getName());
        }

        return destFile;
    }

    public static void save(List<File> files, File objective) throws IOException {
        final FileOutputStream fos = new FileOutputStream(objective);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        save(files, "", zipOut);
        fos.close();
    }

    private static void save(List<File> files, String parent, ZipOutputStream zipOut) throws IOException {
        for (File srcFile : files) {
            if(!srcFile.isDirectory()){saveFile(srcFile, parent, zipOut);}
            else{saveDirectory(srcFile, parent, zipOut);}
        }
        zipOut.close();
    }

    private static void saveFile(File f, String parent, ZipOutputStream zipOut) throws IOException {
        String entry = parent + f.getName();
        FileInputStream fis = new FileInputStream(f);
        ZipEntry zipEntry = new ZipEntry(entry);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    private static void saveDirectory(File scrFile, String parent, ZipOutputStream zipOut) throws IOException {
        String dir = parent + scrFile.getName() + "/";
        zipOut.putNextEntry(new ZipEntry(dir));
        zipOut.closeEntry();
        File[] children = scrFile.listFiles();

        List<File> files = new ArrayList<>(Arrays.asList(children));
        save(files, dir, zipOut );
    }
}
