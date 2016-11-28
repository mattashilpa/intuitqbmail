package com.intuit.gmail.common;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {
	public static Logger logger = Logger.getLogger(BaseTest.class);
	
	@BeforeClass
	public void setUpRA(){
		DOMConfigurator.configure("log4j.xml");
		String className = this.getClass().getName();
		logger.info("Running Test Class **** " + className + " ****");
	}
	
	@AfterClass
	public void tearDown(){
		String className = this.getClass().getName();
		logger.info("Execution Complete Test Class **** " + className + " ****");
			
	}
}
