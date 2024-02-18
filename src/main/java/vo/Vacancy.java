package vo;

import java.util.Objects;

public class Vacancy {
    private String title;
    private String salary;
    private String city;
    private String companyName;
    private String siteName;
    private String url;
    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vacancy vacancy)) return false;
        return Objects.equals(getTitle(), vacancy.getTitle()) && Objects.equals(getSalary(), vacancy.getSalary()) && Objects.equals(getCity(), vacancy.getCity()) && Objects.equals(getCompanyName(), vacancy.getCompanyName()) && Objects.equals(getSiteName(), vacancy.getSiteName()) && Objects.equals(getUrl(), vacancy.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getSalary(), getCity(), getCompanyName(), getSiteName(), getUrl());
    }

    @Override
    public String toString() {
        title = title.replace(",", "").trim();
        salary = salary.replace(",", "").trim();
        city = city.replace(",", "").trim();
        companyName = companyName.replace(",", "").trim();
        date = date.replace(",", "").trim();
        return String.format("<a href=\"%s\">%s</a>, <b>%s</b>, %s, %s, %s, %s", url, title, salary, city, companyName, date, siteName);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
