package org.apache.lucene.codecs;

import com.google.common.collect.Lists;
import org.apache.calcite.rel.core.Collect;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PointRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class PointsFormatTest {

    private String tmpDir = "/tmp";

    private IndexWriter writer;

    @Before
    public void setup() throws Throwable {
        FileUtils.forceMkdir(new File(tmpDir + "/" + "lucene"));
        Directory dir = FSDirectory.open(Paths.get(tmpDir + "/" + "lucene"));
        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        config.setUseCompoundFile(false);
        config.setMaxBufferedDocs(10000);
        this.writer = new IndexWriter(dir, config);
    }

    @Test
    public void test() throws Throwable {
        final List<Integer> points = Lists.newArrayList();
        IntStream.range(0, 3000).forEach(t -> points.add(t));
        Collections.shuffle(points);
        for (int point : points) {
            this.writer.addDocument(Lists.newArrayList(new IntPoint("age", point)));
        }
        writer.flush();
        DirectoryReader reader = StandardDirectoryReader.open(writer);
        IndexSearcher search = new IndexSearcher(reader);
        TopDocs docs = search.search(IntPoint.newRangeQuery("age", 0, 3000), 10000);
        reader.close();
        Assert.assertEquals(3000, docs.totalHits.value);
    }

    @After
    public void clean() throws Throwable {
        writer.close();
        FileUtils.deleteDirectory(new File(tmpDir + "/" + "lucene"));
    }
}
