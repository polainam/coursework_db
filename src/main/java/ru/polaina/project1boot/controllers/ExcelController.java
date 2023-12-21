package ru.polaina.project1boot.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.JournalService;
import ru.polaina.project1boot.services.PeopleService;
import ru.polaina.project1boot.services.TypeBookService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ExcelController {
private final JournalService journalService;
private final PeopleService peopleService;
private final BooksService booksService;
private final TypeBookService typeBookService;

    public ExcelController(JournalService journalService, PeopleService peopleService, BooksService booksService, TypeBookService typeBookService) {
        this.journalService = journalService;
        this.peopleService = peopleService;
        this.booksService = booksService;
        this.typeBookService = typeBookService;
    }

    @GetMapping("/exportToExcel/journal")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        List<Journal> journalEntries = journalService.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Journal Entries");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Book", "Client", "Date Begin", "Date End", "Date Return"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Journal entry : journalEntries) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getBook().getTitle());
                row.createCell(1).setCellValue(entry.getPerson().getUserName());
                row.createCell(2).setCellValue(entry.getDateBegin().toString());
                row.createCell(3).setCellValue(entry.getDateEnd().toString());
                Optional<Date> dateRet = Optional.ofNullable(entry.getDateReturn());
                dateRet.ifPresent(date -> row.createCell(4).setCellValue(date.toString()));
            }

            workbook.write(out);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            httpHeaders.setContentDispositionFormData("attachment", "journal_entries.xlsx");

            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .body(out.toByteArray());
        }
    }

    @GetMapping("/exportToExcel/clients")
    public ResponseEntity<byte[]> exportClientsToExcel() throws IOException {
        List<Person> clients = peopleService.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Clients");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"User name", "First name", "Last name", "Passport seria", "Passport number"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Person client : clients) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(client.getUserName());
                row.createCell(1).setCellValue(client.getFirstName());
                row.createCell(2).setCellValue(client.getLastName());
                row.createCell(3).setCellValue(client.getPassportSeria());
                row.createCell(4).setCellValue(client.getPassportNum());
            }

            workbook.write(out);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            httpHeaders.setContentDispositionFormData("attachment", "clients.xlsx");

            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .body(out.toByteArray());
        }
    }

    @GetMapping("/exportToExcel/books")
    public ResponseEntity<byte[]> exportBooksToExcel() throws IOException {
        List<Book> books = booksService.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Books");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Title", "Number of copies", "Book type"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getTitle());
                row.createCell(1).setCellValue(book.getNumberOfCopies());
                row.createCell(2).setCellValue(book.getTypeBook().getTypeName());
            }

            workbook.write(out);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            httpHeaders.setContentDispositionFormData("attachment", "books.xlsx");

            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .body(out.toByteArray());
        }
    }
    @GetMapping("/exportToExcel/bookTypes")
    public ResponseEntity<byte[]> exportBookTypesToExcel() throws IOException {
        List<TypeBook> bookTypes = typeBookService.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Book Types");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Name", "Count books", "Fine", "Book reading time"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (TypeBook bookType : bookTypes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(bookType.getTypeName());
                row.createCell(1).setCellValue(bookType.getCountOfTypes());
                row.createCell(2).setCellValue(bookType.getFine());
                row.createCell(3).setCellValue(bookType.getCountOfDaysToRead());
            }

            workbook.write(out);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            httpHeaders.setContentDispositionFormData("attachment", "book_types.xlsx");

            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .body(out.toByteArray());
        }
    }

}

