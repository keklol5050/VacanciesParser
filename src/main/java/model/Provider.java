package model;


import vo.Vacancy;

import java.util.List;
import java.util.concurrent.Callable;

public class Provider {
    private Callable<List<Vacancy>> strategy;

    public Provider(Callable<List<Vacancy>> strategy) {
        this.strategy = strategy;
    }

    public Callable<List<Vacancy>> getStrategy() {
        return strategy;
    }

    public void setStrategy(Callable<List<Vacancy>> strategy) {
        this.strategy = strategy;
    }
}
