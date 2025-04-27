package codigo.example.reducirpdf.controllers;

import codigo.example.reducirpdf.aggregates.ResponseBase;
import codigo.example.reducirpdf.services.ReducirPdfService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;


import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ReducirPdfController {
    private final ReducirPdfService reducirPdfService;
    // Constructor manual
    public ReducirPdfController(ReducirPdfService reducirPdfService) {
        this.reducirPdfService = reducirPdfService;
    }



//
//    @Value("${file.upload-dir}")
//    private String uploadDir;


    //SUBIR ARCHIVO PDF
    @PostMapping("/subir-pdf")
    public ResponseBase subirPDF(@RequestPart("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) {
            return ResponseBase.builder()
                    .code(400)
                    .message("No se subió archivo")
                    .data(null)
                    .build();
        }

        try {
            // Generar un nombre único para el archivo
            String nombre = UUID.randomUUID() + "-" + archivo.getOriginalFilename();

            // Usar una ruta absoluta dentro del directorio del proyecto
            String uploadDir = System.getProperty("user.dir") + "/uploads";  // Ruta absoluta en el directorio del proyecto
            File destino = new File(uploadDir + File.separator + nombre);

            // Eliminar el archivo existente (ya que es el único archivo que se sube)
            File carpeta = new File(uploadDir);
            File[] archivosExistentes = carpeta.listFiles();
            if (archivosExistentes != null && archivosExistentes.length > 0) {
                for (File archivoExistente : archivosExistentes) {
                    if (archivoExistente.exists()) {
                        archivoExistente.delete();  // Eliminar el archivo anterior
                    }
                }
            }

            // Crear el directorio si no existe
            if (!destino.getParentFile().exists()) {
                destino.getParentFile().mkdirs();
            }

            // Transferir el archivo al destino
            archivo.transferTo(destino);

            // Devuelve la URL del archivo subido (URL completa)
            String fileUrl = "http://localhost:8080/uploads/" + nombre;
            return ResponseBase.builder()
                    .code(200)
                    .message("Archivo subido correctamente.")
                    .data(fileUrl) // Devuelve la URL completa del archivo subido
                    .build();

        } catch (IOException e) {
            return ResponseBase.builder()
                    .code(500)
                    .message("Error: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }


    @GetMapping("/compressed")
    @ResponseBody
    public ResponseEntity<Resource> getFirstFileCompressed() {
        String rutaBase = System.getProperty("user.dir");
        Path folderPath = Paths.get(rutaBase + File.separator + "compressed");

        // Verificar si el directorio existe
        File folder = folderPath.toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.status(500).body(null); // Error si no es un directorio válido
        }

        // Obtener todos los archivos de la carpeta
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return ResponseEntity.status(204).body(null); // No hay archivos, pero no es un error (204 No Content)
        }

        File file = files[0]; // Obtener el primer archivo
        if (!file.getName().endsWith(".pdf")) {
            return ResponseEntity.status(204).body(null); // Si el archivo no es PDF, devolver 204 No Content
        }

        try {
            Path filePath = file.toPath();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF) // Tipo de contenido PDF
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null); // El archivo no es legible
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // Error al acceder al archivo
        }
    }



