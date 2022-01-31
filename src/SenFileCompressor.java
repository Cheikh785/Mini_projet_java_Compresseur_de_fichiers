import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SenFileCompressor {
    public static void help() {
        System.out.println("Aide :\n " +
                "-L'option '-c' suivi d'une liste de fichier permet d'archiver et de compresser un ensemble de fichier en un seul fichier `.sfc`;" +
                "-L'option `-d` suivi d'un fichier `.sfc` pour de décompresser et de désarchiver un fichier `.sfc`;" +
                "-L'option `-r` permet de spécifier un chemin (absolue ou relatif) vers un répertoire où seront stockés les fichiers (le fichier) compressés ou décompressés ; " +
                "-Spécifier en plus de l'option `-r` l'option `-f` si le répertoire n'existe pas, auquel cas ce dernier sera créer automatiquement;" +
                "-Spécifier l'option `-v` pour la verbosité du programme; " +
                "-L'option `-h` permet d'avoir l'aide sur l'utilisation du programme\n;");
        System.exit(0);
    }

    public static void archivage(List<String> listeSrcFiles, String... params) {
        try {
            FileOutputStream outFile = new FileOutputStream("Archive.zip");
            File source = null;
            if (params.length >= 1) {
                String path = params[0];
                source = new File(path);
                if (!source.isDirectory()) {
                    if (params.length == 2) {
                        if (source.mkdir()) {
                            outFile = new FileOutputStream(source.getAbsolutePath() + FileSystems.getDefault().getSeparator() + "Archive.zip");
                        } else {
                            System.out.println("Erreur lors de la création du répertoire " + path);
                            System.exit(0);
                        }
                    } else {
                        System.out.println(path + " n'est pas un répertoire ! Veuillez spécifier l'option `-f` pour créer automatiquement le répertoire");
                        System.exit(0);
                    }
                } else {
                    outFile = new FileOutputStream(source.getAbsolutePath() + FileSystems.getDefault().getSeparator() + "Archive.zip");
                }
            }
            ZipOutputStream out = new ZipOutputStream(outFile);
            for (String srcFile : listeSrcFiles) {
                File file = new File(srcFile);
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                byte[] bytes = new byte[1024];
                int length;
                while ((length = in.read(bytes)) >= 0) {
                    out.write(bytes, 0, length);
                }
                in.close();
            }
            out.close();
            outFile.close();

        } catch (IOException e) {
            System.out.println("Erreur sur les fichiers renseignés !");
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    public static void desarchivage(String file, String... params) {
        try {
            File destDirectory = new File("./");
            File source = null;
            String path;
            if (params.length >= 1) {
                path = params[0];
                source = new File(path);
                if (!source.isDirectory()) {
                    if (params.length == 2) {
                        if (source.mkdir()) {
                            destDirectory = new File(source.getAbsolutePath());
                        } else {
                            System.out.println("Erreur lors de la création du répertoire " + path);
                            System.exit(0);
                        }
                    } else {
                        System.out.println(path + " n'est pas un répertoire ! Veuillez spécifier l'option `-f` pour créer automatiquement le répertoire");
                        System.exit(0);
                    }
                } else {
                    destDirectory = new File(source.getAbsolutePath());
                }
            }
//            File destDirectory = new File("unzip_directory");
            byte[] buffer = new byte[1024];
            ZipInputStream in = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry = in.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDirectory, zipEntry.getName());
                FileOutputStream out = new FileOutputStream(newFile);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
                zipEntry = in.getNextEntry();
            }
            in.closeEntry();
            in.close();
        } catch (IOException e) {
            System.out.println("Erreur sur le fichier à décompresser");
            e.printStackTrace();
            System.exit(0);
        }

        System.exit(0);
    }

    public static void main(String[] args) {
        System.out.println("\n\n\t\t\t\tBienvenue dans notre compresseur de fichiers !\n\n");
        List<String> listSrcFiles = new ArrayList<String>();
        int k = 0;
        if (args.length == 0) {
            help();
        } else if (args.length == 1) {
            help();
        } else {
            if (args[0].equals("-c") || args[0].equals("-d")) {
                System.out.println("OK - option -c | -d");
                if (args.length == 2) {
                    if (args[0].equals("-c")) {
                        System.out.println("OK - Archivage with option -c only");
                        listSrcFiles.add(args[1]);
                        archivage(listSrcFiles);
                    }else {
                        System.out.println("OK - Désarchivage with option -d only");
                        desarchivage(args[1]);
                    }
                }
            } else {
                help();
            }
            if (args.length >= 3) {
                if (args[args.length - 1].equals("-v")) {
                    if (args[args.length - 2].equals("-f")) {
                        if (args[args.length - 4].equals("-r")) {
                            if (args[0].equals("-c")) {
                                System.out.println("OK - Archivage avec option -r, -f and -v");
                                for (int i = 1; i <= args.length - 5; i++) {
                                    listSrcFiles.add(args[i]);
                                }
                                archivage(listSrcFiles, args[args.length - 3], "f");
                            } else {
                                if (args.length == 6) {
                                    System.out.println("OK - Désarchivage avec option -r, -f et -v");
                                    desarchivage(args[1], args[args.length - 3], "f");
                                }
                                else {
                                    help();
                                }
                            }
                        } else {
                            help();
                        }
                    } else if (args[args.length - 3].equals("-r")) {
                        if (args[0].equals("-c")) {
                            System.out.println("Archivage with option -d, -r and option -v");
                            listSrcFiles = new ArrayList<String>(Arrays.asList(args).subList(1, args.length - 3));
                            archivage(listSrcFiles, args[args.length - 2]);
                        } else {
                            if (args.length == 5) {
                                System.out.println("Désarchivage avec option -d, -r et -v");
                                desarchivage(args[1], args[args.length - 2]);
                            } else {
                                help();
                            }
                        }
                    } else {
                        if (args[0].equals("-c")) {
                            System.out.println("Archivage with option -c and -v");
                            listSrcFiles = new ArrayList<String>(Arrays.asList(args).subList(1, args.length - 1));
                            archivage(listSrcFiles);
                        } else {
                            if (args.length == 3) {
                                System.out.println("Désarchivage with option -d and -v");
                                desarchivage(args[1]);
                            } else {
                                help();
                            }
                        }
                    }
                } else if (args[args.length - 1].equals("-f")) {
                    if (args[args.length - 3].equals("-r")) {
                        if (args[0].equals("-c")) {
                            System.out.println("OK - Archivage with option -r and -f");
                            for (int i = 1; i <= args.length - 4; i++) {
                                listSrcFiles.add(args[i]);
                            }
                            archivage(listSrcFiles, args[args.length - 2], "f");
                        } else {
                            if (args.length == 5) {
                                System.out.println("OK - Désarchivage with option -r and -f");
                                desarchivage(args[1], args[args.length - 2], "f");
                            } else {
                                help();
                            }
                        }
                    } else {
                        help();
                    }
                } else if (args[args.length - 2].equals("-r")) {
                    if (args[0].equals("-c")) {
                        System.out.println("OK - Archivage with option -r");
                        listSrcFiles = new ArrayList<String>(Arrays.asList(args).subList(1, args.length - 2));
                        archivage(listSrcFiles, args[args.length - 1]);
                    } else {
                        if (args.length == 4) {
                            System.out.println("Ok - Désarchivage with option -r");
                            desarchivage(args[1], args[args.length - 1]);
                        } else {
                            help();
                        }
                    }
                }
                if (args[0].equals("-c")) {
                    System.out.println("OK - Archivage with option -c only");
                    listSrcFiles = new ArrayList<String>(Arrays.asList(args).subList(1, args.length));
                    archivage(listSrcFiles);
                } else {
                    help();
                }
            }
        }
    }
}
