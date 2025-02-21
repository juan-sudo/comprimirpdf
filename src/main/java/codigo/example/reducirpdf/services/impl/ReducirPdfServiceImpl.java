package codigo.example.reducirpdf.services.impl;

import codigo.example.reducirpdf.aggregates.ResponseBase;
import codigo.example.reducirpdf.services.ReducirPdfService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class ReducirPdfServiceImpl implements ReducirPdfService {

    @Override
    public ResponseBase reducirPDF(String inputPath, String outputPath, int calidad, float tamanio) {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);

        try (PDDocument document = PDDocument.load(inputFile);
             PDDocument compressedDocument = new PDDocument()) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                // Renderizar la página como imagen con la resolución deseada
                BufferedImage image = pdfRenderer.renderImageWithDPI(i, calidad);

                // Convertir la imagen a JPEG con la calidad especificada
                PDImageXObject compressedImage = JPEGFactory.createFromImage(compressedDocument, image, tamanio);

                // Crear una nueva página y agregar la imagen comprimida
                PDPage newPage = new PDPage(document.getPage(i).getMediaBox());
                compressedDocument.addPage(newPage);

                try (PDPageContentStream contentStream = new PDPageContentStream(compressedDocument, newPage)) {
                    contentStream.drawImage(compressedImage, 0, 0, newPage.getMediaBox().getWidth(), newPage.getMediaBox().getHeight());
                }
            }

            // Guardar el PDF comprimido
            compressedDocument.save(outputFile);

            // Reemplazar barras invertidas con barras normales
            String formattedPath = outputFile.getAbsolutePath().replace("\\", "/");

            return ResponseBase.builder()
                    .code(HttpStatus.OK.value())
                    .message("PDF comprimido exitosamente.")
                    .data(formattedPath)
                    .build();

        } catch (IOException e) {
            return ResponseBase.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error al procesar el PDF: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
