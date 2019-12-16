package org.apache.lucene.query;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.PayloadAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class TermQueryTester {

    @Before
    public void before() throws IOException {
        FileUtils.forceMkdir(new File("/data/logs/lucene"));
    }

    @Test
    public void test() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        PayloadAnalyzer analyzer = new PayloadAnalyzer();
        analyzer.setPayloadData("content", "hi".getBytes(StandardCharsets.UTF_8), 0, 2);
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
        //
        Document doc;
        //
        doc = new Document();
        doc.add(new Field("content", "one", type));
        writer.addDocument(doc);
        //
        doc = new Document();
        doc.add(new Field("content", "two", type));
        writer.addDocument(doc);
        //
        doc = new Document();
        doc.add(new Field("content", "three", type));
        writer.addDocument(doc);
        //
        writer.commit();
        //
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(new TermQuery(new Term("content", "two")), 10);
        Assert.assertEquals(docs.totalHits, 1);
        reader.close();
        //
        writer.close();
    }

    @Test
    public void test_stored_fields() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        PayloadAnalyzer analyzer = new PayloadAnalyzer();
        analyzer.setPayloadData("content", "hi".getBytes(StandardCharsets.UTF_8), 0, 2);
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

        for (int t = 0; t <= 128; ++t) {
            Document doc = new Document();
            doc.add(new Field("content", String.format("%d", t), type));
            writer.addDocument(doc);
        }

        writer.commit();

        writer.close();
    }

    @Test
    public void test_doc_nvd_nvm_format() throws IOException {
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

        for (int t = 0; t <= 128; ++t) {
            Document doc = new Document();
            doc.add(new Field("content", "one two one ", type));
            writer.addDocument(doc);
        }

        writer.commit();

        writer.close();
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

}
