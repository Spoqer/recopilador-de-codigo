import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Recopila el codigo de un programa en un solo archivo de word.
 */
public class RecopiladorDeCodigo {
    private static XWPFDocument documentoDeSalida = new XWPFDocument();
    private static XWPFStyles estilos = documentoDeSalida.createStyles();

    enum Opciones {
        h,
        r;
    }

    enum Estilos {
        Contenido,
        Carpeta,
        Archivo
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

        //crear estilos
        crearEstilo(Estilos.Contenido.toString());
        crearEstilo(Estilos.Carpeta.toString(), 1);
        crearEstilo(Estilos.Archivo.toString(), 2);

        for (String argumento :
                argumentos) {
            System.out.println(argumento);
        }
        //Crear documento Word de salida
        try {

            FileOutputStream salida = new FileOutputStream(new File("recopilacion.docx"));

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
                directoriosAndArchivos = recopilarCarpetasAndArchivos(carpeta, argumentos);

            } else {
                System.out.printf("Path invalido: %s", argumentos);
            }

//            for (File entry :directoriosAndArchivos) {
//                if (entry.isDirectory()) {
//                    System.out.print(">>");
//                }
//                System.out.println(entry.getName());
//
//            }
            documentoDeSalida.write(salida);
            salida.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static ArrayList<File> recopilarCarpetasAndArchivos(final File directorioRaiz,
                                                                ArrayList<String> tiposDeArchivo)
    throws IOException{
        ArrayList<File> listaDeCarpetasAndArchivos = new ArrayList<>();
        if (directorioRaiz.isDirectory()) {
            //escribirNombre(directorioRaiz, Estilos.Carpeta);


            for (final File entry : directorioRaiz.listFiles()) {
                if (entry.isDirectory()) {
                    listaDeCarpetasAndArchivos.add(entry);
                    listaDeCarpetasAndArchivos.addAll(recopilarCarpetasAndArchivos(entry, tiposDeArchivo));
                } else {
                    for (String tipoDeArchivo : tiposDeArchivo) {
                        String extension = entry.getName();
                        String[] nombreArchivo = extension.split("\\.");
                        if (nombreArchivo.length > 1) {
                            extension = nombreArchivo[1];
                            if (extension.equals(tipoDeArchivo)) {
                                escribirNombre(entry, Estilos.Archivo);
                                escribirContenido(entry);
                                listaDeCarpetasAndArchivos.add(0, entry);
                            }
                        }
                    }
                }
            }
        } else {
            System.out.printf("%s - no es un directorio\n", directorioRaiz.getName());
        }


        return listaDeCarpetasAndArchivos;
    }

    private static void escribirNombre(File file, Estilos tipo) {
        XWPFParagraph parrafo = documentoDeSalida.createParagraph();
        XWPFRun run = parrafo.createRun();
        run.setText(file.getPath());
        parrafo.setStyle(tipo.toString());
    }

    private static void escribirContenido(File archivo) throws IOException {
        XWPFParagraph parrafo = documentoDeSalida.createParagraph();
        XWPFRun run = parrafo.createRun();
        String contenido = new String(Files.readAllBytes(Paths.get(archivo.toURI())));

        run.setText(contenido);

        parrafo.setStyle(Estilos.Contenido.toString());
    }

    private static void crearEstilo(String nombre, int headingLevel) {
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(nombre);

        CTString nombreEstilo = CTString.Factory.newInstance();
        nombreEstilo.setVal(nombre);
        ctStyle.setName(nombreEstilo);

        CTDecimalNumber numeroDeIndentado = CTDecimalNumber.Factory.newInstance();

        CTOnOff onOff = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onOff);

        ctStyle.setQFormat(onOff);

        if (headingLevel >= 0) {
            numeroDeIndentado.setVal(BigInteger.valueOf(headingLevel));
            ctStyle.setUiPriority(numeroDeIndentado);
            CTPPr ppr = CTPPr.Factory.newInstance();
            ppr.setOutlineLvl(numeroDeIndentado);
            ctStyle.setPPr(ppr);
        } else {
            numeroDeIndentado.setVal(BigInteger.valueOf(estilos.getNumberOfStyles()+1));
            ctStyle.setUiPriority(numeroDeIndentado);
        }

        XWPFStyle estilo = new XWPFStyle(ctStyle);
        estilo.setType(STStyleType.PARAGRAPH);
        estilos.addStyle(estilo);
    }

    private static void crearEstilo(String nombre) {
        crearEstilo(nombre, -1);
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
