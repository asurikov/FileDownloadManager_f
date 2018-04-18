package ru.itmo.surikov;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileDownloader {
    private static int freeSlots; // максимальное количество потоков одновременно качающие файлы.
    File ouputfolder;
    File linksFiles;
    ArrayList<String> readedLinks = new ArrayList<>();//массив прочитанных ссылок для избежания повторов


    public FileDownloader(Option op) {
        this.ouputfolder = op.getOutputFolder();
        this.linksFiles = op.getFileLinks();
        freeSlots = op.getNumberThread();
    }

    //проверка повторяющихся ссылок
    private boolean checkAndAdd(String s) {
        if (readedLinks.contains(s)) return false;
        else {
            readedLinks.add(s);
            return true;
        }
    }

    //метод для зупуска потоков чтения/записи файлов
    public void run() {
        Timer tm = new Timer(); //засекаем время
        ExecutorService threadPool = Executors.newFixedThreadPool(freeSlots); //пул потоков с заданой размерностью
        int lineCount = 0;//счетчик строк
        int fileReadedCount = 0;//счетчик ссылок для обработки
        try (BufferedReader br = new BufferedReader(new FileReader(this.linksFiles))) {
            String line;



                while ((line = br.readLine()) != null) {
                    lineCount++;
                    //проверка не комментарий ли в строке файлов ссылок
                    if (line.length()>2){
                        char c1 = line.charAt(0);
                        char c2 = line.charAt(1);
                        if ((c1=='/')&&(c2=='/'))continue;;

                    }
                    String[] files = line.split(" ");//разделитель пробел преобразуем в массив параметров(ссылка/имя вайла сохраниния)
                    //System.out.println(files[0]);
                    if (files.length == 2) {
                        // fileReadedCount++;//если ставить здесь то итоговый процент выполнения будет уменьшаться если будут попадаться повторы в файле со ссылками
                        if (checkAndAdd(files[0])) {//проверяем может ссылка уже была. true если ее не было
                            fileReadedCount++;//если ставить здесь то итоговы процент выполнения будет зависить от рабочая ссылка или нет и возможной ошибки во время скачивания
                            //Проверяем урл на валидность
                            if (!FDMUtility.isValidURL(files[0])){
                                FDMUtility.errorPrintln( ("Ошибка ввода/вывода с файлом " + linksFiles + " в строке " + lineCount));
                                continue;
                            };
                            URL url = new URL(files[0]);//делаем новый урл

                            File outputFile = new File(ouputfolder + File.separator + files[1]);
                            ReadWriteThread readWriteThread = new ReadWriteThread(url, outputFile);
                            threadPool.execute(readWriteThread);//запускаем пулл
                        } else {
                            FDMUtility.errorPrintln(files[0] + " ссылка уже скачивалась и будет пропущена");
                        }

                    } else {

                        FDMUtility.errorPrintln("!Ошибка  в файле " + linksFiles + " в строке с номером " + lineCount);
                    }

                }

        } catch (FileNotFoundException e) {
            FDMUtility.errorPrintln("!Файл " + linksFiles + " не найден!");
            // e.printStackTrace();
        } catch (IOException e) {
            FDMUtility.errorPrintln("Ошибка ввода/вывода с файлом " + linksFiles);
            // e.printStackTrace();
        } finally {
            threadPool.shutdown();
            ;//ждем завершения потоков
            while (!threadPool.isTerminated()) {
                System.out.print("\r.\r..\r\r...\r\r\r");//печатаем бегунок что еще не умерли
            }
//выводим статистику по завершению
            double completePercentage = ((double) ReadWriteThread.getFilesCount() / fileReadedCount) * 100;
            //проверка массива ошибок чтения/записи
            if (!(ReadWriteThread.errorList).isEmpty()){//если не пуст печатаем
                FDMUtility.errorPrintln("Список ошибок при скачивании:");
                for(String item:ReadWriteThread.errorList){
                    FDMUtility.errorPrintln(item);
                }
            }


            System.out.println("Завершено " + String.format("%.1f ", completePercentage) + "%");
            System.out.println("Загружено: " + ReadWriteThread.getFilesCount() + " файлов, " + FDMUtility.humanReadableByteCount(ReadWriteThread.getBytesCount()));
            System.out.println("Время " + tm.getTime());
            long timeExcution = tm.getSeconds();
            if (timeExcution > 0) {
                long averageSpeed = ReadWriteThread.getBytesCount() / timeExcution;
                System.out.println("Средняя скорость " + FDMUtility.humanReadableByteCount(averageSpeed) + "/s");
            }

//    Время: 2 минуты 13 секунд
//    Средняя скорость: 17.2 kB/s
        }


    }


}