//
//    @GetMapping("/compressed")
//    @ResponseBody
//    public ResponseEntity<Resource> getFirstFileCompressed() {
//        String rutaBase = System.getProperty("user.dir");
//        Path folderPath = Paths.get(rutaBase + File.separator + "compressed");
//
//        // Verificar si el directorio existe
//        File folder = folderPath.toFile();
//        if (!folder.exists() || !folder.isDirectory()) {
//            return ResponseEntity.status(500).body(null); // Error si no es un directorio válido
//        }
//
//        // Obtener todos los archivos de la carpeta
//        File[] files = folder.listFiles();
//        if (files == null || files.length == 0) {
//            return ResponseEntity.status(404).body(null); // No hay archivos
//        }
//
//        File file = files[0]; // Obtener el primer archivo
//        if (!file.getName().endsWith(".pdf")) {
//            return ResponseEntity.status(404).body(null); // Solo procesar PDFs
//        }
//
//        try {
//            Path filePath = file.toPath();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.APPLICATION_PDF) // Tipo de contenido PDF
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
//                        .body(resource);
//            } else {
//                return ResponseEntity.status(404).body(null); // El archivo no es legible
//            }
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body(null); // Error al acceder al archivo
//        }
//    }
//
//


    @GetMapping("/uploads")
    @ResponseBody
    public ResponseEntity<Resource> getFile() {
        // Obtener la ruta dinámica desde el directorio de trabajo del proyecto
        String rutaBase = System.getProperty("user.dir");
        Path folderPath = Paths.get(rutaBase + File.separator + "uploads");

        // Verificar si el directorio existe
        File folder = folderPath.toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.status(500).body(null); // Error si no es un directorio válido
        }

        // Obtener el primer archivo en la carpeta (suponiendo que solo hay uno)
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return ResponseEntity.status(404).body(null); // No hay archivos
        }

        File file = files[0]; // El primer archivo encontrado
        Path filePath = file.toPath();
        Resource resource = null;

        try {
            // Usar UrlResource para convertir el archivo en un recurso accesible
            resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Retornar el archivo PDF
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null); // Archivo no legible
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // Error al acceder al archivo
        }
    }




//
//    @GetMapping("/uploads")
//    @ResponseBody
//    public ResponseEntity<ResponseBase> serveFile() {
//        // Obtener la ruta dinámica desde el directorio de trabajo del proyecto
//        String rutaBase = System.getProperty("user.dir");
//        String uploadDir = "uploads"; // Define la carpeta de subida
//        Path folderPath = Paths.get(rutaBase + File.separator + uploadDir);
//
//        // Verificar si el directorio existe
//        File folder = folderPath.toFile();
//        if (!folder.exists() || !folder.isDirectory()) {
//            // Si no existe el directorio, retornar error 500
//            ResponseBase response = ResponseBase.builder()
//                    .code(500)
//                    .message("El directorio de subida no existe o no es válido.")
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(500).body(response);
//        }
//
//        // Buscar el primer archivo en el directorio (suponiendo que solo hay uno)
//        File[] files = folder.listFiles();
//        if (files == null || files.length == 0) {
//            // Si no hay archivos, devolver respuesta de error con código 404
//            ResponseBase response = ResponseBase.builder()
//                    .code(404)
//                    .message("No se encontraron archivos en el directorio.")
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(404).body(response); // No hay archivos
//        }
//
//        File file = files[0]; // Obtener el primer archivo encontrado
//        Resource resource = null;
//        String fileUrl = null;
//
//        try {
//            // Usar Paths.get para convertir el archivo a URI correctamente
//            Path filePath = file.toPath();
//            resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists() && resource.isReadable()) {
//                // Establecer la URL completa del archivo
//                fileUrl = filePath.toUri().toString();
//
//                // Crear la respuesta con el archivo y la URL
//                ResponseBase response = ResponseBase.builder()
//                        .code(200)
//                        .message("Archivo encontrado y listo para su descarga.")
//                        .data(fileUrl) // Aquí pasamos la URL del archivo como dato
//                        .build();
//
//                // Retornar solo la respuesta con la URL, no el archivo
//                return ResponseEntity.ok()
//                        .contentType(MediaType.APPLICATION_JSON)  // Tipo de contenido JSON para la respuesta
//                        .body(response);  // Aquí devolvemos el ResponseBase con la URL
//
//            } else {
//                throw new IOException("El archivo no es legible: " + file.getName());
//            }
//        } catch (IOException e) {
//            // Si ocurre un error, se captura y retorna un error detallado
//            ResponseBase response = ResponseBase.builder()
//                    .code(500)
//                    .message("Error al leer el archivo: " + e.getMessage())
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(500).body(response); // Error de lectura
//        }
//    }



