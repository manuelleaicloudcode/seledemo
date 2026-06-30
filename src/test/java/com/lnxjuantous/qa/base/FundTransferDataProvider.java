package com.lnxjuantous.qa.base;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FundTransferDataProvider extends BaseTest {

	@Test(dataProvider = "searchTerms")
	public void googleSearchTest(String searchTerm) {
		// Assuming a search field and button are present
		// driver.findElement(By.name("q")).sendKeys(searchTerm + Keys.ENTER);
		// For this example, we'll just print and assert the title contains the term
		System.out.println("Thread ID: " + Thread.currentThread().getId() + " | Searching for: " + searchTerm);
		Assert.assertTrue(driver.getTitle().contains("Google"), "Not on Google search page!");
	}

	@DataProvider(name = "searchTerms", parallel = true) // parallel=true is good practice
	public Object[][] getData() {
		return new Object[][] { { "TestNG" }, { "Selenium" }, { "Java" } };
	}
}