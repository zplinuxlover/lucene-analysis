package org.apache.lucene.index;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class SortedDocValuesFieldTester {

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
            addDocument(writer, String.format("value-%d", t % 32));
        }
        writer.commit();
        writer.close();
    }

    private void addDocument(IndexWriter writer, String value) throws IOException {
        Document doc = new Document();
        doc.add(new SortedDocValuesField("field", new BytesRef(value)));
        writer.addDocument(doc);
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

}