//    @GetMapping("/uploads")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile() {
//        // Obtener la ruta dinámica desde el directorio de trabajo del proyecto
//        String rutaBase = System.getProperty("user.dir");
//        Path folderPath = Paths.get(rutaBase + File.separator + uploadDir);
//
//        // Buscar el primer archivo en el directorio (suponiendo que solo hay uno)
//        File folder = folderPath.toFile();
//        File[] files = folder.listFiles();
//
//        if (files == null || files.length == 0) {
//            return ResponseEntity.status(404).body(null); // No hay archivos
//        }
//
//        File file = files[0]; // Obtener el primer archivo encontrado
//        Resource resource = null;
//
//        try {
//            // Usar Paths.get para convertir el archivo a URI correctamente
//            Path filePath = file.toPath();
//            resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists() || resource.isReadable()) {
//                // Especificar Content-Type como PDF
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
//                        .contentType(MediaType.APPLICATION_PDF)  // Asegura que el archivo sea tratado como PDF
//                        .body(resource);
//            } else {
//                throw new IOException("No se puede leer el archivo: " + file.getName());
//            }
//        } catch (IOException e) {
//            return ResponseEntity.status(404).body(null); // Archivo no encontrado o no legible
//        }
//    }
//


    //MOSTRAR ARCHIVO PDF

    //@GetMapping("/uploads/{filename}")
    //@ResponseBody

//    @PostMapping("/subir-pdf")
//    public ResponseBase subirPDF(@RequestPart("archivo") MultipartFile archivo) {
//        if (archivo.isEmpty()) {
//            return ResponseBase.builder()
//                    .code(400)
//                    .message("No se subió archivo")
//                    .data(null)
//                    .build();
//        }
//
//        try {
//            String nombre = UUID.randomUUID() + "-" + archivo.getOriginalFilename();
//            String uploadDir = "C:/pdf_subidos/";
//            File destino = new File(uploadDir + nombre);
//            destino.getParentFile().mkdirs(); // Crea la carpeta si no existe
//            archivo.transferTo(destino);
//
//            return ResponseBase.builder()
//                    .code(200)
//                    .message("Archivo subido correctamente.")
//                    .data("/pdfs/" + nombre)
//                    .build();
//
//        } catch (IOException e) {
//            return ResponseBase.builder()
//                    .code(500)
//                    .message("Error: " + e.getMessage())
//                    .data(null)
//                    .build();
//        }
//    }



//
//
//    @PostMapping("/comprimir-pdf")
//    public ResponseBase compressPDF(
//            @RequestPart("archivo") MultipartFile archivo,
//            @RequestParam(defaultValue = "150") int calidad,
//            @RequestParam(defaultValue = "0.85") float tamanio) {
//
//        if (archivo.isEmpty()) {
//            return ResponseBase.builder()
//                    .code(400)
//                    .message("Error: No file uploaded.")
//                    .data(null)
//                    .build();
//        }
//
//        try {
//            // Guardar el archivo temporalmente
//            File inputFile = File.createTempFile("input-", ".pdf");
//            archivo.transferTo(inputFile);
//
//            // Archivo de salida temporal
//            File outputFile = File.createTempFile("output-", ".pdf");
//
//            // Llamar al servicio para comprimir el PDF
//            ResponseBase response = reducirPdfService.reducirPDF(
//                    inputFile.getAbsolutePath(),
//                    outputFile.getAbsolutePath(),
//                    calidad,
//                    tamanio
//            );
//
//            return response;
//
//        } catch (IOException e) {
//            return ResponseBase.builder()
//                    .code(500)
//                    .message("Error during PDF compression: " + e.getMessage())
//                    .data(null)
//                    .build();
//        }
//    }
//
//    // Comprimir archivo PDF
//    @PostMapping("/comprimir-pdf")
//    public ResponseBase compressPDF(
//            @RequestPart("archivo") MultipartFile archivo,
//            @RequestParam(defaultValue = "150") int calidad,
//            @RequestParam(defaultValue = "0.85") float tamanio) {
//
//        if (archivo.isEmpty()) {
//            return ResponseBase.builder()
//                    .code(400)
//                    .message("Error: No file uploaded.")
//                    .data(null)
//                    .build();
//        }
//
//        try {
//            // Guardar el archivo temporalmente
//            File inputFile = File.createTempFile("input-", ".pdf");
//            archivo.transferTo(inputFile);
//
//            // Crear la ruta de salida para el archivo comprimido en la carpeta "compressed"
//            String compressedDir = System.getProperty("user.dir") + "/compressed";  // Ruta para la carpeta comprimida
//            File outputFile = new File(compressedDir + File.separator + "comprimido-" + inputFile.getName());
//
//            // Crear el directorio si no existe
//            File compressedFolder = new File(compressedDir);
//            if (!compressedFolder.exists()) {
//                compressedFolder.mkdirs(); // Crear la carpeta "compressed" si no existe
//            }
//
//
//
//            // Llamar al servicio para comprimir el PDF
//            ResponseBase response = reducirPdfService.reducirPDF(
//                    inputFile.getAbsolutePath(),
//                    outputFile.getAbsolutePath(),
//                    calidad,
//                    tamanio
//            );
//
//            // URL del archivo comprimido
//            String compressedFileUrl = "http://localhost:8080/compressed/" + outputFile.getName();
//
//            // Verificar si el archivo ya existe y eliminarlo si es necesario
//            if (outputFile.exists()) {
//                boolean deleted = outputFile.delete(); // Eliminar el archivo existente
//                if (!deleted) {
//                    return ResponseBase.builder()
//                            .code(500)
//                            .message("Error: No se pudo reemplazar el archivo existente.")
//                            .data(null)
//                            .build();
//                }
//            }
//
//            return ResponseBase.builder()
//                    .code(HttpStatus.OK.value())
//                    .message("PDF comprimido exitosamente.")
//                    .data(compressedFileUrl) // Retornar la URL del archivo comprimido
//                    .build();
//
//        } catch (IOException e) {
//            return ResponseBase.builder()
//                    .code(500)
//                    .message("Error durante la compresión del PDF: " + e.getMessage())
//                    .data(null)
//                    .build();
//        }
//    }


