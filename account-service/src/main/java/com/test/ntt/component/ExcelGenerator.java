package com.test.ntt.component;

import com.test.ntt.models.dtos.MovementsReportsDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelGenerator {

    public ByteArrayInputStream generateExcel(List<MovementsReportsDto> data) {
        String[] columns = {"Fecha", "Cliente", "Número Cuenta", "Tipo", "Saldo Inicial", "Estado", "Movimiento", "Saldo Disponible"};

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Movimientos");

            // 1. Crear Estilo para el Encabezado (Negrita)
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // 2. Crear Fila de Encabezados
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // 3. Llenar Datos
            int rowIdx = 1;
            for (MovementsReportsDto item : data) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(item.getDateAt().toString());
                row.createCell(1).setCellValue(item.getClient());
                row.createCell(2).setCellValue(item.getNumber());
                row.createCell(3).setCellValue(item.getAccountType());

                // Manejo de BigDecimal (convertir a double para que Excel lo entienda como número)
                row.createCell(4).setCellValue(item.getInitialBalance().doubleValue());

                row.createCell(5).setCellValue(item.getStatus() ? "Activo" : "Inactivo");

                row.createCell(6).setCellValue(item.getMovement().doubleValue());

                row.createCell(7).setCellValue(item.getCurrentBalance().doubleValue());
            }

            // 4. Ajustar tamaño de columnas automáticamente
            for(int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage());
        }
    }
}
