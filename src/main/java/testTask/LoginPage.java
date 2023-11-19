package testTask;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends AbstractPage {
    private final String emailFieldId = "loginEmail";
    private final String passwordFieldId = "loginPassword";
    private final String authButtonFieldId = "authButton";
    private final String emailFormatErrorId = "emailFormatError";
    private final String invalidLoginCredentialsErrorId = "invalidEmailPassword";
    private final String closeAlertWindowXpath = "//*[@class=\"uk-alert-close uk-close\"]";

    @FindBy(id = emailFieldId)
    private WebElement emailField;
    @FindBy(id = passwordFieldId)
    private WebElement passwordField;
    @FindBy(id = authButtonFieldId)
    private WebElement authButton;
    @FindBy(id = emailFormatErrorId)
    private WebElement emailFormatError;
    @FindBy(id = invalidLoginCredentialsErrorId)
    private WebElement invalidLoginCredentialsError;
    @FindBy(xpath = closeAlertWindowXpath)
    private WebElement closeAlertWindowX;

    public String getEmailFormatErrorId() {
        return emailFormatErrorId;
    }
    public String getInvalidLoginCredentialsErrorId(){
        return invalidLoginCredentialsErrorId;
    }
    public WebElement getEmailFormatErrorElement() {
        wait.until(ExpectedConditions.visibilityOf(emailFormatError));
        return emailFormatError;
    }
    public WebElement getInvalidLoginCredentialsErrorElement() {
        wait.until(ExpectedConditions.visibilityOf(invalidLoginCredentialsError));
        return invalidLoginCredentialsError;
    }
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    public void closeAlertWindow(){
        wait.until(ExpectedConditions.visibilityOf(closeAlertWindowX));
        closeAlertWindowX.click();
        wait.until(ExpectedConditions.invisibilityOf(closeAlertWindowX));
    }
    public FormInputsPage login(String userName, String password){
        wait.until(ExpectedConditions.visibilityOfAllElements(emailField, passwordField, authButton));
        emailField.sendKeys(userName);
        passwordField.sendKeys(password);
        authButton.click();
        return new FormInputsPage(driver);
    }
}