//
//    @PostMapping("/comprimir-pdf")
//    public ResponseBase compressPDF(
//            @RequestPart("archivo") MultipartFile archivo,
//            @RequestParam(defaultValue = "150") int calidad,
//            @RequestParam(defaultValue = "0.85") float tamanio) {
//
//        if (archivo.isEmpty()) {
//            return ResponseBase.builder()
//                    .code(400)
//                    .message("Error: No file uploaded.")
//                    .data(null)
//                    .build();
//        }
//
//        try {
//            // Guardar el archivo temporalmente
//            File inputFile = File.createTempFile("input-", ".pdf");
//            archivo.transferTo(inputFile);
//
//            // Crear la ruta de salida para el archivo comprimido en la carpeta "compressed"
//            String compressedDir = System.getProperty("user.dir") + "/compressed";  // Ruta para la carpeta comprimida
//            File compressedFolder = new File(compressedDir);
//
//            // Crear la carpeta "compressed" si no existe
//            if (!compressedFolder.exists()) {
//                compressedFolder.mkdirs();
//            }
//
//            // Crear el archivo de salida, con un nombre basado en el archivo original
//            File outputFile = new File(compressedDir + File.separator + "comprimido-" + inputFile.getName());
//
//            // Verificar si el archivo ya existe y eliminarlo si es necesario
//            if (outputFile.exists()) {
//                boolean deleted = outputFile.delete(); // Eliminar el archivo existente
//                if (!deleted) {
//                    return ResponseBase.builder()
//                            .code(500)
//                            .message("Error: No se pudo reemplazar el archivo existente.")
//                            .data(null)
//                            .build();
//                }
//            }
//
//            // Llamar al servicio para comprimir el PDF
//            ResponseBase response = reducirPdfService.reducirPDF(
//                    inputFile.getAbsolutePath(),
//                    outputFile.getAbsolutePath(),
//                    calidad,
//                    tamanio
//            );
//
//            // URL del archivo comprimido
//            String compressedFileUrl = "http://localhost:8080/compressed/" + outputFile.getName();
//
//            return ResponseBase.builder()
//                    .code(HttpStatus.OK.value())
//                    .message("PDF comprimido exitosamente.")
//                    .data(compressedFileUrl) // Retornar la URL del archivo comprimido
//                    .build();
//
//        } catch (IOException e) {
//            return ResponseBase.builder()
//                    .code(500)
//                    .message("Error durante la compresión del PDF: " + e.getMessage())
//                    .data(null)
//                    .build();
//        }
//    }

