package org.apache.solr.schema;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.PayloadAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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

public class IntPointFieldTester {
    //
    org.apache.lucene.document.FieldType type;

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
    public void test() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        PayloadAnalyzer analyzer = new PayloadAnalyzer();
        analyzer.setPayloadData("content", "one".getBytes(StandardCharsets.UTF_8), 0, 3);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setUseCompoundFile(false);
        IndexWriter writer = new IndexWriter(dir, config);
        Document doc;
        for (int t = 0; t < 20480; ++t) {
            doc = new Document();
            doc.add(new IntPoint("field", t));
            writer.addDocument(doc);
        }
        writer.flush();
        writer.commit();
        //
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = IntPoint.newRangeQuery("field", 1, 5);
        TopDocs docs = searcher.search(query, 10);
        Assert.assertEquals(docs.totalHits, 5);
        reader.close();
    }


    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

}
