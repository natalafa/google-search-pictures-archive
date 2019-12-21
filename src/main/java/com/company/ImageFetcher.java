package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageFetcher implements Runnable{
    private String pic;
    private String fileName;

    ImageFetcher (String pic, String fileName) {
        this.pic = pic;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        String lowerCaseFileName = this.fileName.toLowerCase();
        boolean isJpeg = lowerCaseFileName.endsWith("jpeg") || lowerCaseFileName.endsWith("jpg");
        boolean isPng = lowerCaseFileName.endsWith("png");
        String imageType = isJpeg ? "jpeg" : isPng ? "png" : "unknown";
        saveImage(decodeToImage(this.pic), this.fileName, imageType);
    }


    public static void saveImage(BufferedImage bi, String fileName, String imageType) {
        try {
            File outputFile = new File(fileName);
            ImageIO.write(bi, imageType, outputFile);
        } catch (IOException e) {
            System.out.println("Unable to save image. Reason " + e.getMessage());
        }
    }

    public static BufferedImage decodeToImage(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            imageByte = decoder.decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
