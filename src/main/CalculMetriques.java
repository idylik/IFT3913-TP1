package main;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static main.Type.*;

/**
 * Calcule les métriques pour les classes et les paquets.
 */
public class CalculMetriques {

    private static boolean estDansCommentaire;

    /**
     * Calcule le nombre de lignes non vides, LOC et CLOC dans un fichier du code.
     *
     * @param cheminFichierAbsolu chemin absolu du fichier
     * @return HashMap avec le nombre de lignes non vides, LOC et CLOC dans un fichier de code
     */
    public static HashMap<Type, Object> compterLignes(String cheminFichierAbsolu) {

        HashMap<Type, Object> nbLignes = new HashMap<>();
        int classeCLOC = 0;
        int classeLOC = 0;
        int nbLignesNonVides = 0;
        estDansCommentaire = false;
        String codeSansComm = "";
        Scanner fichier;

        try {
            fichier = new Scanner(new File(cheminFichierAbsolu));
            while (fichier.hasNext()) {
                String ligne = fichier.nextLine();
                if (!ligne.isEmpty()) {
                    ligne = ligne.trim();
                    nbLignesNonVides++;
                    if (estCode(ligne)) {
                        classeLOC++;
                        codeSansComm += " " + ligne;
                    }
                    if (estCommentaire(ligne)) classeCLOC++;
                }
            }
            fichier.close();
        } catch (FileNotFoundException ex) {
        }
        nbLignes.put(LIGNES_NON_VIDES, nbLignesNonVides);
        nbLignes.put(CLASSE_LOC, classeLOC);
        nbLignes.put(CLASSE_CLOC, classeCLOC);
        long wmc = calculerWMC(codeSansComm);
        nbLignes.put(WMC, wmc);
        return nbLignes;
    }

    /**
     * Vérifie si la ligne passée en paramètre est du code.
     *
     * @param ligne ligne à lire
     * @return true si la ligne est du code, false si la ligne est un commentaire
     */
    private static boolean estCode(String ligne) {
        return !(estDansCommentaire || ligne.startsWith("//") || ligne.startsWith("/*"));
    }

    /**
     * Vérifie si la ligne passée en paramètre est un commentaire.
     *
     * @param ligne ligne à lire
     * @return true si la ligne appartient à un commentaire, false sinon
     */
    public static boolean estCommentaire(String ligne) {
        if ((ligne.contains("//") && !estDansCommentaire)
                || (ligne.contains("/*") && ligne.contains("*/"))) {
            return true;
        }
        if (ligne.contains("/*") && !ligne.contains("*/")) {
            estDansCommentaire = true;
            return true;
        }
        if (estDansCommentaire && ligne.contains("*/")) {
            estDansCommentaire = false;
            return true;
        }
        return estDansCommentaire && !(ligne.contains("/*") && ligne.contains("*/"));
    }

    /**
     * Calcule les valeurs des métriques du paquet et les met dans un HashMap.
     *
     * @param cheminDossier chemin du dossier
     * @param calculsDossier HashMap qui stocke les valeurs des métriques du paquet
     * @param extensionFichier extension des fichiers à analyser
     * @param profondeur niveau de profondeur dans le dossier (initialement 0)
     */
    public static void calculerPaquet(String cheminDossier, HashMap<Type, Object> calculsDossier,
                                      String extensionFichier, int profondeur) {

        File dossier = new File(cheminDossier);
        File[] listeEntites = dossier.listFiles();

        for (File entite : listeEntites) {
            if (entite.isFile()) {
                if (entite.getName().endsWith(extensionFichier)) {
                    // Accède aux metrique d'une classe.
                    HashMap compte = compterLignes(entite.getAbsolutePath());
                    int loc = (int) compte.get(CLASSE_LOC);
                    int cLoc = (int) compte.get(CLASSE_CLOC);
                    long wmc = (long) compte.get(WMC);
                    int paquetLoc = (int) calculsDossier.get(PAQUET_LOC);
                    int paquetCLoc = (int) calculsDossier.get(PAQUET_CLOC);
                    long wcp = ((Number) calculsDossier.get(WCP)).longValue();

                    // Additionne seulement les classes qui sont au premier niveau de profondeur du paquet.
                    if (profondeur == 0) {
                        calculsDossier.put(PAQUET_LOC, paquetLoc + loc);
                        calculsDossier.put(PAQUET_CLOC, paquetCLoc + cLoc);
                    }
                    calculsDossier.put(WCP, wcp + wmc);
                }
            } else {
                // Si l'entité est un sous-paquet, appel récursif de la méthode.
                calculerPaquet(entite.getAbsoluteFile().toString(), calculsDossier, extensionFichier,
                        profondeur + 1);
            }
        }
    }

