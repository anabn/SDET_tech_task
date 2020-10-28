package testUI;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 @author anna fetsun
 @version 1

 Trainee SDET tech task
 It should search movie by title “Back to the future”.
 0. Navigate to https://www.imdb.com/
 1. Make sure the page is opened
 2. Search the movie by title “Back to the future” and open its page
 (keep in mind this is the first part of the trilogy that came out in 1985)
 3. Once you’re on the movie page, make sure the movie description is:
 “Marty McFly, a 17-year-old high school student, is ....”
 */

public class SearchMovieByTitle {
    private WebDriver chromeWebDriver;
    private Logger logger;
    private final String URL_FOR_CHECKING_PAGE = "https://www.imdb.com/";
    private final String MOVIE_TITLE = "Back to the Future";

    // prepare environment for tech task
    private final String pathToLocalWebdriver = "/Users/popcorn/chromedriver";
    @BeforeSuite
    public void setUpChromeDriver() throws Exception {

        // -----------> credentials for execute a webdriver via docker
//
//      DesiredCapabilities dcap = DesiredCapabilities.chrome();
//      String driverPath = System.getProperty("user.dir") + "/exe/chromedriver";
//      System.setProperty("webdriver.chrome.driver", driverPath);
//      URL seleniumServerUrl = new URL("http://localhost:4444/wd/hub");
//      chromeWebDriver = new RemoteWebDriver(seleniumServerUrl, dcap);
//
        System.setProperty("webdriver.chrome.driver", pathToLocalWebdriver);
        chromeWebDriver = new ChromeDriver();
        chromeWebDriver.manage().window().maximize();

        logger = Logger.getLogger(SearchMovieByTitle.class.getName());
        logger.info("start task");
        chromeWebDriver.get(URL_FOR_CHECKING_PAGE);
        logger.info("opening page for testing " + URL_FOR_CHECKING_PAGE);
        chromeWebDriver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
    }

    @Test
    public void checkOpeningPage() throws Exception{
        String expectedPageTitle = "IMDb: Ratings, Reviews, and Where to Watch the Best Movies & TV Shows";
        try {
            Assert.assertEquals(expectedPageTitle, chromeWebDriver.getTitle(), "checking title of current url");
            logger.info("expected page is opened, check by getTitle");
        } catch (Throwable pageNavigationError){
            logger.error("Didn't navigate to correct webpage", pageNavigationError);
        }
    }

    @Test
    public void findMovieByTitleAndCompareDescription() throws Exception {
        String xpathToSearchButton = "//input[@id=\"suggestion-search\"]";
        String expectPageTitle = "Back to the Future (1985) - IMDb";
        String description = "Marty McFly, a 17-year-old high school student, " +
                "is accidentally sent thirty years into the past in a " +
                "time-traveling DeLorean invented by his close friend, " +
                "the eccentric scientist Doc Brown.";
        String xpathToDescription = "//div[@class=\"plot-text\"]/div/div";

        WebElement searchButton = chromeWebDriver.findElement(By.xpath(xpathToSearchButton));
        logger.info("get element by xpath : " + xpathToSearchButton);
        if(searchButton.isEnabled()){
            searchButton.sendKeys(MOVIE_TITLE);
            searchButton.submit();
            logger.info("in field for searching was entered : " + MOVIE_TITLE);
        } else {
            logger.error("search button is not selected");
        }

        logger.info("Open page url : " + chromeWebDriver.getCurrentUrl());
        List<WebElement> elements = chromeWebDriver.findElements(By.linkText(MOVIE_TITLE));
        if(!elements.isEmpty()){
            elements.get(0).click();
            logger.info("Movie title : " + elements.get(0).toString() + " was clicked");
        } else {
            logger.error("list is empty");
        }
        logger.info(chromeWebDriver.getCurrentUrl() + "is current url");
        // time to rendering
        Thread.sleep(100);
        Assert.assertEquals(chromeWebDriver.getTitle(), expectPageTitle, "checking the current url");
        logger.info("test passed: got the expected page");
        String findDescription = chromeWebDriver.findElement(By.xpath(xpathToDescription)).getText();
        logger.info("found : " + findDescription.substring(0, 20));
        Assert.assertEquals(findDescription, description);
        logger.info("descriptions match");
    }

    @AfterClass
    public void closeMethod(){
        chromeWebDriver.close();
        logger.info("task completed");
    }
}
