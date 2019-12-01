package org.apache.lucene.document;

import org.apache.commons.io.FileUtils;
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

public class PostingsTester {

    @Before
    public void before() throws IOException {
        FileUtils.forceMkdir(new File("/data/logs/lucene"));
    }

    @Test
    public void test() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig();
        config.setUseCompoundFile(false);
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        IndexWriter writer = new IndexWriter(dir, config);
        PayloadAnalyzer analyzer = new PayloadAnalyzer();
        analyzer.setPayloadData("content", "hi".getBytes(StandardCharsets.UTF_8), 0, 2);
        FieldType type = new FieldType();
        type.setStored(true);
        type.setTokenized(true);
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        //
        Document doc = new Document();
        doc.add(new Field("content", "the book is ", type));
        doc.add(new Field("title", "book", type));
        writer.addDocument(doc);
        //
        doc = new Document();
        doc.add(new Field("content", "book", type));
        writer.addDocument(doc);

        writer.commit();
        writer.close();
    }

    private void addDocument(IndexWriter writer, long value) throws IOException {
        Document doc = new Document();
        doc.add(new SortedNumericDocValuesField("field", value));
        doc.add(new SortedNumericDocValuesField("field", value + 1));
        writer.addDocument(doc);
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

}
