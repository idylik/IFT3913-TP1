/**
 * Le programme implémente les métriques qui permettent d'estimer la densité des commentaires par classe et
 * par paquet, ainsi que la somme pondérée des complexités cyclomatiques de McCabe. Il retourne deux fichiers CSV
 * avec les métriques par classe et par paquets.
 * Métriques :
 * - nombre de lignes de code d'une classe et d'un paquet
 * - nombre de lignes de commentaires d'une classe et d'un paquet
 * - densité de commentaires pour une classe et un paquet
 * - Weighted Methods per Class et Weighted Classes per Package
 *
 * @author Michel Adant C1176
 * @author Maryna Starastsenka 20166402
 */

package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.opencsv.CSVWriter;
import static main.Type.*;

/**
 * Classe principale qui délègue les opérations de calcul des métriques.
 */
public class Main {

    private final static String FICHIER_CONFIG = "tp1.properties";
    private static String EXTENSION_FICHIER = null;

    /**
     * Prend en paramètre le chemin du dossier avec le code à analyser et appelle les méthodes qui parcouren le dossier
     * et calculent les métriques.
     *
     * @param args chemin du dossier/paquet à analyser
     * @throws IOException si le chemin du dossier n'est pas passé en paramètre
     */
    public static void main(String[] args) throws IOException {

        File dossier;

        getConfiguration(FICHIER_CONFIG);
        if (args.length == 1) {
            if (Files.exists(Paths.get(args[0]))) {
                dossier = new File(args[0]);
            } else {
                System.out.println("Le chemin d'accès spécifié n'existe pas");
                System.exit(2);
                return;
            }
        } else {
            System.out.println("Le chemin d'accès du dossier n'est pas spécifié en argument");
            System.exit(1);
            return;
        }

        // Listes de tableaux des classes et des paquets du dossier initial
        ArrayList<String> listeClasses = new ArrayList<>();
        ArrayList<String> listeDossiers = new ArrayList<>();

        ParcoursFichiers.listerFichiers(dossier, EXTENSION_FICHIER, listeClasses, listeDossiers);

        // Tableaux de HashMap qui serviront aux calculs
        HashMap[] calculsClasses = initialiserListeCalculs(listeClasses, CLASSE);
        HashMap[] calculsPaquets = initialiserListeCalculs(listeDossiers, PAQUET);

        for (HashMap classe : calculsClasses) {
            classe = CalculMetriques.calculerClasse(classe);
            System.out.println(classe);
        }

        for (HashMap<Type, Object> paquet : calculsPaquets) {
            String cheminDossier = paquet.get(CHEMIN).toString() + paquet.get(PAQUET).toString();
            CalculMetriques.calculerPaquet(cheminDossier, paquet, EXTENSION_FICHIER, 0);
            int loc = (int) paquet.get(PAQUET_LOC);
            int cLoc = (int) paquet.get(PAQUET_CLOC);
            float dc = 0;
            long wcp = ((Number) paquet.get(WCP)).longValue();
            float bc = 0;

            if (loc > 0 && cLoc > 0) {
                dc = (float) cLoc / (float) loc;
                paquet.put(PAQUET_DC, dc);
            }
            if (dc > 0 && wcp > 0) {
                bc = dc / (float) wcp;
                paquet.put(PAQUET_BC, bc);
            }
            System.out.println(paquet);
        }
        creerFichierCsv(args[0], CLASSE, calculsClasses);
        creerFichierCsv(args[0], PAQUET, calculsPaquets);
    }

    /**
     * Initialise les listes de tableaux des classes et des paquets dépendemment du type d'éntité passé en paramètre.
     *
     * @param listeEntites liste de toutes les classes ou paquets
     * @param type         classe ou paquet
     * @return tableau de hashmaps stocke les valeurs des métriques
     */
    public static HashMap[] initialiserListeCalculs(ArrayList<String> listeEntites, Type type) {

        HashMap[] calculsEntites = new HashMap[listeEntites.size()];

        for (int i = 0; i < listeEntites.size(); i++) {
            calculsEntites[i] = new HashMap<Type, Object>();
            int indexSlash = listeEntites.get(i).lastIndexOf(File.separator);
            calculsEntites[i].put(CHEMIN, listeEntites.get(i).substring(0, indexSlash + 1));
            calculsEntites[i].put(type, listeEntites.get(i).substring(indexSlash + 1));
            calculsEntites[i].put(LOC.typeFrom(type), 0);
            calculsEntites[i].put(CLOC.typeFrom(type), 0);
            calculsEntites[i].put(DC.typeFrom(type), 0);
            calculsEntites[i].put(CCM.typeFrom(type), 0);
            calculsEntites[i].put(BC.typeFrom(type), 0);
        }
        return calculsEntites;
    }

    /**
     * Crée les fichiers CSV avec la liste des classes et des paquets et leurs metriques réspectives.
     *
     * @param chemin        chemin du dossier où les fichiers CSV seront placés
     * @param type          classe ou paquet
     * @param calculEntites tableau de hashmaps des valeurs des métriques
     * @throws IOException si le chemin du dossier n'est pas passé en paramètre
     */
    public static void creerFichierCsv(String chemin, Type type, HashMap[] calculEntites) throws IOException {

        FileWriter ecritureFichier = new FileWriter(chemin + File.separator + type.getNom() + "s.csv");
        CSVWriter ecritureCsv = new CSVWriter(ecritureFichier);
        String[] enTete = {CHEMIN.getNom(), type.getNom(), LOC.stringFrom(type), CLOC.stringFrom(type),
                DC.stringFrom(type), type == CLASSE ? WMC.getNom() : WCP.getNom(), BC.stringFrom(type)};
        ecritureCsv.writeNext(enTete, false);

        for (HashMap<Type, Object> classe : calculEntites) {
            String[] line = {classe.get(CHEMIN).toString(), classe.get(type).toString(),
                    classe.get(LOC.typeFrom(type)).toString(), classe.get(CLOC.typeFrom(type)).toString(),
                    classe.get(DC.typeFrom(type)).toString(), type == CLASSE ? classe.get(WMC).toString() :
                    classe.get(WCP).toString(), classe.get(BC.typeFrom(type)).toString()};
            ecritureCsv.writeNext(line, false);
        }
        ecritureCsv.close();
    }

    /**
     * @param FICHIER_CONFIG nom du fichier de configuration
     */
    public static void getConfiguration(String FICHIER_CONFIG) {

        Scanner fichier;

        try {
            fichier = new Scanner(new File(FICHIER_CONFIG));
            while (fichier.hasNext()) {
                String ligne = fichier.nextLine();
                if (!ligne.isEmpty() && ligne.charAt(0) != '#') {
                    ligne = ligne.trim();
                    ligne = ligne.replaceAll(" ", "");
                    String arg1 = ligne.substring(0, ligne.indexOf("="));
                    String arg2 = ligne.substring(ligne.indexOf("=") + 1);
                    arg2 = arg2.replaceAll("\"", "");

                    switch (arg1) {
                        case "EXTENSION_FICHIER":
                            EXTENSION_FICHIER = arg2;
                            break;
                        /*
                         * Ajouter d'autres propriétés à lire
                         *
                         * */
                    }
                }
            }
            fichier.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Fichier introuvable: "+FICHIER_CONFIG);
            System.exit(2);
        }
    }
}