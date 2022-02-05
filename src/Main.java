import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String extensionFichier = ".java";
        File dossier = new File(System.getProperty("user.dir"));
        listeFichiers(dossier, extensionFichier);
    }



    public static void listeFichiers(File chemin, String extensionFichier) {
        File listeFichiers[] = chemin.listFiles();
        for (File fichier : listeFichiers) {
            if (fichier.isFile()) {

                if (fichier.getName().endsWith(extensionFichier)) {
                    System.out.println(fichier.getAbsoluteFile());
                }

            } else {
                listeFichiers(fichier, extensionFichier);
            }
        }
    }


}



