package com.taurustex.api.tools.excel;

import com.taurustex.api.dtos.CandidateExportDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelCsvExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] HEADERS = {
            "N_ENR", "N_TABLE", "NOM", "PRENOMS", "DATE_NAIS",
            "SEXE", "EMAIL", "TELEPHONE", "SERIE", "NATIONALITE",
            "OPTION", "STATUT"
    };

    /**
     * Génère un fichier Excel (.xlsx) sous forme de tableau d'octets.
     */
    public byte[] exportToExcel(List<CandidateExportDto> candidates) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Candidats");

            // En-tête style (Fond vert foncé / texte blanc)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 1. Création de la ligne d'en-tête
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. Remplissage des données
            int rowIdx = 1;
            for (CandidateExportDto c : candidates) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(nullToEmpty(c.getNumero()));
                row.createCell(1).setCellValue(nullToEmpty(c.getNumeroTable()));
                row.createCell(2).setCellValue(nullToEmpty(c.getLastName()));
                row.createCell(3).setCellValue(nullToEmpty(c.getFirstName()));

                Cell birthCell = row.createCell(4);
                if (c.getBirthDate() != null) {
                    birthCell.setCellValue(c.getBirthDate());
                }

                row.createCell(5).setCellValue(nullToEmpty(c.getGender()));
                row.createCell(6).setCellValue(nullToEmpty(c.getEmail()));
                row.createCell(7).setCellValue(nullToEmpty(c.getPhone()));
                row.createCell(8).setCellValue(nullToEmpty(c.getSerie()));
                row.createCell(9).setCellValue(nullToEmpty(c.getNationality()));
                row.createCell(10).setCellValue(nullToEmpty(c.getOption()));
                row.createCell(11).setCellValue(c.getStatus() != null ? c.getStatus() : "");
            }

            // Auto-ajustement de la largeur des colonnes
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Génère un fichier CSV sous forme de tableau d'octets avec séparateur point-virgule (;).
     */
    public byte[] exportToCsv(List<CandidateExportDto> candidates) {
        StringBuilder csvBuilder = new StringBuilder();

        // En-têtes avec BOM UTF-8 pour un affichage correct des caractères sous Excel
        csvBuilder.append("\uFEFF");
        csvBuilder.append(String.join(";", HEADERS)).append("\n");

        // Lignes de données (Alignées exactement sur HEADERS)
        for (CandidateExportDto c : candidates) {
            csvBuilder.append(escapeCsv(c.getNumero())).append(";")
                    .append(escapeCsv(c.getNumeroTable())).append(";")
                    .append(escapeCsv(c.getLastName())).append(";")
                    .append(escapeCsv(c.getFirstName())).append(";")
                    .append(c.getBirthDate() != null ? c.getBirthDate() : "").append(";")
                    .append(escapeCsv(c.getGender())).append(";")
                    .append(escapeCsv(c.getEmail())).append(";")
                    .append(escapeCsv(c.getPhone())).append(";")
                    .append(escapeCsv(c.getSerie())).append(";")
                    .append(escapeCsv(c.getNationality())).append(";")
                    .append(escapeCsv(c.getOption())).append(";")
                    .append(c.getStatus() != null ? c.getStatus() : "").append("\n");
        }

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}