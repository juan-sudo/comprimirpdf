package codigo.example.reducirpdf.services;

import codigo.example.reducirpdf.aggregates.ResponseBase;

public interface ReducirPdfService {
    ResponseBase reducirPDF(String inputPath, String outputPath, int dpi, float quality);
}
