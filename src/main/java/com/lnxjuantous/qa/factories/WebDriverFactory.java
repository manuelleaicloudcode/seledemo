package com.lnxjuantous.qa.factories;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Factory class to create and manage WebDriver instances.
 * [MadeBt:T::J]
 * It supports both local and remote (Selenium Grid) execution and is thread-safe
 * by using ThreadLocal.
 */
public class WebDriverFactory {

    // ThreadLocal to ensure each thread gets its own WebDriver instance
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static Properties prop;

    // Load properties from the classpath when the class is loaded
    static {
        prop = new Properties();
        try (InputStream input = WebDriverFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find config.properties in the classpath");
            }
            prop.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading configuration properties.", ex);
        }
    }

    /**
     * Gets the WebDriver instance for the current thread.
     * If it doesn't exist, it creates a new one based on the configuration.
     *
     * @return The WebDriver instance.
     */
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            boolean isGridEnabled = Boolean.parseBoolean(prop.getProperty("grid.enabled"));
            String browser = prop.getProperty("browser");
            
            System.out.println("Grid enabled: " + isGridEnabled);
            System.out.println("Browser: " + browser);

            if (isGridEnabled) {
                driver.set(initRemoteDriver(browser));
            } else {
                driver.set(initLocalDriver(browser));
            }
        }
        return driver.get();
    }

    /**
     * Initializes a RemoteWebDriver for execution on a Selenium Grid.
     *
     * @param browser The browser to use (e.g., "chrome", "firefox").
     * @return A RemoteWebDriver instance.
     */
    private static WebDriver initRemoteDriver(String browser) {
        WebDriver remoteDriver = null;
        try {
            String gridUrl = prop.getProperty("grid.url");
            System.out.println("Connecting to Selenium Grid at: " + gridUrl);
            
            switch (browser.toLowerCase()) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    // These arguments are essential for running Chrome in a Docker container
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-dev-tools-extensions");
                    remoteDriver = new RemoteWebDriver(new URL(gridUrl), chromeOptions);
                    break;
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--disable-dev-tools-extensions");
                    remoteDriver = new RemoteWebDriver(new URL(gridUrl), firefoxOptions);
                    break;
                default:
                    throw new IllegalArgumentException("Browser not supported for Grid: " + browser);
            }
            System.out.println("Successfully created remote driver for " + browser);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Grid URL: " + prop.getProperty("grid.url"), e);
        }
        return remoteDriver;
    }

    /**
     * Initializes a local WebDriver instance.
     *
     * @param browser The browser to use (e.g., "chrome", "firefox").
     * @return A local WebDriver instance.
     */
    private static WebDriver initLocalDriver(String browser) {
        WebDriver localDriver = null;
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                // You can add local-specific options here, e.g., chromeOptions.addArguments("--headless");
                localDriver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                localDriver = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Browser not supported for local execution: " + browser);
        }
        System.out.println("Successfully created local driver for " + browser);
        return localDriver;
    }

    /**
     * Quits the WebDriver instance for the current thread and removes it from ThreadLocal.
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}