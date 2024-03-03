package testTask;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginTest {
    private WebDriver driver;
    private LoginPage loginPage;
    FormInputsPage inputsPage;

    @BeforeAll
    static void registerDriver() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setupBrowser() {
        FirefoxOptions options = new FirefoxOptions();
        //options.addArguments("--incognito");
        //options.addArguments("--headless");
        //options.addArguments("start-maximized");
        driver = new FirefoxDriver(options);

        Path sampleFile = Paths.get("src/test/resources/qa-test.html");
        driver.get(sampleFile.toUri().toString());
        loginPage = new LoginPage(driver);
        inputsPage = new FormInputsPage(driver);
    }
    @Test
    @DisplayName("Авторизация с валидными данными")
    void loginUsingValidCredentials(){
        loginPage.login("test@protei.ru", "test");
        assertAll(
                () ->  assertThat(inputsPage.getSendFormButtonElement().isDisplayed()).isTrue() //Убеждаемся, что попали в новый раздел сайта
        );
    }
    @ParameterizedTest
    @DisplayName("Авторизация с невалидными данными")
    @CsvSource(
            {"invalid@protei.ru, test",
             "test@protei.ru, invalid",
             "invalid@protei.ru, invalid"
            })
    void loginUsingInvalidCredentials(String email, String password) {
        loginPage.login(email, password);
        assertAll(
                () -> assertThat(loginPage.getInvalidLoginCredentialsErrorElement().isDisplayed()).isTrue(),
                () -> assertThat(loginPage.getInvalidLoginCredentialsErrorElement().getText()).isEqualTo("Неверный E-Mail или пароль")
        );

        loginPage.closeAlertWindow();
        assertThrows(NoSuchElementException.class,
                ()->{
                    driver.findElement(By.id(loginPage.getInvalidLoginCredentialsErrorId()));
                });
    }
    @ParameterizedTest
    @DisplayName("Авторизация с некорректным E-mail")
    @ValueSource(strings = {// Проверка наличия @
                    "Abc.example.com", // Нет @
                    "A@b@c@example.com", // Больше одной @

            // Проверка наличия домена
                    "123", // Нет домена
                    "a@b", // Нет домена

            // Проверка наличия точки после домена
                    "a@.c.com", // Начинается с точки
                    "a@b.c.", // Лишняя точка в конце
                    "a@b", // Нет точки

            // Проверка наличия символов перед @
                    ".a@c.com", // Начинается с точки
                    "a.@b.com", // Заканчивается точкой
                    "a..b@c.com", // Две точки подряд
                    "a b@c.com", // Есть пробел
                    "a#b@c.com", // Есть спецсимвол

            // Проверка наличия символов после @
                    "a@b..c.com", // Две точки подряд
                    "a@b c.com", // Есть пробел
                    "a@b#c.com", // Есть спецсимвол
    })
    void loginUsingInvalidEmail(String email){
        loginPage.login(email, "test");
        assertAll(
                () -> assertThat(loginPage.getEmailFormatErrorElement().isDisplayed()).isTrue(),
                () -> assertThat(loginPage.getEmailFormatErrorElement().getText()).isEqualTo("Неверный формат E-Mail")
        );
        loginPage.closeAlertWindow();
        assertThrows(NoSuchElementException.class,
                ()->{
                    driver.findElement(By.id(loginPage.getEmailFormatErrorId()));
                });
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}

