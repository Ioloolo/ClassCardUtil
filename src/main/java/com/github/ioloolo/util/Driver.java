package com.github.ioloolo.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public record Driver(WebDriver driver) {
    public void goIfNotEqualPage(String url) {
        if (driver.getCurrentUrl().equals(url))
            return;

        driver.get(url);
        sleep(1);
    }

    public void sendKeys(String selector, String content) {
        List<WebElement> elements = driver.findElements(By.cssSelector(selector));

        if (elements.size() > 0)
            elements.get(0).sendKeys(content);
    }

    public void click(String selector) {
        List<WebElement> elements = driver.findElements(By.cssSelector(selector));

        if (elements.size() > 0)
            elements.get(0).click();
    }

    public String getText(String selector) {
        List<WebElement> elements = driver.findElements(By.cssSelector(selector));

        return elements.size() > 0 ? elements.get(0).getText() : null;
    }

    public void sleep(double seconds) {
        try {
            Thread.sleep((long) (1000 * seconds));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void exit() {
        driver.quit();
    }
}
