package main;

/**
 * Types des entités et des métriques utilisées dans le code.
 */
public enum Type {
    CHEMIN("chemin"),
    CLASSE("classe"),
    PAQUET("paquet"),
    LOC("LOC"),
    CLOC("CLOC"),
    DC("DC"),
    BC("BC"),
    CCM("CCM"), //Complexité Cyclomatique de McCabe
    CLASSE_LOC("classe_LOC"),
    CLASSE_CLOC("classe_CLOC"),
    CLASSE_DC("classe_DC"),
    WMC("WMC"),
    CLASSE_BC("classe_BC"),
    PAQUET_LOC("paquet_LOC"),
    PAQUET_CLOC("paquet_CLOC"),
    PAQUET_DC("paquet_DC"),
    WCP("WCP"),
    PAQUET_BC("paquet_BC"),
    LIGNES_NON_VIDES("LIGNES_NON_VIDES");

    private final String nom;

    /**
     * Constructeur du type.
     *
     * @param nom nom du type
     */
    Type(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le nom de l'entité ou de la métrique en chaîne de caractères.
     *
     * @return nom de l'entité ou de la métrique en chaîne de caractères
     */
    public String getNom() {
        return nom;
    }

    /**
     * Prend en paramètre le type d'entité (classe ou paquet) et retourne la métrique associée
     * en chaîne de caractères.
     *
     * @param type classe ou paquet
     * @return métrique de la classe ou du paquet en chaîne de caractères
     */
    public String stringFrom(Type type) {
        if (type == CLASSE || type == PAQUET) {
            return type.nom + "_" + nom;
        }
        return nom;
    }

    /**
     * Prend en paramètre le type d'entité (classe ou paquet) et retourne la métrique associée
     * sous forme de Type.
     *
     * @param type classe ou paquet
     * @return métrique de la classe ou du paquet sous forme de Type
     */
    public Type typeFrom(Type type) {
        if (type == CLASSE) {
            switch (this) {
                case LOC:
                    return CLASSE_LOC;
                case CLOC:
                    return CLASSE_CLOC;
                case DC:
                    return CLASSE_DC;
                case BC:
                    return CLASSE_BC;
                case CCM:
                    return WMC;
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
                case BC:
                    return PAQUET_BC;
                case CCM:
                    return WCP;
            }
        }
        return this;
    }
}