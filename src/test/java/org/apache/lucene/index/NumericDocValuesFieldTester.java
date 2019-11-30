package org.apache.lucene.index;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class NumericDocValuesFieldTester {

    @Before
    public void before() throws IOException {
        FileUtils.forceMkdir(new File("/data/logs/lucene"));
    }

    @Test
    public void test() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig();
        config.useCompoundFile = false;
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        IndexWriter writer = new IndexWriter(dir, config);
        for (int t = 0; t < 64; ++t) {
            addDocument(writer, t);
        }
        writer.commit();
        writer.close();
    }

    private void addDocument(IndexWriter writer, long value) throws IOException {
        Document doc = new Document();
        doc.add(new NumericDocValuesField("field", value));
        writer.addDocument(doc);
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

}
