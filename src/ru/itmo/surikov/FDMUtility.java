package ru.itmo.surikov;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

//Вспомогательный "Статический" класс для методов необходимых в работе программы. чтобы не засорять Main и легко портировать в другие схожие программы в будущем
public final class FDMUtility {
    //константы для работы с цветом в консоле
    public static final String ANSI_RESET;
    public static final String ANSI_BLACK;
    public static final String ANSI_RED;
    public static final String ANSI_GREEN;
    public static final String ANSI_YELLOW;
    public static final String ANSI_BLUE;
    public static final String ANSI_PURPLE;
    public static final String ANSI_CYAN;
    public static final String ANSI_WHITE;

    //todo разобраться!!!
    //конструктор статических полей
    static {
        String os = System.getProperty("os.name").toLowerCase();
        //windows
        if (os.indexOf("win") >= 0) {
            ANSI_RESET = "";
            ANSI_BLACK = "";
            ANSI_RED = "";
            ANSI_GREEN = "";
            ANSI_YELLOW = "";
            ANSI_BLUE = "";
            ANSI_PURPLE = "";
            ANSI_CYAN = "";
            ANSI_WHITE = "";
        } else {
            ANSI_RESET = "\u001B[0m";
            ANSI_BLACK = "\u001B[30m";
            ANSI_RED = "\u001B[31m";
            ANSI_GREEN = "\u001B[32m";
            ANSI_YELLOW = "\u001B[33m";
            ANSI_BLUE = "\u001B[34m";
            ANSI_PURPLE = "\u001B[35m";
            ANSI_CYAN = "\u001B[36m";
            ANSI_WHITE = "\u001B[37m";
        }


    }

    private FDMUtility() {
    }

    //метод первичной проверки входных параметров. Если соответствует то true
    public static boolean firstCheck(String[] args) {
        boolean checkFlagNumber = false;
        boolean checkFlagOutputPath = false;
        boolean checkFlagLinksFile = false;
        try {

            if (args.length == 3) {
                Path pathOutputFolder = Paths.get(args[1]);
                Path pathInputFile = Paths.get(args[2]);
                if (Pattern.matches("(\\d)+", args[0])) {
                    checkFlagNumber = true;
                } else {
                    //System.out.println(ANSI_RED+"Wrong the first argument. It must be a digit"+ANSI_RESET);

                    FDMUtility.errorPrintln("Неверный первый аргумент. Должно быть цифра количества потоков");
                    help();
                    return false;
                }
                // if ((Pattern.matches("(([a-zA-Z]\\:)|\\\\)\\\\([^\\\\]+\\\\)*[^\\/:*?\"<>|]+", args[1]))) {
                if (Files.exists(pathOutputFolder)) {
                    if (Files.isDirectory(pathOutputFolder)) {
                        checkFlagOutputPath = true;
                    }
                } else {
                    try {
                        Files.createDirectory(pathOutputFolder);
                        checkFlagOutputPath = true;
                    } catch (Exception e) {
                        FDMUtility.errorPrintln("Неверный второй аргумент. Должен быть путь к выходной папке или у вас нет прав на создания папки в указаном");
                        help();
                        return false;
                    }                    //System.out.println(ANSI_RED+"Wrong the second argument. It must be a path to output folder"+ANSI_RESET);

                }
                // if (Pattern.matches("((([a-zA-Z]\\:)|\\\\)\\\\([^\\\\]+\\\\)*)?[^\\/:*?\"<>|]+", args[2])) {
                if (Files.exists(pathInputFile)){
                  checkFlagLinksFile = true;
                } else {
               //  System.out.println(ANSI_RED+"Wrong the third argument. It must be a path to links file"+ANSI_RESET);
                 FDMUtility.errorPrintln("Неверный третий аргумент. Должен быть путь к файлу с сылками для скачивания");
                help();
                return false;
                }

            } else if (args.length == 1) {
                if (args[0].equals("/?")) {
                    help();
                    return false;
                }
            } else if (args.length == 0) {
                help();
                return false;

            }
            if (checkFlagNumber && checkFlagOutputPath&&checkFlagLinksFile) return true;
            FDMUtility.errorPrintln("Неверные аргументы");
            help();
            return false;
        } catch (
                Exception e)

        {
            //e.printStackTrace();
            FDMUtility.errorPrintln("Неверные аргументы");
            help();
            return false;
        }

    }

    public static void help() {
        System.out.println(ANSI_RED + "\n             Помощь \n" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "Консольная утилита для скачивания файлов по HTTP протоколу \n " + ANSI_RESET +
                "Входные параметры: \n" +
                ANSI_YELLOW + "- количество одновременно качающих потоков (1,2,3,4....) \n" +
                "- путь к файлу со списком ссылок \n" +
                "- имя папки, куда складывать скачанные файлы \n" + ANSI_RESET +
                "Пример вызова:\n " +
                ANSI_BLUE + "java -jar utility.jar 5 output_folder links.txt\n" + ANSI_RESET +
                "Формат файла со ссылками:\n" +
                ANSI_GREEN + "<HTTP ссылка><пробел><имя файла, под которым его надо " +
                "сохранить>\n" + ANSI_RESET +
                "пример:\n" +
                ANSI_BLUE + "<http://example.com><пробел><имя файла под которым его можно сохранить>\n\n" + ANSI_RESET +
                "В HTTP ссылке недолжно быть пробелов и encoded символов и прочего — это должны быть\n" +
                "обычные ссылки с латинскими символами без специальных символов в именах\n" +
                "файлов и прочее. Ссылкам  не делаеться decode. Ссылки должны быть без авторизации, не\n" +
                "HTTPS/FTP — всегда только HTTP-протокол.\n" +
                ANSI_RED + "Если ссылки  повторяются в файле, но с разными именами для сохранения,\n" +
                "то повторная закачка не происходит\n" + ANSI_RESET);
        //todo написать код хелпа
    }

    //проверка валидности URL старая версия. не корректно работала
//    public static boolean isValidURL(String url) {
//        try {
//            new URI(url).parseServerAuthority();
//            return true;
//        } catch (URISyntaxException e) {
//            return false;
//        }
//    }
    //проверка валидности URL
    public static boolean isValidURL(String url) {

        URL u = null;

        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }

    //метод преобразующий байты в кб,мб и т.д.
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (("KMGTPE").charAt(exp - 1) + "");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    //метод печати сообщения об ошибки в соответствии со стилем
    public static void errorPrintln(String error) {
        System.out.println(ANSI_RED + error + ANSI_RESET);
    }

}