package model.strategy;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vo.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class WorkUAStrategy implements Strategy, Callable<List<Vacancy>> {
    private static final String URL_FORMAT = "https://www.work.ua/jobs-%s/?page=%d";
    private final String query;
    public boolean canRunning = true;

    public WorkUAStrategy(String query) {
        this.query = query;
    }

    @Override
    public List<Vacancy> call() {
        return getVacancies();
    }

    @Override
    public List<Vacancy> getVacancies() {
        ArrayList<Vacancy> list = new ArrayList<Vacancy>();
        int page = 1;
        try {
            while (canRunning) {
                Document document = getDocument(page);
                Elements elements = document.select("div.card.card-hover.card-search");
                if (elements.isEmpty()) break;
                for (Element element : elements) {
                    Vacancy vacancy = new Vacancy();
                    String url = element.select("h2.cut-top.cut-bottom a").attr("href");
                    String title = element.select("h2.cut-top.cut-bottom a").text();
                    String companyName = element.select(".add-top-xs > span > span.strong-600").text();
                    String city = element.select(".add-top-xs > span:not(.add-right-xs)").text();
                    String salary = element.select("div:not(.add-top-xs) > span.strong-600").text();
                    String date = element.select(".text-default-7.add-top").text();
                    city = city.contains(companyName) ? city.replaceAll(companyName, "") : city;
                    if (salary.trim().isEmpty()) {
                        salary = "(0) ЗП не указана";
                    } else if(salary.contains("\u2013")) {
                        salary = salary.split("\u2013")[1];
                    }
                    vacancy.setSalary(salary);
                    vacancy.setCity(city);
                    vacancy.setCompanyName(companyName);
                    vacancy.setSiteName("work.ua");
                    vacancy.setTitle(title);
                    vacancy.setUrl("https://www.work.ua" + url);
                    vacancy.setDate(date);
                    list.add(vacancy);
                }
                page++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    protected Document getDocument(int page) throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, query, page))
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                .referrer("https://google.com/").get();
    }

    @Override
    public void stop() {
        canRunning = false;
    }
}
