package ru.itmo.surikov;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

//класс потока чтения/записи
public class ReadWriteThread implements Runnable {
    static int filesCount = 0;
    static long bytesCount = 0;
    static ArrayList<String> errorList = new ArrayList<>();
// сделать общую статистику вида
//    Завершено: 100%
//    Загружено: 17 файлов, 2.3 MB
//    Время: 2 минуты 13 секунд
//    Средняя скорость: 17.2 kB/s

    public static int getFilesCount() {
        return filesCount;
    }

    public static long getBytesCount() {
        return bytesCount;
    }
//массив где будут храниться сообщения об ошибках
    public static ArrayList<String> getErrorList() {
        return errorList;
    }

    private URL url; //ссылка на адрес закачки
    private File outputFile;//выходной файл

    //конструктор с аргументами что и куда читать/писать
    public ReadWriteThread(URL url, File outputFile) {
        this.url = url;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        // сделать статистику на каждый файл
        // Загружается файл: %ИМЯ%
        //Файл %ИМЯ% загружен: 1 MB за 1 минуту
        Timer tm = new Timer();
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            System.out.println("Загружается файл: " + outputFile.getName());
            outputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);


            System.out.println("Файл " + outputFile.getName() + " загружен: " + FDMUtility.humanReadableByteCount(outputFile.length()) + " за  " + tm.getTime());
            filesCount++;
            bytesCount += outputFile.length();
        } catch (Exception e) {
            // e.printStackTrace(); // В случае ошибки выводим стектрейс
            errorList.add("Ошибка. Файл " + outputFile.getName()+ " не обработан. Проверьте правильность ссылки "+ url.toString());
            //System.out.println("Ошибка. Файл " + url.getFile()+" не обработан. Проверьте правильность ссылки" );
        } finally {

        }

    }
}