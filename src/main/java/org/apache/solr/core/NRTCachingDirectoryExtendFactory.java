package org.apache.solr.core;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.NRTCachingDirectoryExtend;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;

import java.io.File;
import java.io.IOException;

/**
 * Factory to instantiate {@link org.apache.lucene.store.NRTCachingDirectory}
 */
public class NRTCachingDirectoryExtendFactory extends StandardDirectoryFactory {

    public static final int DEFAULT_MAX_MERGE_SIZE_MB = 4;
    private double maxMergeSizeMB = DEFAULT_MAX_MERGE_SIZE_MB;
    public static final int DEFAULT_MAX_CACHED_MB = 48;
    private double maxCachedMB = DEFAULT_MAX_CACHED_MB;

    @Override
    @SuppressWarnings({"rawtypes"})
    public void init(NamedList args) {
        super.init(args);
        SolrParams params = args.toSolrParams();
        maxMergeSizeMB = params.getDouble("maxMergeSizeMB", DEFAULT_MAX_MERGE_SIZE_MB);
        if (maxMergeSizeMB <= 0) {
            throw new IllegalArgumentException("maxMergeSizeMB must be greater than 0");
        }
        maxCachedMB = params.getDouble("maxCachedMB", DEFAULT_MAX_CACHED_MB);
        if (maxCachedMB <= 0) {
            throw new IllegalArgumentException("maxCachedMB must be greater than 0");
        }
    }

    @Override
    protected Directory create(String path, LockFactory lockFactory, DirContext dirContext) throws IOException {
        // we pass NoLockFactory, because the real lock factory is set later by injectLockFactory:
        return new NRTCachingDirectoryExtend(FSDirectory.open(new File(path).toPath(), lockFactory), maxMergeSizeMB, maxCachedMB);
    }

    @Override
    public boolean isAbsolute(String path) {
        return new File(path).isAbsolute();
    }

}
