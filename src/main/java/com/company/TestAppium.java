package com.company;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestAppium extends Thread {

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
        List<Thread> picSaveThreads = new ArrayList<>();
        List<WebElement> picturesWebElements = getElements("#isr_mc img[src^=\"data\"]");
        List<String> picFileNames = new ArrayList<>();
        for (int i = 0; i < picturesWebElements.size(); i++) {
            picFileNames.add("picture-" + (i + 1) + ".jpeg");
        }

        for (int i = 0; i < picturesWebElements.size(); i++) {
            WebElement picture = picturesWebElements.get(i);
            String imgSrc = picture.getAttribute("src");
            String encodedImg = imgSrc.substring(imgSrc.indexOf(",") + 1);
            Thread thread = new Thread(new ImageFetcher(encodedImg, picFileNames.get(i)));
            thread.start();
            picSaveThreads.add(thread);
        }

        for (Thread imgProcessingRoutine : picSaveThreads) {
            imgProcessingRoutine.join();
        }

        System.out.println("Creating a zip archive");
        Thread thread = new Thread(new ImagePackager(picFileNames));
        thread.start();
        thread.join();
        driver.quit();
    }

}





