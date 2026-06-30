package com.lnxjuantous.qa.base;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

	@Test
	public void verifyGoogleTitle() {
		System.out
				.println("Thread ID: " + Thread.currentThread().getId() + " | Current URL: " + driver.getCurrentUrl());
		String title = driver.getTitle();
		System.out.println("Thread ID: " + Thread.currentThread().getId() + " | Page Title is: " + title);
		assertEquals(title, "Google");
	}
}