package testTask;

import io.github.bonigarcia.wdm.WebDriverManager;
import jdk.jfr.Category;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FormTest {
    private WebDriver driver;
    private LoginPage loginPage;
    FormInputsPage inputsPage;
    @BeforeAll
    static void registerDriver() {
        //Код для исправления совместимости с ChromeDriver 115+
        //WebDriverManager.chromedriver().clearDriverCache().setup();
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupBrowser() {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--incognito");
        //options.addArguments("--headless");
        //options.addArguments("start-maximized");
        driver = new ChromeDriver(options);

        Path sampleFile = Paths.get("src/test/resources/qa-test.html");
        driver.get(sampleFile.toUri().toString());
        loginPage = new LoginPage(driver);
        inputsPage = new FormInputsPage(driver);
    }

    @Test
    @DisplayName("Переход со страницы логина и добавление двух новых строк")
    void loginAndFillTheTableWithValidData(){
        // Заполняю и отправляю форму
        loginPage.login("test@protei.ru", "test")
                .inputEmail("test@protei.ru")
                .inputName("Ксения")
                .selectGender("Женский")
                .selectCheckboxes(1)
                .selectRadioVariant(1)
                .sendForm();

        // Проверяем что появилось модальное окно, блокирующее взаимодействие с сайтом
        assertAll(
                () -> assertThat(inputsPage.getModalDialogContentElement().isDisplayed()).isTrue(),
                () -> assertThat(inputsPage.getModalDialogContentElement().getText()).isEqualTo("Данные добавлены.")
        );
        // Клик по заблокированному модальным окном элементу вернет ElementClickInterceptedException
        assertThrows(ElementClickInterceptedException.class,
                ()->{
                    inputsPage.getSendFormButtonElement().click();
                });
        // Закрываем окно и убеждаемся что все данные записаны на свои места
        inputsPage.getModalDialogOKButton().click();
        assertAll(
                () -> assertThat(inputsPage.checkIfColumnInTableContains("E-Mail", "test@protei.ru")).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Имя", "Ксения")).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Пол", "Женский")).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Выбор 1", "1.1")).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Выбор 2", "2.1")).isTrue()
        );
        // Отправляем форму, убеждаясь что после закрытия модального окна таблица доступна. Модальное окно вновь появилось.
        inputsPage.getSendFormButtonElement().click();
        assertAll(
                () -> assertThat(inputsPage.getModalDialogContentElement().isDisplayed()).isTrue(),
                () -> assertThat(inputsPage.getModalDialogContentElement().getText()).isEqualTo("Данные добавлены.")
        );
    }

    @ParameterizedTest
    @DisplayName("Переход со страницы логина и добавление строк с валидными данными из таблицы pairwise")
    @CsvSource({
            "test@protei.ru,Кириллица,Мужской,1,1",
            "test@protei.ru,Латиница,Женский,2,2",
            "test@protei.ru,Кириллица,Мужской,12,3",
            "test@protei.ru,Латиница,Женский,0,0",
            "test@protei.ru,Латиница,Мужской,0,1",
            "test@protei.ru,Кириллица,Женский,1,2",
            "test@protei.ru,Латиница,Мужской,2,3",
            "test@protei.ru,Кириллица,Женский,12,0",
            "test@protei.ru,Кириллица,Мужской,12,1",
            "test@protei.ru,Латиница,Женский,0,2",
            "test@protei.ru,Кириллица,Мужской,1,3",
            "test@protei.ru,Латиница,Женский,2,0",
            "test@protei.ru,Латиница,Мужской,2,1",
            "test@protei.ru,Кириллица,Женский,12,2",
            "test@protei.ru,Латиница,Мужской,0,3",
            "test@protei.ru,Кириллица,Женский,1,0",
    })
    void fillInTheTableUsingPairwiseTable(String email, String nameFormat, String gender, int checkBox, int radioButton){
        // Заполняем форму
        String name;
        if(nameFormat.equals("Кириллица")){
            name = "Алекс";
        } else if(nameFormat.equals("Латиница"))
            name = "Alex";
        else {
            name = "";
        }

        loginPage.login("test@protei.ru", "test")
                .inputEmail(email)
                .inputName(name)
                .selectGender(gender)
                .selectCheckboxes(checkBox)
                .selectRadioVariant(radioButton)
                .sendForm();

        // Закрываем модальное окно и убеждаемся что все данные записаны на свои места
        inputsPage.getModalDialogOKButton().click();

        assertAll(
                () -> assertThat(inputsPage.checkIfColumnInTableContains("E-Mail", email)).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Имя", name)).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Пол", gender)).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Выбор 1", inputsPage.getResultCheckBox())).isTrue(),
                () -> assertThat(inputsPage.checkIfColumnInTableContains("Выбор 2", inputsPage.getResultRadioButton())).isTrue()
        );
        inputsPage.clearTempVariables();
    }

    @ParameterizedTest
    @DisplayName("Пустой email")
    @CsvSource({
            "0,Латиница,Мужской,0,0",
            "0,Кириллица,Женский,0,0",
    })
    void fillInTheTableWithEmptyEmail(String email, String nameFormat, String gender, int checkBox, int radioButton){
        // Заполняем форму
        if(email.equals("0")){
            email = "";
        }
        String name;
        if(nameFormat.equals("Кириллица")){
            name = "Алекс";
        }else if(nameFormat.equals("Латиница"))
            name = "Alex";
        else {
            name = "";
        }

        loginPage.login("test@protei.ru", "test")
                .inputEmail(email)
                .inputName(name)
                .selectGender(gender)
                .selectCheckboxes(checkBox)
                .selectRadioVariant(radioButton)
                .sendForm();

        assertAll(
                () -> assertThat(inputsPage.getEmailFormatErrorElement().isDisplayed()).isTrue(),
                () -> assertThat(inputsPage.getEmailFormatErrorElement().getText()).isEqualTo("Неверный формат E-Mail")
        );
        inputsPage.closeAlertWindow();
        assertThrows(NoSuchElementException.class,
                ()->{
                    driver.findElement(By.id(inputsPage.getEmailFormatErrorId()));
                });
        inputsPage.clearTempVariables();
    }

    @ParameterizedTest
    @DisplayName("Невалидный email")
    @CsvSource({
            "Abc.example.com,Кириллица,Мужской,0,0",
            "A@b@c@example.com,Латиница,Женский,0,0",
            "a@.c.com,Латиница,Мужской,0,0",
            "a@a,Кириллица,Женский,0,0"
    })
    void fillInTheTableUsingInvalidEmail(String email, String nameFormat, String gender, int checkBox, int radioButton){
        // Заполняем форму
        String name;
        if(nameFormat.equals("Кириллица")){
            name = "Алекс";
        }else if(nameFormat.equals("Латиница"))
            name = "Alex";
        else {
            name = "";
        }

        loginPage.login("test@protei.ru", "test")
                .inputEmail(email)
                .inputName(name)
                .selectGender(gender)
                .selectCheckboxes(checkBox)
                .selectRadioVariant(radioButton)
                .sendForm();

        assertAll(
                () -> assertThat(inputsPage.getEmailFormatErrorElement().isDisplayed()).isTrue(),
                () -> assertThat(inputsPage.getEmailFormatErrorElement().getText()).isEqualTo("Неверный формат E-Mail")
        );
        inputsPage.closeAlertWindow();
        assertThrows(NoSuchElementException.class,
                ()->{
                    driver.findElement(By.id(inputsPage.getEmailFormatErrorId()));
                });
        inputsPage.clearTempVariables();
    }

    @ParameterizedTest
    @DisplayName("Пустое имя")
    @CsvSource({
            "test@protei.ru,0,Мужской,0,0",
            "test@protei.ru,0,Женский,0,0"
    })
    void fillInTheTableUsingEmptyName(String email, String nameFormat, String gender, int checkBox, int radioButton){
        // Заполняем форму
        String name;
        if(nameFormat.equals("Кириллица")){
            name = "Алекс";
        }else if(nameFormat.equals("Латиница"))
            name = "Alex";
        else {
            name = "";
        }

        loginPage.login("test@protei.ru", "test")
                .inputEmail(email)
                .inputName(name)
                .selectGender(gender)
                .selectCheckboxes(checkBox)
                .selectRadioVariant(radioButton)
                .sendForm();

        assertAll(
                () -> assertThat(inputsPage.getBlankNameErrorElement().isDisplayed()).isTrue(),
                () -> assertThat(inputsPage.getBlankNameErrorElement().getText()).isEqualTo("Поле имя не может быть пустым")
        );
        inputsPage.closeAlertWindow();
        assertThrows(NoSuchElementException.class,
                ()->{
                    driver.findElement(By.id(inputsPage.getBlankNameErrorId()));
                });
        inputsPage.clearTempVariables();
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
