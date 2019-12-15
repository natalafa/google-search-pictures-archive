package com.company;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestAppium extends Thread {
    private String pic;
    private String fileName;

    TestAppium(String pic, String fileName) {
        this.pic = pic;
        this.fileName = fileName;
        start();
    }

    public static WebDriver driver;

    public static WebElement getElement(String cssSelector) {
        return getElements(cssSelector).get(0);
    }

    public static List<WebElement> getElements(String cssSelector) {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, 3);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(cssSelector)));
    }

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/Users/nafanaseva/Downloads/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("http://www.google.com/");
        WebElement searchInput = getElement("input[name=q]");
        searchInput.sendKeys("cats");
        searchInput.sendKeys(Keys.ENTER);
        // Click on the "Images" tabs
        getElement("#hdtb-msb .hdtb-mitem:nth-child(2) > .q").click();
        List<TestAppium> picSaveThreads = new ArrayList<>();
        List<WebElement> picturesWebElements = getElements("#isr_mc img[src^=\"data\"]");
        List<String> picFileNames = new ArrayList<>();
        for (int i = 0; i < picturesWebElements.size(); i++) {
            picFileNames.add("picture-" + (i + 1) + ".jpeg");
        }

        for (int i = 0; i < picturesWebElements.size(); i++) {
            WebElement picture = picturesWebElements.get(i);
            String imgSrc = picture.getAttribute("src");
            String encodedImg = imgSrc.substring(imgSrc.indexOf(",") + 1);
            picSaveThreads.add(new TestAppium(encodedImg, picFileNames.get(i)));
        }

        for (TestAppium imgProcessingRoutine : picSaveThreads) {
            imgProcessingRoutine.join();
        }

        System.out.println("Creating a zip archive");
        String archiveName = "output.zip";
        pack(picFileNames, archiveName);
        driver.quit();
    }

    public static void pack(List<String> filesToArchive, String archiveName) {
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





