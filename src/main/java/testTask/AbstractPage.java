package testTask;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AbstractPage {
    public static WebDriver driver;
    public static WebDriverWait wait;

    public AbstractPage(WebDriver driver) {
        AbstractPage.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        PageFactory.initElements(driver, this);
    }
}
