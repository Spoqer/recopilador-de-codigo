import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Recopila el codigo de un programa en un solo archivo de word.
 */
public class RecopiladorDeCodigo {
    private static XWPFDocument documentoDeSalida = new XWPFDocument();

    enum Opciones {
        h,
        r;
    }

    /**
     * Corre el programa.
     * @param args El primer argumento es una opcion o es el path del directorio,
     *             si el primero es -r, el segundo es el path del directorio y
     *             los siguientes son los tipos de archivos a recopilar
     */
    public static void main(String[] args) {
        ArrayList<String> argumentos = new ArrayList<>(Arrays.asList(args));
        ArrayList<File> directoriosAndArchivos = new ArrayList<>();
        String directorio = "";

        for (String argumento :
                argumentos) {
            System.out.println(argumento);
        }
        //Crear documento Word de salida
        try {

            FileOutputStream salida = new FileOutputStream(new File("recopilacion.docx"));
            documentoDeSalida.write(salida);
            salida.close();

            if (!(args.length > 0)) {
                System.out.println("No se definieron argumentos para el codigo a recopilar");
            }

            //Para desplegar la ayuda, el primer argumento debe ser -h
            if (args[0].equals("-" + Opciones.h)) {
                mostrarAyuda();
                return;
            }

            //Para usar un directorio relativo, el primer argumento debe ser -r
            if (args[0].equals("-" + Opciones.r)) {
                //Define el directorio respecto a donde se esta corriendo el programa
                argumentos.remove(0);
                directorio = directorio + System.getProperty("user.dir");
            }

            //TODO: Validar el path
            boolean pathValido = true;
            if (pathValido) {

                if (argumentos.size() > 0) {
                    directorio = directorio + argumentos.get(0);
                    argumentos.remove(0);
                }
                System.out.printf("Directorio: %s \n", directorio);

                final File carpeta = new File(directorio);
                for (String tipoDeArchivo :
                        argumentos) {
                    System.out.printf("-- %s\n", tipoDeArchivo);
                }
                directoriosAndArchivos = listaDeCarpetasAndArchivos(carpeta, argumentos);

            } else {
                System.out.printf("Path invalido: %s", argumentos);
            }

            for (File entry :directoriosAndArchivos) {
                if (entry.isDirectory()) {
                    System.out.print(">>");
                }
                System.out.println(entry.getName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static ArrayList<File> listaDeCarpetasAndArchivos(final File directorioRaiz, ArrayList<String> tiposDeArchivo) {
        ArrayList<File> listaDeCarpetasAndArchivos = new ArrayList<>();

        for (final File entry : directorioRaiz.listFiles()) {
            if (entry.isDirectory()) {
                listaDeCarpetasAndArchivos.add(entry);
                listaDeCarpetasAndArchivos.addAll(listaDeCarpetasAndArchivos(entry, tiposDeArchivo));
            } else {
                for (String tipoDeArchivo : tiposDeArchivo) {
                    String extension = entry.getName();
                    String[] nombreArchivo = extension.split("\\.");
                    if (nombreArchivo.length > 1) {
                        extension= nombreArchivo[1];
                        if (extension.equals(tipoDeArchivo)) {
                            listaDeCarpetasAndArchivos.add(0, entry);
                        }
                    }
                }
            }
        }

        return listaDeCarpetasAndArchivos;
    }

    private static void mostrarAyuda() {
        System.out.println("recopilador-de-codigo");
        System.out.println();
        System.out.println("Para usar, se necesitan al menos 2 argumentos");
        System.out.println("1.- La ruta del directorio raiz del proyecto a recopilar");
//        System.out.println("Se puede usar la opcion -r como primer argumento para que la ruta sea relativa " +
//                "al directorio en donde se corre la aplicación");
        System.out.println("2.- Los tipos de archivo a recopilar, por ejemplo \"java\", \"jsp\", etc.");
        System.out.println("Debe haber al menos uno, pueden ser varios");
        System.out.println();
        System.out.println("Por ejemplo, para recopilar los archivos java de un proyecto que se encuentra en \"c:\\Proyecto\\\":");
        System.out.println("java RecopiladorDeCodigo c:\\Proyecto\\ java");
    }
}
