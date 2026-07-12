package epam.course.appliance.brain.service;

import java.util.List;
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

    public void processPdfAndSave(@NotNull MultipartFile file) throws Exception {
        Resource resource = new ByteArrayResource(file.getBytes());

        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder().build();
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, config);
        List<Document> rawDocuments = pdfReader.get();

        rawDocuments.forEach(doc -> doc.getMetadata()
                .put("filename", file.getOriginalFilename()));

        List<Document> splitDocuments = textSplitter.apply(rawDocuments);
        vectorStore.add(splitDocuments);
    }
}
