package org.apache.lucene.util.fst;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.PayloadAnalyzer;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FSTTester {

    private FieldType type;

    @Before
    public void before() throws IOException {
        FileUtils.forceMkdir(new File("/data/logs/lucene"));
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
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        PayloadAnalyzer analyzer = new PayloadAnalyzer();
        analyzer.setPayloadData("content", "one".getBytes(StandardCharsets.UTF_8), 0, 3);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setUseCompoundFile(false);
        IndexWriter writer = new IndexWriter(dir, config);
        String[] terms = new String[]{
                "mop", "moth", "pop", "star", "stop", "top"
        };
        Document doc;
        for (final String term : terms) {
            doc = new Document();
            doc.add(new Field("content", term, type));
            writer.addDocument(doc);
        }
        writer.flush();
        writer.commit();
        writer.close();
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

}
