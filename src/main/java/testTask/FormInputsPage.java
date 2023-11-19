package testTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FormInputsPage extends AbstractPage {
    private final String emailFieldId = "dataEmail";
    private final String nameFieldId = "dataName";
    private final String genderSelectorId = "dataGender";
    private final String firstCheckBoxId = "dataCheck11";
    private final String secondCheckBoxId = "dataCheck12";
    private final String radioButtonFirstVariantId = "dataSelect21";
    private final String radioButtonSecondVariantId = "dataSelect22";
    private final String radioButtonThirdVariantId = "dataSelect23";
    private final String sendFormButtonId = "dataSend";
    private final String modalDialogContentXpath = "//*[contains(@class, \"uk-modal-content\")]";
    private final String modalDialogOKButtonXpath = "//*[contains(@class, \"uk-modal-close\")]";
    private final String dataTableId = "dataTable";
    private final String emailFormatErrorId = "emailFormatError";
    private final String blankNameErrorId = "blankNameError";
    private final String closeAlertWindowXpath = "//*[@class=\"uk-alert-close uk-close\"]";

    @FindBy(id = emailFieldId)
    private WebElement emailField;
    @FindBy(id = nameFieldId)
    private WebElement nameField;
    @FindBy(id = genderSelectorId)
    private WebElement genderSelector;
    @FindBy(id = firstCheckBoxId)
    private WebElement firstCheckBox;
    @FindBy(id = secondCheckBoxId)
    private WebElement secondCheckBox;
    @FindBy(id = radioButtonFirstVariantId)
    private WebElement radioFirstVariant;
    @FindBy(id = radioButtonSecondVariantId)
    private WebElement radioSecondVariant;
    @FindBy(id = radioButtonThirdVariantId)
    private WebElement radioThirdVariant;
    @FindBy(id = sendFormButtonId)
    private WebElement sendFormButton;
    @FindBy(xpath = modalDialogContentXpath)
    private WebElement modalDialogContent;
    @FindBy(xpath = modalDialogOKButtonXpath)
    private WebElement modalDialogOKButton;
    @FindBy(id = dataTableId)
    private WebElement dataTable;
    @FindBy(id = emailFormatErrorId)
    private WebElement emailFormatError;
    @FindBy(id = blankNameErrorId)
    private WebElement blankNameError;
    @FindBy(xpath = closeAlertWindowXpath)
    private WebElement closeAlertWindowX;

    public WebElement getSendFormButtonElement() {
        wait.until(ExpectedConditions.visibilityOf(sendFormButton));
        return sendFormButton;
    }
    public WebElement getModalDialogContentElement(){
        wait.until(ExpectedConditions.visibilityOf(modalDialogContent));
        return modalDialogContent;
    }
    public WebElement getModalDialogOKButton(){
        wait.until(ExpectedConditions.visibilityOf(modalDialogOKButton));
        return modalDialogOKButton;
    }
    public WebElement getEmailFormatErrorElement() {
        wait.until(ExpectedConditions.visibilityOf(emailFormatError));
        return emailFormatError;
    }
    public WebElement getBlankNameErrorElement() {
        wait.until(ExpectedConditions.visibilityOf(blankNameError));
        return blankNameError;
    }
    public String getEmailFormatErrorId(){
        return emailFormatErrorId;
    }
    public String getBlankNameErrorId() {
        return blankNameErrorId;
    }
    public FormInputsPage(WebDriver driver) {
        super(driver);
    }
    static List<String> resultCheckBox = new ArrayList<>();
    public String getResultCheckBox() {
        if (!resultCheckBox.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : resultCheckBox) {
                stringBuilder.append(s).append(", ");
            }
            // Убираю из результата 2 последних символа, чтобы не было лишней запятой
            return stringBuilder.substring(0, stringBuilder.length() - 2);
        }
        return "Нет";
    }
    static String resultRadioButton = "";
    public String getResultRadioButton(){
        if(!resultRadioButton.isEmpty()) {
            return resultRadioButton;
        }
        return "";
    }
    public FormInputsPage inputEmail(String email){
        wait.until(ExpectedConditions.visibilityOf(emailField));
        emailField.sendKeys(email);
        return this;
    }
    public FormInputsPage inputName(String name){
        wait.until(ExpectedConditions.visibilityOf(nameField));
        nameField.sendKeys(name);
        return this;
    }
    public FormInputsPage selectGender(String gender){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(genderSelectorId)));
        assertThat(gender.equals("Мужской") || gender.equals("Женский")).isTrue();
        Select genderDropDown = new Select(genderSelector);
        genderDropDown.selectByVisibleText(gender);
        return this;
    }
    public FormInputsPage selectCheckboxes(int checkBoxes){
        wait.until(ExpectedConditions.visibilityOf(firstCheckBox));
        switch (checkBoxes) {
            case (1) -> {
                firstCheckBox.click();
                resultCheckBox.add("1.1");
            }
            case (2) -> {
                secondCheckBox.click();
                resultCheckBox.add("1.2");
            }
            case (12) -> { // 12 -> 1 и 2
                firstCheckBox.click();
                resultCheckBox.add("1.1");
                secondCheckBox.click();
                resultCheckBox.add("1.2");
            }
            default -> {
            }
        }
        return this;
    }

    public FormInputsPage selectRadioVariant(int radioVariant){
        wait.until(ExpectedConditions.visibilityOf(radioFirstVariant));
        switch (radioVariant) {
            case (1) -> {
                radioFirstVariant.click();
                resultRadioButton = "2.1";
            }
            case (2) -> {
                radioSecondVariant.click();
                resultRadioButton = "2.2";
            }
            case (3) -> {
                radioThirdVariant.click();
                resultRadioButton = "2.3";
            }
            default -> {
            }
        }
        return this;
    }
    public void sendForm(){
        wait.until(ExpectedConditions.visibilityOf(sendFormButton));
        sendFormButton.click();
    }
    public void closeAlertWindow(){
        wait.until(ExpectedConditions.visibilityOf(closeAlertWindowX));
        closeAlertWindowX.click();
        wait.until(ExpectedConditions.invisibilityOf(closeAlertWindowX));
    }
    // Очищаю resultCheckbox и resultRadioButton, чтобы в следующих тестах они не были заполнены значениями
    public void clearTempVariables(){
        resultCheckBox.clear();
        resultRadioButton = "";
    }
    // Метод, который проверяет, есть ли значение в определенном столбце в последней строке таблицы
    public boolean checkIfColumnInTableContains(String columnName, String value){
        // Запоминаем все заголовки столбцов в таблице
        List<WebElement> headers = dataTable.findElements(By.tagName("th"));

        // Определяем индекс столбца по его заголовку
        int columnIndex = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        // Если столбец не найден, бросаем исключение, поскольку это не дефект сайта, а неверный аргумент в тесте
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Invalid column name: " + columnName);
        }

        // Находим последнюю строку в таблице
        List<WebElement> rows = dataTable.findElements(By.tagName("tr"));
        WebElement lastRow = rows.get(rows.size() - 1); // индекс последней строки равен размеру списка минус 1

        // Находим все ячейки в последней строке
        List<WebElement> cells = lastRow.findElements(By.tagName("td"));

        // Проверяем, есть ли искомое значение в нужном столбце
        if (cells.get(columnIndex).getText().equals(value)) {
            return true;
        } else {
            return false;
        }
    }
}

