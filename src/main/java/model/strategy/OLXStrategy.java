package model.strategy;

import jnr.ffi.annotations.In;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vo.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class OLXStrategy implements Strategy, Callable<List<Vacancy>> {
    private static final String URL_FORMAT = "https://www.olx.ua/uk/rabota/q-%s/?page=%d";
    private final String query;
    public boolean canRunning = true;

    public OLXStrategy(String query) {
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
        int max = 0;
        try {
            while (canRunning) {
                Document document = getDocument(page);
                Elements elements = document.select("[data-cy=\"l-card\"]");
                if (document.location().contains("page")) {
                    max = Integer.parseInt(document.location().split("page=")[1]);
                    if (max < page) return list;
                }
                if (elements.isEmpty()) break;
                for (Element element : elements) {
                    Vacancy vacancy = new Vacancy();
                    String title = element.select(".css-mr8wpq a").text();
                    String city = element.select(".css-d5w927").text();
                    String salary = element.select(".css-1jnbm5x").text();
                    String date = element.select(".css-l3c9zc").text();
                    String url = element.select(".css-mr8wpq a").attr("href");
                    salary = salary.isEmpty() ? "(0) ЗП не указана" : salary;
                    vacancy.setSalary(salary);
                    vacancy.setCity(city);
                    vacancy.setCompanyName("Пользователь OLX.ua");
                    vacancy.setSiteName("olx.ua");
                    vacancy.setTitle(title);
                    vacancy.setUrl("https://olx.ua" + url);
                    vacancy.setDate(date);
                    if (vacancy.getTitle().isEmpty()) return list;
                    if (!list.contains(vacancy)) list.add(vacancy);
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