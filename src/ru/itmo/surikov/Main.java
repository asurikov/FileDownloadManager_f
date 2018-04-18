package ru.itmo.surikov;

import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        // write your code here

        if (!FDMUtility.firstCheck(args)) {

            return;
        }

//старый код. удалил так.как паттерн очень большой получился и приминим к винде
        // Pattern patern = Pattern.compile("\\d\\s([a-zA-Z]\\:|\\\\)\\\\([^\\\\]+\\\\)*[^\\/:*?\"<>|]+\\s(([a-zA-Z]\\:|\\\\)\\\\([^\\\\]+\\\\)*[^\\/:*?\"<>|]+)");
//        for (String item : args) {
//            System.out.println(item);
//        }
        //создаем новый объект Опция с проверкой входных данных
        Option op = new Option();
        try {
            op = Option.CheckParametrs(args);
            System.out.println(op);

        } catch (Exception e) {
            // e.printStackTrace();

            FDMUtility.errorPrintln(e.getMessage());
            FDMUtility.help();
            return;
        }
        FileDownloader fileDownloader = new FileDownloader(op);
        fileDownloader.run();


    }


}