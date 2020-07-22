package org.apache.lucene.index;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TimTester {

    private String tmpDir = "/tmp";

    private IndexWriter writer;

    private FieldType type;

    @Before
    public void before() throws IOException {
        FileUtils.forceMkdir(new File(tmpDir + "/" + "lucene"));
        Directory dir = FSDirectory.open(Paths.get(tmpDir + "/" + "lucene"));
        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        config.setUseCompoundFile(false);
        config.setMaxBufferedDocs(2);
        this.writer = new IndexWriter(dir, config);
        //
        this.type = new FieldType();
        type.setStored(true);
        type.setTokenized(true);
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        type.setStoreTermVectors(true);
        type.setStoreTermVectorPositions(true);
        type.setStoreTermVectorOffsets(true);
        type.freeze();
    }

    @Test
    public void test() throws Throwable {
        final String[] docList = new String[]{
                "a b c",
                "a c c",
                "a c d",
                "a c e a",
                "a c e b",
                "a c e e",
                "a c e f",
                "a c e g"
        };
        for (final String str : docList) {
            Document doc = new Document();
            doc.add(new Field("content", str, type));
            writer.addDocument(doc);
        }
        writer.flush();
    }

    @After
    public void after() throws IOException {
        writer.close();
        FileUtils.deleteDirectory(new File(tmpDir + "/" + "lucene"));
    }
}
