package epam.course.appliance.brain.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for processing PDF files and saving them to a vector store.
 */
@Service
public class VectorStorageService {

    private final ChromaVectorStore vectorStore;
    private final TokenTextSplitter textSplitter;

    public VectorStorageService(ChromaVectorStore vectorStore, TokenTextSplitter textSplitter) {
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
    }

    public void processPdfAndSave(@NotNull MultipartFile file,
                                  String category,
                                  String serialNumber,
                                  String modelNumber) throws Exception {
        Resource resource = new ByteArrayResource(file.getBytes());

        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder().build();
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, config);
        List<Document> rawDocuments = pdfReader.get();

        List<Document> documents = rawDocuments.stream().map(doc -> {
            String enrichedText = String.format("Appliance: %s\n Serial Number: %s\n Model Number: %s\n %s",
                    category, serialNumber, modelNumber, doc.getText());
            return new Document(enrichedText, Map.of(
                    "appliance", category,
                    "serial-number", serialNumber,
                    "model-number", modelNumber,
                    "filename", Objects.requireNonNull(file.getOriginalFilename())
            ));
        }).toList();

        List<Document> splitDocuments = textSplitter.apply(documents);
        vectorStore.add(splitDocuments);
    }
}