//
//    @PostMapping("/comprimir-pdf")
//    public ResponseBase compressPDF(
//            @RequestPart("archivo") MultipartFile archivo,
//            @RequestParam(defaultValue = "150") int calidad,
//            @RequestParam(defaultValue = "0.85") float tamanio) {
//
//        if (archivo.isEmpty()) {
//            return ResponseBase.builder()
//                    .code(400)
//                    .message("Error: No file uploaded.")
//                    .data(null)
//                    .build();
//        }
//
//        try {
//            // Guardar el archivo temporalmente
//            File inputFile = File.createTempFile("input-", ".pdf");
//            archivo.transferTo(inputFile);
//
//            // Crear la ruta de salida para el archivo comprimido en la carpeta "compressed"
//            String compressedDir = System.getProperty("user.dir") + "/compressed";  // Ruta para la carpeta comprimida
//            File compressedFolder = new File(compressedDir);
//
//            // Crear la carpeta "compressed" si no existe
//            if (!compressedFolder.exists()) {
//                compressedFolder.mkdirs();
//            }
//
//            // Crear el archivo de salida, con un nombre basado en el archivo original
//            File outputFile = new File(compressedDir + File.separator + "comprimido-" + inputFile.getName());
//
//            // Verificar si el archivo ya existe y eliminarlo si es necesario
//            if (outputFile.exists()) {
//                boolean deleted = outputFile.delete(); // Eliminar el archivo existente
//                if (!deleted) {
//                    return ResponseBase.builder()
//                            .code(500)
//                            .message("Error: No se pudo reemplazar el archivo existente.")
//                            .data(null)
//                            .build();
//                }
//            }
//
//            // Llamar al servicio para comprimir el PDF
//            ResponseBase response = reducirPdfService.reducirPDF(
//                    inputFile.getAbsolutePath(),
//                    outputFile.getAbsolutePath(),
//                    calidad,
//                    tamanio
//            );
//
//            // URL del archivo comprimido
//            String compressedFileUrl = "http://localhost:8080/compressed/" + outputFile.getName();
//
//            return ResponseBase.builder()
//                    .code(HttpStatus.OK.value())
//                    .message("PDF comprimido exitosamente.")
//                    .data(compressedFileUrl) // Retornar la URL del archivo comprimido
//                    .build();
//
//        } catch (IOException e) {
//            return ResponseBase.builder()
//                    .code(500)
//                    .message("Error durante la compresión del PDF: " + e.getMessage())
//                    .data(null)
//                    .build();
//        }
//    }
//
//
//
//    @PostMapping("/comprimir-pdf")
//    public ResponseBase compressPDF(
//            @RequestPart("archivo") MultipartFile archivo,
//            @RequestParam(defaultValue = "150") int calidad,   // Calidad de las imágenes dentro del PDF (por ejemplo, resolución)
//            @RequestParam(defaultValue = "0.85") float tamanio) { // Tamaño objetivo (porcentaje de reducción del tamaño total)
//
//        if (archivo.isEmpty()) {
//            return ResponseBase.builder()
//                    .code(400)
//                    .message("Error: No file uploaded.")
//                    .data(null)
//                    .build();
//        }
//
//        try {
//            // Guardar el archivo temporalmente
//            File inputFile = File.createTempFile("input-", ".pdf");
//            archivo.transferTo(inputFile);
//
//            // Crear la ruta de salida para el archivo comprimido en la carpeta "compressed"
//            String compressedDir = System.getProperty("user.dir") + "/compressed";  // Ruta para la carpeta comprimida
//            File compressedFolder = new File(compressedDir);
//
//            // Crear la carpeta "compressed" si no existe
//            if (!compressedFolder.exists()) {
//                compressedFolder.mkdirs();
//            }
//
//            // Usar un nombre fijo para el archivo comprimido
//            File outputFile = new File(compressedDir + File.separator + "comprimido.pdf");
//
//            // Verificar si el archivo ya existe y eliminarlo si es necesario
//            if (outputFile.exists()) {
//                boolean deleted = outputFile.delete(); // Eliminar el archivo existente
//                if (!deleted) {
//                    return ResponseBase.builder()
//                            .code(500)
//                            .message("Error: No se pudo reemplazar el archivo existente.")
//                            .data(null)
//                            .build();
//                }
//            }
//
//            // Llamar al servicio para comprimir las imágenes dentro del PDF
//            reducirPdfService.comprimirImagenes(
//                    inputFile.getAbsolutePath(),
//                    outputFile.getAbsolutePath(),
//                    calidad,    // Este parámetro se usará para reducir la calidad de las imágenes
//                    tamanio     // Este parámetro se usará para reducir el tamaño del archivo PDF
//            );
//
//            // URL del archivo comprimido
//            String compressedFileUrl = "http://localhost:8080/compressed/" + outputFile.getName();
//
//            return ResponseBase.builder()
//                    .code(HttpStatus.OK.value())
//                    .message("PDF comprimido exitosamente.")
//                    .data(compressedFileUrl) // Retornar la URL del archivo comprimido
//                    .build();
//
//        } catch (IOException e) {
//            return ResponseBase.builder()
//                    .code(500)
//                    .message("Error durante la compresión del PDF: " + e.getMessage())
//                    .data(null)
//                    .build();
//        }
//    }
//


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

            // Crear la ruta de salida para el archivo comprimido en la carpeta "compressed"
            String compressedDir = System.getProperty("user.dir") + "/compressed";  // Ruta para la carpeta comprimida
            File compressedFolder = new File(compressedDir);

            // Crear la carpeta "compressed" si no existe
            if (!compressedFolder.exists()) {
                compressedFolder.mkdirs();
            }

            // Usar un nombre fijo para el archivo comprimido, para evitar crear múltiples archivos
            File outputFile = new File(compressedDir + File.separator + "comprimido.pdf");

            // Verificar si el archivo ya existe y eliminarlo si es necesario
            if (outputFile.exists()) {
                boolean deleted = outputFile.delete(); // Eliminar el archivo existente
                if (!deleted) {
                    return ResponseBase.builder()
                            .code(500)
                            .message("Error: No se pudo reemplazar el archivo existente.")
                            .data(null)
                            .build();
                }
            }

            // Llamar al servicio para comprimir el PDF
            ResponseBase response = reducirPdfService.reducirPDF(
                    inputFile.getAbsolutePath(),
                    outputFile.getAbsolutePath(),
                    calidad,
                    tamanio
            );

            // URL del archivo comprimido
            String compressedFileUrl = "http://localhost:8080/compressed/" + outputFile.getName();

            return ResponseBase.builder()
                    .code(HttpStatus.OK.value())
                    .message("PDF comprimido exitosamente.")
                    .data(compressedFileUrl) // Retornar la URL del archivo comprimido
                    .build();

        } catch (IOException e) {
            return ResponseBase.builder()
                    .code(500)
                    .message("Error durante la compresión del PDF: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }


    // ELIMINAR ARCHIVOS DE LAS CARPETAS compressed Y uploads
    @DeleteMapping("/eliminar-archivos")
    public ResponseBase eliminarArchivos() {
        String rutaBase = System.getProperty("user.dir");

        // Definir las rutas de las carpetas
        Path uploadsPath = Paths.get(rutaBase, "uploads");
        Path compressedPath = Paths.get(rutaBase, "compressed");

        try {
            eliminarArchivosDeCarpeta(uploadsPath);
            eliminarArchivosDeCarpeta(compressedPath);

            return ResponseBase.builder()
                    .code(200)
                    .message("Archivos eliminados correctamente de las carpetas 'uploads' y 'compressed'.")
                    .data(null)
                    .build();

        } catch (IOException e) {
            return ResponseBase.builder()
                    .code(500)
                    .message("Error eliminando archivos: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // Método auxiliar para eliminar archivos de una carpeta específica
    private void eliminarArchivosDeCarpeta(Path folderPath) throws IOException {
        File folder = folderPath.toFile();
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        file.delete();  // Eliminar cada archivo
                    }
                }
            }
        }
    }




}
