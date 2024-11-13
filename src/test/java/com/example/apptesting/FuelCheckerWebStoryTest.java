package AppTesting;


import java.time.Duration;
import java.time.LocalDateTime;
import java.io.File;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import java.nio.file.Paths;

//
//Description here
//
public class FuelCheckerWebStoryTest {


 WebDriver driver;
 Wait<WebDriver> wait;

 // URL for the application to test

 String url = Paths.get("src", "test", "resources", "fuelchecker", "fuelchecker.html").toUri().toString();
 String webdriver;

 // Determine the OS and architecture, and set the webdriver path accordingly
 String os = System.getProperty("os.name").toLowerCase();
 String arch = System.getProperty("os.arch").toLowerCase();


 @BeforeClass
 public void setupDriver() throws Exception {
     if (os.contains("win")) {
         if (arch.contains("64")) {
             webdriver = Paths.get("src", "test", "resources", "WebDriver", "Win64", "chromedriver.exe").toAbsolutePath().toString();
         } else {
             webdriver = Paths.get("src", "test", "resources", "WebDriver", "Win32", "chromedriver.exe").toAbsolutePath().toString();
         }
     } else if (os.contains("mac")) {
         if (arch.contains("arm")) {
             webdriver = Paths.get("src", "test", "resources", "WebDriver", "MacArm64", "chromedriver").toAbsolutePath().toString();
         } else {
             webdriver = Paths.get("src", "test", "resources", "WebDriver", "Mac", "chromedriver").toAbsolutePath().toString();
         }
     } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
         if (arch.contains("64")) {
             webdriver = Paths.get("src", "test", "resources", "WebDriver", "Linux64", "chromedriver").toAbsolutePath().toString();
         } else {
             webdriver = Paths.get("src", "test", "resources", "WebDriver", "Linux32", "chromedriver").toAbsolutePath().toString();
         }
     }
     System.out.println("Test started at: "+LocalDateTime.now());
     if (url==null)
         throw new Exception("Test URL not defined: use -Durl=<url>");
     System.out.println("For URL: "+url);
     System.out.println();
     // Create web driver (this code uses chrome)
     if (webdriver==null)
         throw new Exception("Web driver not defined: use -Dwebdriver=<filename>");
     if (!new File(webdriver).exists())
         throw new Exception("Web driver missing: "+ System.getProperty("webdriver"));
     System.setProperty("webdriver.chrome.driver", webdriver);
     driver = new ChromeDriver();
     // Create wait
     wait = new WebDriverWait( driver, Duration.ofSeconds(5) );
     // Open web page
     driver.get( url );
 }

 @AfterClass
 public void shutdown() {
     driver.quit();
 }

 @AfterMethod
 public void returnToMain() {
     // If test has not left app at the main window, try to return there for the next test
     if ("Results".equals(driver.getTitle()))
         driver.findElement(By.id("Continue")).click();
     else if ("Fuel Checker Information".equals(driver.getTitle()))
         driver.findElement(By.id("goback")).click();
     else if ("Thank you".equals(driver.getTitle()))
         driver.get( url ); // only way to return to main screen from here
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
 }

 // Tests go here
 

 @Test(timeOut=60000)
 public void test_T1() {
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
     wait.until(ExpectedConditions.visibilityOfElementLocated( By.id("litres")));
     driver.findElement(By.id("litres")).sendKeys("1000");
     wait.until(ExpectedConditions.visibilityOfElementLocated( By.id("highsafety")));
     if (driver.findElement( By.id("highsafety")).isSelected()!=false)
         driver.findElement( By.id("highsafety")).click();
     wait.until(ExpectedConditions.visibilityOfElementLocated( By.id("Enter")));
     driver.findElement( By.id("Enter")).click();
     wait.until(ExpectedConditions.titleIs("Results"));
     wait.until(ExpectedConditions.visibilityOfElementLocated( By.id("result")));
     assertEquals( driver.findElement( By.id("result")).getAttribute("value"),"Fuel fits in tank." );
     wait.until(ExpectedConditions.visibilityOfElementLocated( By.id("Continue")));
     driver.findElement( By.id("Continue")).click();
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
 }

 @Test(timeOut=60000)
 public void test_T2() {
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
     driver.findElement(By.id("litres")).sendKeys("400");
     driver.findElement(By.id("highsafety")).click(); // Select highsafety
     driver.findElement(By.id("Enter")).click();
     wait.until(ExpectedConditions.titleIs("Results"));
     assertEquals(driver.findElement(By.id("result")).getAttribute("value"), "Fuel fits in tank.");
     driver.findElement(By.id("Continue")).click();
 }

 @Test(timeOut=60000)
 public void test_T3() {
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
     driver.findElement(By.id("litres")).sendKeys("2000");
     if (driver.findElement(By.id("highsafety")).isSelected())
         driver.findElement(By.id("highsafety")).click(); // Deselect highsafety
     driver.findElement(By.id("Enter")).click();
     wait.until(ExpectedConditions.titleIs("Results"));
     assertEquals(driver.findElement(By.id("result")).getAttribute("value"), "Fuel does not fit in tank.");
     driver.findElement(By.id("Continue")).click();
 }

 @Test(timeOut=60000)
 public void test_T4() {
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
     driver.findElement(By.id("litres")).sendKeys("1000");
     if (!driver.findElement(By.id("highsafety")).isSelected())
         driver.findElement(By.id("highsafety")).click(); // Select highsafety
     driver.findElement(By.id("Enter")).click();
     wait.until(ExpectedConditions.titleIs("Results"));
     assertEquals(driver.findElement(By.id("result")).getAttribute("value"), "Fuel does not fit in tank.");
     driver.findElement(By.id("Continue")).click();
 }

 @Test(timeOut=60000)
 public void test_T5() {
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
     driver.findElement(By.id("Info")).click();
     wait.until(ExpectedConditions.titleIs("Fuel Checker Information"));
     String bodyText = driver.findElement(By.tagName("body")).getText();
     assertTrue(bodyText.contains("Standard tank capacity: 1200 litres"));
     assertTrue(bodyText.contains("High safety tank capacity: 800 litres"));
     driver.findElement(By.id("goback")).click();
 }

 @Test(timeOut=60000)
 public void test_T6() {
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
     driver.findElement(By.id("exitlink")).click();
     wait.until(ExpectedConditions.titleIs("Thank you"));
     String bodyText = driver.findElement(By.tagName("body")).getText();
     assertTrue(bodyText.contains("Thank you for using fuelchecker."));
     driver.get(url); // Return to main screen
 }

 @Test(timeOut=60000)
 public void test_T7() {
     wait.until(ExpectedConditions.titleIs("Fuel Checker"));
     driver.findElement(By.id("litres")).sendKeys("xxx");
     driver.findElement(By.id("highsafety")).click(); // Select highsafety
     driver.findElement(By.id("Enter")).click();
     wait.until(ExpectedConditions.titleIs("Results"));
     assertEquals(driver.findElement(By.id("result")).getAttribute("value"), "Invalid data values.");
     driver.findElement(By.id("Continue")).click();
 }


}
