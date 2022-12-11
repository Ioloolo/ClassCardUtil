package com.github.ioloolo.classcard;

import com.github.ioloolo.util.Account;
import com.github.ioloolo.util.Driver;
import com.github.ioloolo.Main;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassCard {
    private static final Driver driver = Main.driver;

    public static String login(Account account) {
        driver.goIfNotEqualPage("https://www.classcard.net/Login");

        driver.sendKeys("#login_id", account.getId());
        driver.sendKeys("#login_pwd", account.getPw());
        driver.click("#loginForm > div.checkbox.primary.text-primary.text-center.m-t-md > button");

        return ClassCard.getUserIndex();
    }

    public static String getUserIndex() {
        driver.sleep(1);

        String script = """
                return user_idx
                """;

        return String.valueOf(((JavascriptExecutor) driver.driver()).executeScript(script));
    }

    public static Map<String, String> fetchClassSets() {
        return new HashMap<>() {{
            List<WebElement> classSetsStream = driver.driver().findElements(By.className("left-class-items"));

            ArrayList<String> index = new ArrayList<>() {{
                classSetsStream
                        .stream()
                        .map(webElement -> webElement.findElements(By.xpath("./child::*")))
                        .map(webElements -> webElements.get(1))
                        .map(webElement -> webElement.findElements(By.xpath("./child::*")))
                        .map(webElements -> webElements.get(0))
                        .map(WebElement::getText)
                        .forEach(name -> {
                            add(name);
                            put(name, "");
                        });
            }};

            AtomicInteger i = new AtomicInteger();

            classSetsStream
                    .stream()
                    .map(webElement -> webElement.getAttribute("href"))
                    .map(href -> href.split("/ClassMain/")[1])
                    .map(String::trim)
                    .forEach(text -> replace(index.get(i.getAndIncrement()), text));
        }};
    }

    public static Map<String, String> fetchCardSets(String classSetCode) {
        driver.goIfNotEqualPage("https://www.classcard.net/ClassMain/" + classSetCode);

        return new HashMap<>() {{
            driver.driver()
                    .findElements(By.className("set-name-a"))
                    .forEach(webElement -> {
                        WebElement element = webElement
                                .findElement(By.xpath(".."))
                                .findElement(By.xpath(".."))
                                .findElement(By.xpath(".."));

                        boolean isWordSet1 = element
                                .findElements(By.xpath("./child::*")).get(0)
                                .findElements(By.xpath("./child::*")).get(0)
                                .getAttribute("class")
                                .contains("word");

                        boolean isWordSet2 = element
                                .findElements(By.xpath("./child::*")).get(1)
                                .findElements(By.xpath("./child::*")).get(0)
                                .getAttribute("class")
                                .contains("word");

                        if (isWordSet1 || isWordSet2)
                            put(webElement.getText().substring(0, webElement.getText().length()-5), webElement.getAttribute("data-idx"));
                    });
        }};
    }
}
