package org.apache.lucene.index;

import com.google.common.collect.Sets;
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
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class BlockTreeTermsWriterTest {

    private String tmpDir = "/tmp";

    private IndexWriter writer;

    private FieldType type;

    @Before
    public void before() throws IOException {
        FileUtils.forceMkdir(new File(tmpDir + "/" + "lucene"));
        Directory dir = FSDirectory.open(Paths.get(tmpDir + "/" + "lucene"));
        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        config.setUseCompoundFile(false);
        config.setMaxBufferedDocs(10000);
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
    public void test_tim_tip() throws Throwable {
        final Set<String> docList = Sets.newTreeSet();
        IntStream.range(0, 26).forEach(t -> docList.add("A" + (char) ('a' + t)));
        IntStream.range(0, 26).forEach(t -> docList.add("A" + (char) ('A' + t)));
        IntStream.range(0, 10).forEach(t -> docList.add("A" + (char) ('0' + t)));
        docList.add("B");
        System.out.println(docList);
        for (final String str : docList) {
            Document doc = new Document();
            doc.add(new Field("content", str, type));
            writer.addDocument(doc);
        }
        writer.flush();
    }

    @Test
    public void test_2() throws Throwable {
        final Set<String> docList = Sets.newTreeSet();
        for (int t = 0; t < 32; ++t) {
            StringBuilder builder = new StringBuilder();
            builder.append("a").append(t < 26 ? (char) ('A' + t) : (char) ('a' + t - 26));
            docList.add(builder.toString());
        }
        for (int t = 0; t < 32; ++t) {
            StringBuilder builder = new StringBuilder();
            builder.append("b").append(t < 26 ? (char) ('A' + t) : (char) ('a' + t - 26));
            docList.add(builder.toString());
        }
        System.out.println(docList);
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
