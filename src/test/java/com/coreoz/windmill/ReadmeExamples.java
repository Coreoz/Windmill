package com.coreoz.windmill;

import com.coreoz.windmill.exports.exporters.csv.ExportCsvConfig;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.Parsers;
import com.coreoz.windmill.imports.parsers.csv.CsvParserConfig;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadmeExamples {

    private static final Bean bean1 = Bean.builder()
            .name("first name")
            .user(new User("first login"))
            .build();

    private static final Bean bean2 = Bean.builder()
            .name("second name")
            .user(new User("second login"))
            .build();

    private static final Bean bean3 = Bean.builder()
            .name("third name")
            .user(new User("third login"))
            .build();

    private static final Film film1 = Film.builder()
            .title("first title")
            .releaseDate(new Date())
            .build();

    private static final Film film2 = Film.builder()
            .title("second title")
            .releaseDate(new Date())
            .build();

    @Data
    @Builder
    public static class Bean {
        private String name;
        private User user;
    }

    @Data
    @Builder
    public static class User {
        private String login;
    }

    @Data
    @Builder
    public static class Film {
        private String title;
        private Date releaseDate;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Assert.assertTrue(new File("Export.xlsx").delete());
    }

    @Test
    public void should_write_into_xls_file() throws IOException {
        // example start
        Windmill
                .<Bean>exporter()
                .withHeaders()
                .column("Name", Bean::getName)
                .column("User login", bean -> bean.getUser().getLogin())
                .asExcel()
                .writeRows(Arrays.asList(bean1, bean2, bean3))
                .writeInto(new FileOutputStream("Export.xlsx"));
        // example end

        List<Bean> actual = Windmill
                .importer()
                .source(FileSource.of(new FileInputStream("Export.xlsx")))
                .withHeaders()
                .stream()
                .map(row -> new Bean(row.cell("Name").asString(), new User(row.cell(1).asString())))
                .collect(Collectors.toList());

        assertThat(actual).containsExactlyElementsOf(Arrays.asList(bean1, bean2, bean3));
    }

    @Test
    public void should_write_into_csv_file() throws IOException {
        // example start
        byte[] bytes = Windmill
                .<Bean>exporter()
                .withoutHeaders()
                .column(Bean::getName)
                .column(bean -> bean.getUser().getLogin())
                .asCsv(ExportCsvConfig.builder()
                        .separator(';')
                        .escapeChar('"')
                        .build())
                .writeRows(Arrays.asList(bean1, bean2, bean3))
                .toByteArray();
        // example end

        List<Bean> actual = Windmill
                .importer()
                .source(FileSource.of(bytes))
                .parser(Parsers.csv(CsvParserConfig.builder()
                        .separator(';')
                        .escapeChar('"')
                        .quoteChar('\'')
                        .build()))
                .withoutHeaders()
                .stream()
                .map(row -> new Bean(row.cell(0).asString(), new User(row.cell(1).asString())))
                .collect(Collectors.toList());

        assertThat(actual).containsExactlyElementsOf(Arrays.asList(bean1, bean2, bean3));
    }

    @Test
    public void should_write_into_single_xls_workbook() throws IOException {
        // example start
        Workbook xlsxFile = new XSSFWorkbook();

        Windmill
                .<Bean>exporter()
                .withoutHeaders()
                .column(Bean::getName)
                .column(bean -> bean.getUser().getLogin())
                .asExcel(ExportExcelConfig.fromWorkbook(xlsxFile)
                        .build("First tab"))
                .writeRows(Arrays.asList(bean1, bean2, bean3));

        Windmill
                .<Film>exporter()
                .withoutHeaders()
                .column(Film::getTitle)
                .column(Film::getReleaseDate)
                .asExcel(ExportExcelConfig.fromWorkbook(xlsxFile)
                        .build("Second tab with films"))
                .writeRow(film1)
                .writeRow(film2);

        xlsxFile.write(new FileOutputStream("Export.xlsx"));
        // example end

        List<Bean> actualBeans = Windmill
                .importer()
                .source(FileSource.of(new FileInputStream("Export.xlsx")))
                .parser(Parsers.xlsx("First tab"))
                .withoutHeaders()
                .stream()
                .map(row -> new Bean(row.cell(0).asString(), new User(row.cell(1).asString())))
                .collect(Collectors.toList());

        assertThat(actualBeans).containsExactlyElementsOf(Arrays.asList(bean1, bean2, bean3));

        List<Film> actualFilms = Windmill
                .importer()
                .source(FileSource.of(new FileInputStream("Export.xlsx")))
                .parser(Parsers.xlsx("Second tab with films"))
                .withoutHeaders()
                .stream()
                .map(row -> {
                    String title = row.cell(0).asString();
                    Date releaseDate = DateUtil.getJavaDate(row.cell(1).asDouble().value());
                    return new Film(title, releaseDate);
                })
                .collect(Collectors.toList());

        assertThat(actualFilms).containsExactlyElementsOf(Arrays.asList(film1, film2));
    }
}
