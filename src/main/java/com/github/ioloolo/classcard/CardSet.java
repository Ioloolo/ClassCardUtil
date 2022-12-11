package com.github.ioloolo.classcard;

import com.github.ioloolo.util.Driver;
import com.github.ioloolo.Main;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public final class CardSet {
    private static final Driver driver = Main.driver;
    public static String classSetCode;
    private final String cardSetCode;
    public final String name;

    public CardSet(String cardSetCode) {
        this.cardSetCode = cardSetCode;
        this.name = getName();
    }

    private String getName() {
        driver.goIfNotEqualPage("https://www.classcard.net/set/" + cardSetCode + "/" + classSetCode);

        return driver.driver().findElement(By.xpath("/html/body/div[1]/div[2]/div/div[1]/div[1]/div[1]")).getText();
    }

    public List<StudyType> getProcessList() {
        driver.goIfNotEqualPage("https://www.classcard.net/set/" + cardSetCode + "/" + classSetCode);

        return new ArrayList<>() {{
            new ArrayList<WebElement>() {{
                add(driver.driver().findElement(By.xpath("/html/body/div[2]/div/div[2]/div[2]")));
                add(driver.driver().findElement(By.xpath("/html/body/div[2]/div/div[2]/div[1]")));
            }}
                    .stream()
                    .map(webElement -> webElement.findElements(By.xpath("./child::*")))
                    .forEach(webElements -> {
                        webElements
                                .stream()
                                .map(webElement -> webElement.findElements(By.xpath("./child::*")))
                                .map(webElements1 -> webElements1.get(0))
                                .map(webElement -> webElement.findElements(By.xpath("./child::*")))
                                .forEach(webElements1 -> {
                                            StudyType type = StudyType.from(webElements1.get(0).getText());

                                            if (type != null) {
                                                String processString;

                                                if ((processString = webElements1.get(1).getAttribute("data-rate")) != null)
                                                    type.setProcess(Integer.parseInt(processString));
                                                else {
                                                    try {
                                                        processString = webElements1.get(1).findElements(By.xpath("./child::*")).get(0).getText();

                                                        if (processString != null)
                                                            type.setProcess(Integer.parseInt(processString.split("점")[0]));
                                                        else
                                                            type.setProcess(0);
                                                    } catch (IndexOutOfBoundsException e) {
                                                        type.setProcess(0);
                                                    }
                                                }

                                                add(type);
                                            }
                                        }
                                );
                    });
        }};
    }

    public void doMemorization() {
        driver.goIfNotEqualPage("https://www.classcard.net/Memorize/" + cardSetCode + "/6000/" + classSetCode);

        driver.click("#wrapper-learn > div.start-opt-body > div > div > div > div.m-t > a");
        driver.sleep(2);

        List<String> dataIndex = driver.driver()
                .findElement(By.className("study-body"))
                .findElements(By.xpath("./child::*"))
                .stream()
                .map(webElement -> webElement.getAttribute("data-idx"))
                .toList();

        driver.sleep(0.1);

        MultipartBody field = Unirest.post("https://www.classcard.net/ViewSetAsync/learnAll")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("set_idx_2", cardSetCode)
                .field("activity", "1")
                .field("last_section", "1")
                .field("last_round", "1")
                .field("view_cnt", dataIndex.size())
                .field("user_idx", Main.userIndex)
                .field("class_idx", classSetCode);
        for (String index : dataIndex)
            field = field.field("card_idx[]", index);
        for (int j = 0; j < dataIndex.size(); j++)
            field = field.field("score[]", "1");

        try {
            JSONObject result = field.asJson().getBody().getObject();

            if (!result.getString("result").equals("ok"))
                throw new Exception(result.getString("msg")+", "+dataIndex);

            Unirest.post("https://www.classcard.net/ViewSetAsync/resetAllLog")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("set_idx", cardSetCode)
                    .field("activity", 1)
                    .field("user_idx", Main.userIndex)
                    .field("view_cnt", dataIndex.size())
                    .field("class_idx", CardSet.classSetCode)
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doRecall() {
        driver.goIfNotEqualPage("https://www.classcard.net/Recall/" + cardSetCode + "/6000/" + classSetCode);

        driver.click("#wrapper-learn > div.start-opt-body > div > div > div > div.m-t > a");
        driver.sleep(2);

        List<String> dataIndex = driver.driver()
                .findElement(By.className("study-body"))
                .findElements(By.xpath("./child::*"))
                .stream()
                .map(webElement -> webElement.getAttribute("data-idx"))
                .toList();

        driver.sleep(0.1);

        MultipartBody field = Unirest.post("https://www.classcard.net/ViewSetAsync/learnAll")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("set_idx_2", cardSetCode)
                .field("activity", "2")
                .field("last_section", "1")
                .field("last_round", "1")
                .field("view_cnt", dataIndex.size())
                .field("user_idx", Main.userIndex)
                .field("class_idx", classSetCode);
        for (String index : dataIndex)
            field = field.field("card_idx[]", index);
        for (int j = 0; j < dataIndex.size(); j++)
            field = field.field("score[]", "1");

        try {
            JSONObject result = field.asJson().getBody().getObject();

            if (!result.getString("result").equals("ok"))
                throw new Exception(result.getString("msg")+", "+dataIndex);

            Unirest.post("https://www.classcard.net/ViewSetAsync/resetAllLog")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("set_idx", cardSetCode)
                    .field("activity", 2)
                    .field("user_idx", Main.userIndex)
                    .field("view_cnt", dataIndex.size())
                    .field("class_idx", CardSet.classSetCode)
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doTest() {
        String script = "return study_data.map(idx => ({kor: idx.front, eng: idx.back}))";

        driver.goIfNotEqualPage("https://www.classcard.net/set/" + cardSetCode + "/" + classSetCode);

        JavascriptExecutor executor = (JavascriptExecutor) driver.driver();
        List<Map<String, String>> answers = (List<Map<String, String>>) executor.executeScript(script);

        driver.goIfNotEqualPage("https://www.classcard.net/ClassTest/"+classSetCode+"/"+cardSetCode);

        String quizStartPath = "#wrapper-test > div > div.quiz-start-div";
        String startButtonPath = quizStartPath + " > div.layer.retry-layer.box > div.m-t-xl > a";
        String testStartButtonPath = quizStartPath + " > div.layer.prepare-layer.box.bg-gray.text-white > div.text-center.m-t-md > a";

        driver.click(startButtonPath);
        driver.sleep(0.5);

        driver.click(testStartButtonPath);
        driver.sleep(1.5);

        AtomicReference<String> lastQuestionNumber = new AtomicReference<>("");

        try {
            while (true) {
                if (!driver.driver().findElement(By.className("end-layer")).getAttribute("class").contains("hidden"))
                    break;

                Optional<WebElement> showing = driver.driver()
                        .findElements(By.className("showing"))
                        .stream()
                        .findFirst();

                if (showing.isEmpty()) break;

                WebElement webElement = showing.get();

                String questNumber = webElement.findElement(By.xpath(".//input[@name='test_question[]']")).getAttribute("value");

                if (questNumber != null && !questNumber.equals(lastQuestionNumber.get())) {
                    lastQuestionNumber.set(questNumber);

                    String quest = webElement.findElement(By.xpath("./div/div[1]/div[2]/div/div/div")).getAttribute("innerHTML");

                    String answer = null;
                    Optional<Map<String, String>> first = answers
                            .stream()
                            .filter(o -> o.get("kor").equals(quest) || o.get("eng").equals(quest))
                            .findFirst();
                    if (first.isPresent()) {
                        Map<String, String> answersStream = first.get();

                        if (answersStream.get("kor").equals(quest)) answer = answersStream.get("eng");
                        else if (answersStream.get("eng").equals(quest)) answer = answersStream.get("kor");

                        String finalAnswer = answer;
                        webElement
                                .findElements(By.xpath("./div/div[2]/div/div[1]/child::*"))
                                .stream()
                                .filter(webElement1 -> webElement1.findElement(By.xpath(".//div[contains(@class, 'cc-table')]/div")).getAttribute("innerHTML").equals(finalAnswer))
                                .forEach(webElement1 -> {
                                    String s = webElement1.findElement(By.xpath("./input")).getAttribute("id").split("_")[2];

                                    driver.sleep(0.3);
                                    driver.driver().findElement(By.tagName("body")).sendKeys(Keys.SPACE);
                                    driver.sleep(0.5);
                                    driver.driver().findElement(By.tagName("body")).sendKeys(s);
                                    driver.sleep(0.5);
                                    driver.driver().findElement(By.tagName("body")).sendKeys(Keys.SPACE);
                                });
                    }
                }
            }
        } catch (StaleElementReferenceException ignored) {}
    }

    public void doMatch() {
        String script = "return study_data.map(idx => ({kor: idx.front, eng: idx.back}))";

        driver.goIfNotEqualPage("https://www.classcard.net/set/" + cardSetCode + "/" + classSetCode);

        JavascriptExecutor executor = (JavascriptExecutor) driver.driver();
        List<Map<String, String>> answers = (List<Map<String, String>>) executor.executeScript(script);

        driver.goIfNotEqualPage("https://www.classcard.net/Match/"+cardSetCode+"?c="+classSetCode+"&s=1");

        String startButtonPath = "#wrapper-learn > div.vertical-mid.center.fill-parent > div.start-opt-body > div > div > div.start-opt-box > div:nth-child(4) > a";

        driver.click(startButtonPath);
        driver.sleep(2.5);

        while (true) {
            try {
                for (int i = 0; i < 4; i++) {
                    Optional<WebElement> card = driver.driver()
                            .findElements(By.xpath("//*[@id=\"left_card_" + i + "\"]/div/div[1]/div/div"))
                            .stream()
                            .findFirst();

                    if (card.isEmpty()) continue;

                    WebElement webElement = card.get();

                    String problem = webElement.getAttribute("innerHTML");

                    String answer = null;
                    Optional<Map<String, String>> first = answers
                            .stream()
                            .filter(o -> o.get("kor").equals(problem) || o.get("eng").equals(problem))
                            .findFirst();

                    if (first.isPresent()) {
                        Map<String, String> answersStream = first.get();

                        if (answersStream.get("kor").equals(problem)) answer = answersStream.get("eng");
                        else if (answersStream.get("eng").equals(problem)) answer = answersStream.get("kor");

                        List<String> a = new ArrayList<>() {{
                            for (int j = 0; j < 4; j++) {
                                WebElement right = driver.driver().findElement(By.xpath("//*[@id=\"right_card_" + j + "\"]/div/div/div/div"));

                                add(right.getAttribute("innerHTML"));
                            }
                        }};

                        if (a.contains(answer)) {
                            System.out.println(problem + " : " + answer);
                        }
                    }
                }

                System.out.println();
                driver.sleep(1);
            } catch (Exception e) {}
        }
    }

    public boolean isNeedStudy(List<StudyType> list, StudyType type) {
        Optional<StudyType> studyType = list.stream()
                .filter(o1 -> o1.getName().equals(type.getName()))
                .findFirst();

        if (studyType.isEmpty()) return true;

        return studyType.get().getProcess() < 100;
    }
}
