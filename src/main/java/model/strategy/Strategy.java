package model.strategy;


import vo.Vacancy;

import java.util.List;

public interface Strategy {
    List<Vacancy> getVacancies();

    void stop();
}
