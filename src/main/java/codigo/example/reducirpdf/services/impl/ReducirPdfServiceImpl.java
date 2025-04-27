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
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfArray;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.Iterator;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

@Service
public class ReducirPdfServiceImpl implements ReducirPdfService {

    public void comprimirImagenes(String inputPdfPath, String outputPdfPath, int calidad, float tamanio) {
        try {
            // Abrir el archivo PDF de entrada
            PdfReader reader = new PdfReader(inputPdfPath);
            FileOutputStream os = new FileOutputStream(outputPdfPath);
            PdfStamper stamper = new PdfStamper(reader, os);

            // Comprimir las imágenes dentro del PDF
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                PdfDictionary pageDict = reader.getPageN(i);
                PdfDictionary resources = (PdfDictionary) pageDict.get(PdfName.RESOURCES);
                if (resources != null) {
                    // Accede a los objetos XObject (que contienen las imágenes)
                    PdfDictionary xObject = (PdfDictionary) resources.get(PdfName.XOBJECT);
                    if (xObject != null) {
                        for (PdfName name : xObject.getKeys()) {
                            PdfObject obj = xObject.get(name);
                            if (obj.isStream()) {
                                PRStream stream = (PRStream) obj;
                                byte[] imgBytes = stream.getBytes();

                                // Aquí debes implementar la compresión de la imagen
                                byte[] compressedImg = compressImage(imgBytes, calidad); // Implementa esta función

                                // Reemplazar los datos de la imagen con los comprimidos
                                stream.setData(compressedImg);
                            }
                        }
                    }
                }
            }

            stamper.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Método ficticio para comprimir la imagen
    // Método para comprimir las imágenes
    private byte[] compressImage(byte[] imgBytes, int calidad) {
        // Implementa la lógica para comprimir las imágenes usando un parámetro de calidad
        // Por ejemplo, utilizando ImageIO o alguna librería de compresión de imágenes
        // Puedes usar el formato JPEG y controlar la calidad aquí

        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Crear un writer de imágenes JPG
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            // Ajustar el parámetro de calidad
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(calidad / 100.0f); // Ajusta la calidad según el parámetro

            // Guardar la imagen comprimida
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new javax.imageio.IIOImage(bufferedImage, null, null), param);
            writer.dispose();

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return imgBytes;  // Si ocurre un error, devolvemos los bytes originales
        }
    }



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
