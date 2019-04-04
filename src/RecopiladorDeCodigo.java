public class RecopiladorDeCodigo {
    enum Opciones {
        h;
    }

    public static void main(String[] args) {
        //TODO: Recursivamente revisa los archivos de una carpeta, si son de algún tipo seleccionado,
        // los devuelve y añade al documento, si no, los ignora... y si es una carpeta, corre dentro de ella.
        // El encabezado de cada archivo es su ruta y su nombre

        if (!(args.length > 0)) {
            System.out.println("No se definieron argumentos para el codigo a recopilar");
        }

        //Para desplegar la ayuda, el primer argumento debe ser -h
        if (args[0].equals("-" + Opciones.h)) {
            mostrarAyuda();
        }
    }

    private static void mostrarAyuda() {
        System.out.println("recopilador-de-codigo");
        System.out.println();
        System.out.println("Para usar, se necesitan al menos 2 argumentos");
        System.out.println("1.- La ruta del directorio raiz del proyecto a recopilar");
        System.out.println("Se puede usar la opcion -r como primer argumento para que la ruta sea relativa " +
                "al directorio en donde se corre la aplicación");
        System.out.println("2.- Los tipos de archivo a recopilar, por ejemplo \"java\", \"jsp\", etc.");
        System.out.println("Debe haber al menos uno, pueden ser varios");
        System.out.println();
        System.out.println("Por ejemplo, para recopilar los archivos java de un proyecto que se encuentra en \"c:\\Proyecto\\\":");
        System.out.println("java RecopiladorDeCodigo c:\\Proyecto\\ java");
    }
}