    /**
     * Calcule les valeurs des métriques classe_DC et classe_BC et met toutes les métrique de la classe dans un HashMap.
     *
     * @param calculsClasse valeurs des métriques de la classe
     * @return HashMap avec valeurs des métriques de la classe
     */
    public static HashMap calculerClasse(HashMap calculsClasse) {

        String cheminFichier = calculsClasse.get(CHEMIN).toString() + calculsClasse.get(CLASSE).toString();
        HashMap compte = compterLignes(cheminFichier);
        int loc = (int) compte.get(CLASSE_LOC);
        int cLoc = (int) compte.get(CLASSE_CLOC);
        float dc = 0;
        long wmc = (long) compte.get(WMC);
        float bc = 0;

        calculsClasse.put(CLASSE_LOC, loc);
        calculsClasse.put(CLASSE_CLOC, cLoc);
        calculsClasse.put(WMC, wmc);

        if (loc > 0 && cLoc > 0) {
            dc = (float) cLoc / (float) loc;
            calculsClasse.put(CLASSE_DC, dc);
        }

        if (dc > 0 && wmc > 0) {
            bc = (float) dc / (float) wmc;
            calculsClasse.put(CLASSE_BC, bc);
        }
        return calculsClasse;
    }

    /**
     * Calcule la métrique WMC, la somme pondérée des complexités cyclomatiques de McCabe.
     *
     * @param codeSansComm code de la classe sans les commentaires
     * @return compteur de complexité cyclomatique de McCabe de la classe donnée
     */
    public static long calculerWMC(String codeSansComm) {

        long compteur = 0;

        // Regex qui match une déclaration de méthode.
        // Inspiré de: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration/847507#847507
        // Idéalement stocker dans un fichier de configuration externe
        Pattern methPattern = Pattern.compile("\\s(?:(?:public|protected|private)?\\s+)?" +
                "(?:(static|final|native|synchronized|abstract|threadsafe|transient|" +
                "(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|" +
                "(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))\\s+){0,}(?!return)(?!new)\\b([\\w.]+)\\b" +
                "(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|" +
                "(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})\\s+\\b\\w+\\b\\s*\\(\\s*" +
                "(?:\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|" +
                "(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})(\\.\\.\\.)?\\s+(\\w+)\\b" +
                "(?![>\\[])\\s*(?:,\\s+\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|" +
                "(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})(\\.\\.\\.)?\\s+(\\w+)\\b" +
                "(?![>\\[])\\s*){0,})?\\s*\\)(?:\\s*throws [\\w.]+(\\s*,\\s*[\\w.]+))?\\s*(?:\\{|;)[ \\t]*");

        Matcher methMatcher = methPattern.matcher(codeSansComm);

        // Regex qui match les prédicats avec un degré de sorti supérieur à 1.
        Pattern predicats = Pattern.compile("\\s+if\\s*\\(|\\s+for\\s*\\(|\\s+while\\s*\\(|\\s+case\\s+");
        Matcher predicatsMatcher = predicats.matcher(codeSansComm);
        compteur += methMatcher.results().count();
        compteur += predicatsMatcher.results().count();
        return compteur;
    }
}