package org.apache.lucene.util;

import org.apache.lucene.search.DocIdSetIterator;
import org.junit.Test;

public class FixedBitSetTester {

    @Test
    public void test() {
        FixedBitSet fixedBitSet = new FixedBitSet(300);
        fixedBitSet.set(10);
        fixedBitSet.set(11);
        fixedBitSet.set(32);
        int docId = -1;
        while ((docId = fixedBitSet.nextSetBit(docId + 1)) != DocIdSetIterator.NO_MORE_DOCS) {
            System.out.println(docId);
        }
    }

}
