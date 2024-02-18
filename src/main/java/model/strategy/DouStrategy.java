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

public class DouStrategy implements Strategy, Callable<List<Vacancy>> {
    private static final String URL_FORMAT = "https://jobs.dou.ua/vacancies/?category=%s";
    private final String query;

    public DouStrategy(String query) {
        this.query = query;
    }

    @Override
    public List<Vacancy> call() {
        return getVacancies();
    }

    @Override
    public List<Vacancy> getVacancies() {
        ArrayList<Vacancy> list = new ArrayList<Vacancy>();
        try {
            Document document = getDocument();
            Elements elements = document.select(".l-vacancy");
            for (Element element : elements) {
                Vacancy vacancy = new Vacancy();
                String url = element.select("a.vt").attr("href");
                String title = element.select("div.title").text();
                String companyName = element.select("a.company").text();
                String city = element.select(".cities").text();
                String salary = element.select(".salary").text();
                String date = element.select(".date").text();
                vacancy.setSalary(!salary.isEmpty() ? salary : "(0) ЗП не указана");
                vacancy.setCity(city);
                vacancy.setCompanyName(companyName);
                vacancy.setSiteName("jobs.dou.ua");
                vacancy.setTitle(title);
                vacancy.setUrl(url);
                vacancy.setDate(date);
                list.add(vacancy);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    protected Document getDocument() throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, query))
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                .referrer("https://google.com/").get();
    }

    @Override
    public void stop() {
        Thread.currentThread().interrupt();
    }
}
