/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librarybooktracker;

import java.io.File;
import java.util.List;

public class OperationAnalyzerTask implements Runnable {

    private List<Book> books;
    private String operation;
    private File file;

    public OperationAnalyzerTask(List<Book> books,
                                 String operation,
                                 File file) {
        this.books = books;
        this.operation = operation;
        this.file = file;
    }

    @Override
    public void run() {
        try {

            if (operation.matches("\\d{13}")) {
                LibraryBookTracker.searchByISBN(books, operation);
            }
            else if (operation.contains(":")) {
                LibraryBookTracker.addBook(books, operation, file);
            }
            else {
                LibraryBookTracker.searchByTitle(books, operation);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}