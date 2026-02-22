/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package librarybooktracker;

/**
 *
 * @author shaha
 */

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;

public class LibraryBookTracker {

    private static int validRecords = 0;
    private static int errors = 0;
    private static int searchResults = 0;
    private static int booksAdded = 0;

    public static void main(String[] args) {

        try {

            checkArguments(args);

            String filePath = args[0];
            String operation = args[1];

            File file = prepareFile(filePath);

            List<Book> books = readBooks(file);

            if (operation.matches("\\d{13}")) {
                searchByISBN(books, operation);
            }
            else if (operation.contains(":")) {
                addBook(books, operation, file);
            }
            else {
                searchByTitle(books, operation);
            }

            printStatistics();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Thank you for using the Library Book Tracker.");
        }
    }

    private static void checkArguments(String[] args)
            throws InsufficientArgumentsException, InvalidFileNameException {

        if (args.length < 2)
            throw new InsufficientArgumentsException("At least 2 arguments required.");

        if (!args[0].endsWith(".txt"))
            throw new InvalidFileNameException("File must end with .txt");
    }

    private static File prepareFile(String path) throws IOException {

        File file = new File(path);

        if (!file.exists()) {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            file.createNewFile();
        }

        return file;
    }

    private static List<Book> readBooks(File file) throws IOException {

        List<Book> books = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {

            try {
                Book book = parseBook(line);
                books.add(book);
                validRecords++;
            } catch (BookCatalogException e) {
                logError(line, e.getMessage(), file);
                errors++;
            }
        }

        br.close();
        return books;
    }

    private static Book parseBook(String line)
            throws BookCatalogException {

        String[] parts = line.split(":");

        if (parts.length != 4)
            throw new MalformedBookEntryException("Invalid format");

        String title = parts[0];
        String author = parts[1];
        String isbn = parts[2];
        String copiesStr = parts[3];

        if (title.isEmpty() || author.isEmpty())
            throw new MalformedBookEntryException("Title or author empty");

        if (!isbn.matches("\\d{13}"))
            throw new InvalidISBNException("Invalid ISBN");

        int copies;
        try {
            copies = Integer.parseInt(copiesStr);
        } catch (NumberFormatException e) {
            throw new MalformedBookEntryException("Copies must be a number");
        }

        if (copies <= 0)
            throw new MalformedBookEntryException("Copies must be positive");

        return new Book(title, author, isbn, copies);
    }

    private static void searchByTitle(List<Book> books, String keyword) {

        printHeader();

        for (Book b : books) {
            if (b.getTitle().toLowerCase()
                    .contains(keyword.toLowerCase())) {

                System.out.println(b);
                searchResults++;
            }
        }
    }

    private static void searchByISBN(List<Book> books, String isbn)
            throws DuplicateISBNException {

        printHeader();

        int count = 0;

        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                System.out.println(b);
                count++;
            }
        }

        if (count > 1)
            throw new DuplicateISBNException("Duplicate ISBN found");

        searchResults = count;
    }

    private static void addBook(List<Book> books,
                                String record,
                                File file)
            throws Exception {

        Book newBook = parseBook(record);

        books.add(newBook);
        booksAdded++;

        books.sort(Comparator.comparing(Book::getTitle,
                String.CASE_INSENSITIVE_ORDER));

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        for (Book b : books) {
            bw.write(b.getTitle() + ":" +
                     b.getAuthor() + ":" +
                     b.getIsbn() + ":" +
                     b.getCopies());
            bw.newLine();
        }

        bw.close();

        printHeader();
        System.out.println(newBook);
    }

    private static void logError(String text,
                                 String message,
                                 File file) {

        try {
            File logFile = new File(file.getParent(), "errors.log");
            FileWriter fw = new FileWriter(logFile, true);

            String time = LocalDateTime.now().toString();

            fw.write("[" + time + "] " + text +
                    " -> " + message + "\n");

            fw.close();

        } catch (IOException e) {
            System.out.println("Failed to write log.");
        }
    }

    private static void printHeader() {
        System.out.printf("%-30s %-20s %-15s %5s%n",
                "Title", "Author", "ISBN", "Copies");

    }

    private static void printStatistics() {
        System.out.println("\nValid records processed: " + validRecords);
        System.out.println("Search results: " + searchResults);
        System.out.println("Books added: " + booksAdded);
        System.out.println("Errors encountered: " + errors);
    }
}