package ru.itmo.surikov;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
//класс выбранных опций. Но наверно его можно удалить как атавизм от начальной версии и оперировать полямиFileDownloadera
public class Option {
    private int numberThread;//кол-во потоков
    private File outputFolder; //выходная папка
    private File fileLinks;//файл ссылок

    public int getNumberThread() {
        return numberThread;
    }

    public Option() {
    }

    public Option(int numberThread, File outputFolder, File fileLinks) {
        this.numberThread = numberThread;
        this.outputFolder = outputFolder;
        this.fileLinks = fileLinks;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public File getFileLinks() {
        return fileLinks;
    }

    public static Option CheckParametrs(String[] args) throws Exception {
        int _numberThread = Integer.parseInt(args[0]);
        File _outputFolder = new File(args[1]);
        File _fileLinks = new File(args[2]);
if (_numberThread<1)throw new FileNotFoundException("Количество потоков должно быть больше 0");//проверяем количество потоков >0
//Старый кусок кода переписал проверку в другом месте
//        if (!_fileLinks.exists())
//            throw new FileNotFoundException("Файл с ссылками не найден");
//
//
//        if (!_outputFolder.exists()) {
//
//            if (_outputFolder.mkdirs()) {
//                System.out.println("Папка создана");
//            } else {
//
//                throw new FileNotFoundException("Ошибка при создании папки ");
//            }
//        }

        Option option = new Option(_numberThread, _outputFolder, new File(args[2]));
        return option;
    }

    @Override
    public String toString() {
        return "Количество потоков=" + numberThread +
                ", выходная папка=" + outputFolder +
                ", файл с ссылками=" + fileLinks +
                '}';
    }
}
//todo Сделать свой класс exceptions