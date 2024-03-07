import java.io.*;
import java.util.zip.*;

public class SenFileCompressor {

    public static void regrouperFichiers(String fichierRegroupe, String[] fichiersOriginaux) {
        try (FileOutputStream fileStream = new FileOutputStream(fichierRegroupe);
             ZipOutputStream zos = new ZipOutputStream(fileStream)) {

            for (int i = 0; i < fichiersOriginaux.length; i++) {
                ajouterZip(fichiersOriginaux[i], zos);
            }

            System.out.println("Fichiers regroupés avec succès.");

        } catch (IOException e) {}
    }

    public static void extraireFichiers(String fichierZip, String repertoireDestination) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fichierZip))) {
            byte[] buffer = new byte[1024];
            int len;

            while ((len = zis.read(buffer)) > 0) {
                String nomFichier = zis.getNextEntry().getName();
                String cheminDestination = repertoireDestination + File.separator + nomFichier;

                try (FileOutputStream fos = new FileOutputStream(cheminDestination)) {
                    fos.write(buffer, 0, len);
                }

                System.out.println("Fichier extrait: " + cheminDestination);
            }

            System.out.println("Fichiers extraits avec succès.");

        } catch (IOException e) {}
    }

    private static void ajouterZip(String fichierOriginal, ZipOutputStream zos) throws IOException {
        File fichier = new File(fichierOriginal);
        try (FileInputStream fis = new FileInputStream(fichier)) {
            ZipEntry entry = new ZipEntry(fichier.getName());
            zos.putNextEntry(entry);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            zos.closeEntry();
            System.out.println("Fichier ajouté au zip: " + fichierOriginal);
        }
    }

    private static void ajouterZipAide(File fichier, ZipOutputStream zos, byte[] buffer) throws IOException {
        ZipEntry entry = new ZipEntry(fichier.getName());
        zos.putNextEntry(entry);

        try (FileInputStream fis = new FileInputStream(fichier)) {
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        }

        zos.closeEntry();
    }

    public static void compressFichiers(String fichierSource, String fichierDestination) {
        try (FileInputStream fis = new FileInputStream(fichierSource);
             DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(fichierDestination))) {

            byte[] buffer = new byte[1024];
            int len;

            while ((len = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, len);
            }

            System.out.println("Fichier compressé avec succès.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void regroupeCompresse(String fichierDestination, String[] fichiersOriginaux) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fichierDestination))) {
            byte[] buffer = new byte[1024];

            for (String fichierOrig : fichiersOriginaux) {
                File fichier = new File(fichierOrig);

                if (fichier.exists()) {
                    ajouterZipAide(fichier, zos, buffer);
                } else {
                    System.out.println("Le fichier " + fichierOrig + " n'existe pas");
                }
            }

            System.out.println("Fichiers regroupés et compressés avec succès");

        } catch (IOException e) {}
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String option = args[0].toLowerCase();

            if ("-h".equals(option)) {
                afficherAideH();
                return;
            }   
            else if ("-c".equals(option) && args.length > 1) {
                String fichierDestination = args[1] + ".sfc";
                String[] fichiersOriginaux = new String[args.length - 2];

                for (int i = 2; i < args.length; i++) {
                    fichiersOriginaux[i - 2] = args[i - 1];
                }

                regroupeCompresse(fichierDestination, fichiersOriginaux);
                return;
            }

            else if ("-d".equals(option) && args.length > 1) {
                String fichierAExtraire = args[1];
                String repertoireDestination = "fichiers_extraits";
                extraireFichiers(fichierAExtraire, repertoireDestination);
                return;
            } else {
                System.out.println("Option non reconnue");
                return;
            }
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Saisir nom du fichier zip : ");
            String fichierRegroupe = reader.readLine();

            System.out.print("Saisir le nombre de fichiers en attente : ");
            int nombreFichiers = Integer.parseInt(reader.readLine());

            String[] fichiersOriginaux = new String[nombreFichiers];
            for (int i = 0; i < nombreFichiers; i++) {
                System.out.print("Saisir le nom du fichier n°" + (i + 1) + " : ");
                fichiersOriginaux[i] = reader.readLine();
            }
            regrouperFichiers(fichierRegroupe, fichiersOriginaux);

            System.out.print("Voulez-vous extraire ces fichiers (oui/non) ? ");
            String choix1 = reader.readLine().toLowerCase();

            if ("oui".equals(choix1) || "yes".equals(choix1)) {
                String repertoireDestination = "fichiers_extraits";
                extraireFichiers(fichierRegroupe, repertoireDestination);
            }

            System.out.print("Voulez-vous compresser ces fichiers (oui/non) ? ");
            String choix2 = reader.readLine().toLowerCase();

            if ("oui".equals(choix2) || "yes".equals(choix2)) {
                String repertoireDestination = "fichiers_compresses";
                compressFichiers(fichierRegroupe, repertoireDestination);
            }

        } catch (IOException e) {}
    }

    private static void afficherAideH() {
        System.out.println("Instructions d'utilisation :");
        System.out.println("-h                  Afficher l'aide du programme");
    }

    private static void afficherAideC() {
        System.out.println("Instructions d'utilisation :");
        System.out.println("-c <fichier1> <fichier2> ...   Regrouper et compresser plusieurs fichiers");
    }

    private static void afficherAideD() {
        System.out.println("Instructions d'utilisation :");
        System.out.println("-d <fichier.sfc>   Extraire les fichiers d'un fichier compressé");
    }

}
