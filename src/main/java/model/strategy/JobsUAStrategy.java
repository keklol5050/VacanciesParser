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

public class JobsUAStrategy implements Strategy, Callable<List<Vacancy>> {
    private static final String URL_FORMAT = "https://jobs.ua/vacancy/rabota-%s/page-%d";
    private final String query;
    public boolean canRunning = true;

    public JobsUAStrategy(String query) {
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
                Elements elements = document.select(".b-vacancy__item");
                if (elements.isEmpty()) break;
                for (Element element : elements) {
                    Vacancy vacancy = new Vacancy();
                    String title = element.select("h3").text();
                    String city = element.select(".b-vacancy__tech__item a").text();
                    String salary = element.select(".b-vacancy__top__pay").text();
                    String url = element.select("h3 a").attr("href");
                    salary = salary.isEmpty() ? "(0) ЗП не указана" : salary;
                    String company = element.select(".b-vacancy__tech__item span.link__hidden").text();
                    vacancy.setSalary(salary);
                    vacancy.setCity(city);
                    vacancy.setCompanyName(company);
                    vacancy.setSiteName("jobs.ua");
                    vacancy.setTitle(title);
                    vacancy.setUrl("https://jobs.ua" + url);
                    vacancy.setDate("Не указано");
                    if (!vacancy.getTitle().isEmpty())list.add(vacancy);
                    System.out.println(vacancy);
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