package service;

import model.Ga;

public interface GiaVe {
    double GIA_CO_BAN = 8000;
    double GIA_MOI_GA = 1000;

    double tinhGiaVe(Ga gaDi, Ga gaDen);
}
