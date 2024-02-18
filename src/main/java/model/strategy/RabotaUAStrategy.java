package model.strategy;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import vo.Vacancy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RabotaUAStrategy implements Strategy, Callable<List<Vacancy>> {
    private static final String URL_FORMAT = "https://robota.ua/ru/zapros/%s/ukraine?page=%d";
    private final String query;
    public boolean canRunning = true;
    private WebDriver driver;

    public RabotaUAStrategy(String query) {
        this.query = query;
    }

    @Override
    public List<Vacancy> call() {
        return getVacancies();
    }

    @Override
    public List<Vacancy> getVacancies() {
        ArrayList<Vacancy> list = new ArrayList<Vacancy>();
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);

        int page = 1;
        try {
            while (canRunning) {
                driver.get(String.format(URL_FORMAT, query, page));
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("card")));

                while (!(boolean) js.executeScript("return (window.innerHeight + window.scrollY) >= document.body.scrollHeight;"))
                    js.executeScript("window.scrollBy(0, 150);");

                List<WebElement> cardElements = driver.findElements(By.className("card"));
                for (WebElement cardElement : cardElements)
                    parseAndAdd(list, cardElement);
                page++;
                if (!canRunning) {
                    driver.close();
                }
            }
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            if (driver != null) driver.close();
        }
        return list;
    }

    private void parseAndAdd(ArrayList<Vacancy> list, WebElement cardElement) {
        Vacancy vacancy = new Vacancy();
        vacancy.setUrl(cardElement.getAttribute("href"));
        vacancy.setTitle(cardElement.findElement(By.tagName("h2")).getText());
        List<WebElement> santamb10 = cardElement.findElements(By.cssSelector("div.santa-flex div.santa-mb-10:not(h2):not(.ng-star-inserted)"));
        if (santamb10.size()==2){
            vacancy.setSalary(santamb10.get(0).getText());
            vacancy.setCity(santamb10.get(1).getText());
        } else {
            vacancy.setSalary("(0) ЗП не указана");
            vacancy.setCity(santamb10.getFirst().getText());
        }
        vacancy.setCompanyName(cardElement.findElement(By.className("santa-mr-20")).getText());
        vacancy.setDate(cardElement.findElement(By.cssSelector(".santa-typo-secondary.santa-text-black-500")).getText());
        vacancy.setSiteName("robota.ua");
        if (vacancy.getCity().contains(vacancy.getCompanyName())) vacancy.setCity(vacancy.getCity().replaceAll(vacancy.getCompanyName(), ""));
        if (vacancy.getSalary().isEmpty()) vacancy.setSalary("(0) ЗП не указана");
        list.add(vacancy);
    }

    @Override
    public void stop() {
        canRunning = false;
        try {
            driver.close();
            Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
        } catch (Exception e) {
            System.out.println("error");
        }
    }

}
