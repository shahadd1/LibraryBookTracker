/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librarybooktracker;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileReaderTask implements Runnable {

    private File file;
    private List<Book> books;

    public FileReaderTask(File file, List<Book> books) {
        this.file = file;
        this.books = books;
    }

    @Override
    public void run() {
        try {
            List<Book> loaded =
                    LibraryBookTracker.readBooks(file);

            books.addAll(loaded);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}