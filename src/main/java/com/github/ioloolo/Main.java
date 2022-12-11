package com.github.ioloolo;

import com.github.ioloolo.classcard.CardSet;
import com.github.ioloolo.classcard.ClassCard;
import com.github.ioloolo.classcard.StudyType;
import com.github.ioloolo.util.Account;
import com.github.ioloolo.util.Driver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
public class Main {
    public static Driver driver;
    public static String userIndex;

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/Users/sondaehyeon/Desktop/classcard/src/main/resource/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(false);

        driver = new Driver(new ChromeDriver(options));

        try {
            userIndex = ClassCard.login(Account.YOUR_NAME);

            CardSet.classSetCode = "000000";
            Map<String, String> cardSets = ClassCard.fetchCardSets(CardSet.classSetCode);

            cardSets
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(o -> -Integer.parseInt(o.getValue())))
                    .forEach((entry) -> {
                        String name = entry.getKey();
                        String code = entry.getValue();

                        CardSet card = new CardSet(code);

                        System.out.printf("[학습 시작] %s\n", name);

                        List<StudyType> processList = card.getProcessList();
//                        if (card.isNeedStudy(processList, StudyType.MEMORIZATION))
//                            card.doMemorization();
//                        if (card.isNeedStudy(processList, StudyType.RECALL))
//                            card.doRecall();
//                        if (card.isNeedStudy(processList, StudyType.TEST))
//                            card.doTest();
//                        if (card.isNeedStudy(processList, StudyType.MATCH))
//                            card.doMatch();

                        System.out.printf("[학습 종료] %s\n\n", name);
                    });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            driver.exit();
        }
    }
}
