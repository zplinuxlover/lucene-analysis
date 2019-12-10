package org.apache.lucene.document;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;

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
        type.freeze();
        //
        Document doc = new Document();
        doc.add(new Field("content", getFieldValue("a", 'a'), type));
        writer.addDocument(doc);
        //
        doc = new Document();
        doc.add(new Field("content", getFieldValue("ab", 'b'), type));
        writer.addDocument(doc);
        //
        doc = new Document();
        doc.add(new Field("content", getFieldValue("abc", 'c'), type));
        writer.addDocument(doc);

        writer.commit();
        writer.close();
    }

    private String getFieldValue(String strPrefix, char charPrefix) {
        List<String> vector = Lists.newArrayListWithCapacity(32);
        vector.add(String.format("%s%c", strPrefix, charPrefix));
        for (int t = 1; t < 26; ++t) {
            vector.add(String.format("%s%s%c", vector.get(t - 1), strPrefix, charPrefix + t));
        }
        String fieldValue = StringUtils.join(vector, "");
        return fieldValue;
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
