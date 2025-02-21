package codigo.example.reducirpdf.controllers;

import codigo.example.reducirpdf.aggregates.ResponseBase;
import codigo.example.reducirpdf.services.ReducirPdfService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class ReducirPdfController {
    private final ReducirPdfService reducirPdfService;
    // Constructor manual
    public ReducirPdfController(ReducirPdfService reducirPdfService) {
        this.reducirPdfService = reducirPdfService;
    }

    @PostMapping("/comprimir-pdf")
    public ResponseBase compressPDF(
            @RequestPart("archivo") MultipartFile archivo,
            @RequestParam(defaultValue = "150") int calidad,
            @RequestParam(defaultValue = "0.85") float tamanio) {

        if (archivo.isEmpty()) {
            return ResponseBase.builder()
                    .code(400)
                    .message("Error: No file uploaded.")
                    .data(null)
                    .build();
        }

        try {
            // Guardar el archivo temporalmente
            File inputFile = File.createTempFile("input-", ".pdf");
            archivo.transferTo(inputFile);

            // Archivo de salida temporal
            File outputFile = File.createTempFile("output-", ".pdf");

            // Llamar al servicio para comprimir el PDF
            ResponseBase response = reducirPdfService.reducirPDF(
                    inputFile.getAbsolutePath(),
                    outputFile.getAbsolutePath(),
                    calidad,
                    tamanio
            );

            return response;

        } catch (IOException e) {
            return ResponseBase.builder()
                    .code(500)
                    .message("Error during PDF compression: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
