package org.h2.mvstore;

import org.junit.Assert;
import org.junit.Test;

public class MVStoreTest {

    @Test
    public void testSimple() {
        String fileName = "./documents/" + "test_db.db";
        try (MVStore s = new MVStore.Builder().fileName(fileName).autoCommitDisabled().open()) {
            MVMap<Integer, String> m = s.openMap("data");
            for (int i = 0; i < 3; i++) {
                m.put(i, "hello " + i);
            }
            s.commit();
            for (int i = 1; i < 3; i++) {
                Assert.assertEquals("hello " + i, m.get(i));
            }
        }
    }
}
