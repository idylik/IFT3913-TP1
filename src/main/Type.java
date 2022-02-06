package main;

public enum Type {
    CHEMIN("chemin"),
    CLASSE("classe"),
    PAQUET("paquet"),
    LOC("LOC"),
    CLOC("CLOC"),
    DC("DC"),
    CLASSE_LOC("classe_LOC"),
    CLASSE_CLOC("classe_CLOC"),
    CLASSE_DC("classe_DC"),
    PAQUET_LOC("paquet_LOC"),
    PAQUET_CLOC("paquet_CLOC"),
    PAQUET_DC("paquet_DC"),
    LIGNES_NON_VIDES("LIGNES_NON_VIDES");

    private String nom;

    Type(String nom) {
        this.nom = nom;
    }

    public String stringFrom(Type type) {
        if (type == CLASSE || type == PAQUET) {
            return type.nom + "_" + nom;
        }
        return nom;
    }

    public Type typeFrom(Type type) {
        if (type == CLASSE) {
            switch (this) {
                case LOC:
                    return CLASSE_LOC;
                case CLOC:
                    return CLASSE_CLOC;
                case DC:
                    return CLASSE_DC;
            }
        }
        if (type == PAQUET) {
            switch (this) {
                case LOC:
                    return PAQUET_LOC;
                case CLOC:
                    return PAQUET_CLOC;
                case DC:
                    return PAQUET_DC;
            }
        }
        return this;
    }

    public String getNom() {
        return nom;
    }
}
