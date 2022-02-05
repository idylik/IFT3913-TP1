import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        String extensionFichier = ".java";
        File absoluteDossier = new File(System.getProperty("user.dir"));
        listeFichiers(absoluteDossier, extensionFichier);



    }



    public static void listeFichiers(File chemin, String extensionFichier) throws IOException {
        File listeFichiers[] = chemin.listFiles();
        for (File fichier : listeFichiers) {
            if (fichier.isFile()) {

                if (fichier.getName().endsWith(extensionFichier)) {
                    System.out.println(fichier.getAbsoluteFile());
                    String absoluteNomFichier = fichier.getAbsoluteFile().toString();
                    compterLignes(absoluteNomFichier);
                }

            } else {
                listeFichiers(fichier, extensionFichier);
            }
        }
    }


    //Compter lignes
    public static void compterLignes(String absoluteNomFichier) throws IOException {

        String contenu = new String(Files.readAllBytes(Paths.get(absoluteNomFichier)));

        contenu = contenu.replaceAll(" ", "");

        int newLineCount = 0;
        for (int i=0; i < contenu.length(); i++) {
            if (contenu.charAt(i) == '\n') {
                newLineCount++;
            }
        }

        System.out.println(contenu);
        System.out.println("Nombre de lignes: "+newLineCount);
    }

}



