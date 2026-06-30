package com.lnxjuantous.qa.base;

import static java.time.Duration.ofSeconds;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.lnxjuantous.qa.factories.WebDriverFactory;

public class BaseTest {

	protected WebDriver driver;

	@BeforeMethod
	@Parameters("browser") // Accepts the parameter from testng.xml
	public void setUp(String browser) {
		driver = WebDriverFactory.getDriver(browser); // Passes the browser to the factory
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(ofSeconds(10));
		driver.get("https://www.google.com");
	}

	@AfterMethod
	public void tearDown() {
		WebDriverFactory.quitDriver();
	}
}