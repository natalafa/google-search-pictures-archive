package com.company;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ImagePackager implements Runnable {
    private List<String> filesToArchive;

    public ImagePackager(List<String> filesToArchive) {
        this.filesToArchive = filesToArchive;
    }
    public void pack(String archiveName) {
        byte[] buffer = new byte[1024];
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveName));
            zos.setLevel(Deflater.DEFAULT_COMPRESSION);
            for (String fileToArchive : filesToArchive) {
                System.out.println("Archiving a file " + fileToArchive);
                zos.putNextEntry(new ZipEntry(fileToArchive));
                FileInputStream in = new FileInputStream(fileToArchive);
                int len;
                while ((len = in.read(buffer)) > 0)
                    zos.write(buffer, 0, len);
                zos.closeEntry();
                in.close();
            }
            zos.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Incorrect argument");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File was not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Access error");
        }
    }

    @Override
    public void run() {
        pack("output.zip");
    }
}
