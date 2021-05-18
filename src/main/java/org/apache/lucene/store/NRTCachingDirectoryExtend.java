package org.apache.lucene.store;

public class NRTCachingDirectoryExtend extends NRTCachingDirectory {

    /**
     * We will cache a newly created output if 1) it's a
     * flush or a merge and the estimated size of the merged segment is
     * {@code <= maxMergeSizeMB}, and 2) the total cached bytes is
     * {@code <= maxCachedMB}
     */
    public NRTCachingDirectoryExtend(Directory delegate, double maxMergeSizeMB, double maxCachedMB) {
        super(delegate, maxMergeSizeMB, maxCachedMB);
    }

    protected boolean doCacheWrite(String name, IOContext context) {
        if (name != null && name.endsWith(".liv")) {
            return false;
        }
        return super.doCacheWrite(name, context);
    }
}
