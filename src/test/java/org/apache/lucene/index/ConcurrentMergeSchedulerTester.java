package org.apache.lucene.index;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.PayloadAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class ConcurrentMergeSchedulerTester {

    @Before
    public void before() throws IOException {
        FileUtils.forceMkdir(new File("/data/logs/lucene"));
    }

    @Test
    public void test() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        PayloadAnalyzer analyzer = new PayloadAnalyzer();
        analyzer.setPayloadData("content", "one".getBytes(StandardCharsets.UTF_8), 0, 3);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setUseCompoundFile(false);
        IndexWriter writer = new IndexWriter(dir, config);
        //
        FieldType type = new FieldType();
        type.setStored(true);
        type.setTokenized(true);
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        type.setStoreTermVectors(true);
        type.setStoreTermVectorPositions(true);
        type.setStoreTermVectorOffsets(true);
        type.freeze();
        config.useCompoundFile = false;

        for (int segment = 0; segment < 10; ++segment) {
            for (int t = 0; t < 10000; ++t) {
                Document doc = new Document();
                StringBuilder builder = new StringBuilder();
                for (int k = 0; k < 20; ++k) {
                    builder.append(String.format("%d%d ", t, k));
                }
                doc.add(new Field("content", builder.toString(), type));
                writer.addDocument(doc);
                writer.commit();
            }
        }
        writer.close();
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

}
