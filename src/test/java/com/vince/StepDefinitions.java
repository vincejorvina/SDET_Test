package com.vince;

import io.cucumber.java.en.*;
import io.cucumber.java.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.List;

public class StepDefinitions {    
    private WebDriver driver;
    private String searchId;
    WebElement targetRow = null;
    java.time.Duration waitTime = Duration.ofSeconds(5);

    @Given("the user is on the webpage demo.aspnetawesome.com")
    public void navigateToWebpage() {
        driver = new ChromeDriver();
        driver.get("https://demo.aspnetawesome.com/GridDemo");
    }

    @When("the user enters the Id {string}")
    public void enterId(String id) {
        searchId = id.isEmpty() ? null : id;
    }

    @Then("the program should find the row with the Id")
    public void findRowById() {
        if (searchId == null) {
            return;
        }

        WebElement pageSizeDropdown = driver.findElement(By.id("Grid1PageSize-awed"));
        pageSizeDropdown.click();

        WebElement pageSizeOption = driver.findElement(By.xpath("//li[@data-val='100']"));
        pageSizeOption.click();
        
        // the paginated table controls have two different behaviors depending on page width
        WebElement divPager = driver.findElement(By.className("awe-pager")); 
        WebElement buttonNextPage = divPager.findElement(By.xpath("//button[@data-act='f']"));

        if (buttonNextPage != null) { // behavior for when the page shows the first/previous/next/last buttons
            do {
                if (searchRows(driver, searchId) == true) { 
                    return;
                } 
                buttonNextPage = divPager.findElement(By.xpath("//button[@data-act='f']"));
                if (buttonNextPage.isEnabled()) {
                    buttonNextPage.click();
                }
            } while (buttonNextPage.isEnabled());
        }
        else { // behavior for when the page shows page number buttons and no next/previous buttons
            List<WebElement> pageButtons = driver.findElements(By.xpath("//div[@class='awe-pager']/button"));
            for (WebElement button : pageButtons) {
                button.click();
                if (searchRows(driver, searchId) == true) { 
                    return;
                } 
            }
        }
    }

    @Then("the program should display the values of the found row")
    public void returnValues() {
        if (targetRow != null) {
            List<WebElement> columns = driver.findElements(By.className("awe-col"));
            for (int i = 0; i < columns.size(); i++) {
                List<WebElement> targetRowColumns = targetRow.findElements(By.tagName("td"));
                System.out.println(columns.get(i).getText() + ": " + targetRowColumns.get(i).getText());
            }
        }  
    }

    @But("if the Id is blank, the program should display an error message indicating that the Id is blank")
    public void errorBlankId() {
        if (targetRow == null && searchId == null) {
            fail("Id cannot be blank");
        }
    }

    @But("if the row with the Id could not be found, the program should display an error message indicating the row was not found")
    public void errorRowNotFound() {
        if (targetRow == null) {
            fail("Row with Id '" + searchId + "' was not found");
        }
    }

    @After
    public void closeBrowser() {
        driver.quit();
    }

    private boolean searchRows(WebDriver d, String targetId) {
        WebDriverWait wait = new WebDriverWait(d, waitTime);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"Grid1\"]/div[3]/div[2]/div/table/tbody/tr")));

        List<WebElement> rows = d.findElements(By.xpath("//*[@id=\"Grid1\"]/div[3]/div[2]/div/table/tbody/tr"));
        for (WebElement row : rows) {
            String idValue = row.getAttribute("data-k");
            if (idValue.equals(searchId)) {
                targetRow = row;
                return true;
            }
        }

        return false;
    }
}
