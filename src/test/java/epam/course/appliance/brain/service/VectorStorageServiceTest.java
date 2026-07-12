package epam.course.appliance.brain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class VectorStorageServiceTest {

    @Mock
    private ChromaVectorStore vectorStore;

    @Mock
    private TokenTextSplitter textSplitter;

    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<List<Document>> documentsCaptor;

    private VectorStorageService vectorStorageService;

    @BeforeEach
    void setUp() {
        vectorStorageService = new VectorStorageService(vectorStore, textSplitter);
    }

    @Test
    void processPdfAndSaveSuccessEnrichesMetadataAndSavesToVectorStore() throws Exception {
        String filename = "sample-appliance-doc.pdf";
        byte[] dummyBytes = new byte[]{1, 2, 3};
        when(multipartFile.getBytes()).thenReturn(dummyBytes);
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        List<Document> rawDocsFromReader = new ArrayList<>();
        rawDocsFromReader.add(new Document("Page 1 Content"));
        List<Document> expectedSplitDocs = List.of(
                new Document("Split Content Chunk 1", Map.of("filename", filename))
        );
        when(textSplitter.apply(anyList())).thenReturn(expectedSplitDocs);
        try (MockedConstruction<PagePdfDocumentReader> mockedReader = Mockito.mockConstruction(
                PagePdfDocumentReader.class,
                (mock, context) -> {
                    when(mock.get()).thenReturn(rawDocsFromReader);
                })) {

            vectorStorageService.processPdfAndSave(multipartFile);

            ArgumentCaptor<List<Document>> rawDocsCaptor = ArgumentCaptor.forClass(List.class);
            verify(textSplitter).apply(rawDocsCaptor.capture());
            List<Document> capturedRawDocs = rawDocsCaptor.getValue();
            assertEquals(1, capturedRawDocs.size());
            assertEquals(filename, capturedRawDocs.getFirst().getMetadata().get("filename"));
            verify(vectorStore).add(documentsCaptor.capture());
            List<Document> capturedSavedDocs = documentsCaptor.getValue();
            assertNotNull(capturedSavedDocs);
            assertEquals(1, capturedSavedDocs.size());
            assertEquals(filename, capturedSavedDocs.getFirst().getMetadata().get("filename"));
        }
    }

    @Test
    void processPdfAndSaveIOExceptionThrownPropagatesException() throws Exception {
        when(multipartFile.getBytes()).thenThrow(new IOException("Disk read failure"));

        assertThrows(IOException.class, () -> {
            vectorStorageService.processPdfAndSave(multipartFile);
        });

        verifyNoInteractions(textSplitter);
        verifyNoInteractions(vectorStore);
    }
}