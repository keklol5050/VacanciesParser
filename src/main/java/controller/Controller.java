package controller;

import model.Model;
import model.Provider;

public class Controller {
    private final Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void parse() {
        model.parse();
    }

    public void updateModel() {
        model.setProviders(Main.providers.toArray(new Provider[0]));
    }


    public void sortDate() {
        model.getVacancies().sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
        model.update();
    }

    public void sortSalary() {
        model.getVacancies().sort((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()));
        model.update();
    }

    public void sortCity() {
        model.getVacancies().sort((e1, e2) -> e1.getCity().compareTo(e2.getCity()));
        model.update();
    }

    public void sortSiteName() {
        model.getVacancies().sort((e1, e2) -> e1.getSiteName().compareTo(e2.getSiteName()));
        model.update();
    }
}
