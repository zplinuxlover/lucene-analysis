package org.apache.lucene.util.fst;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.*;
import org.apache.lucene.util.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FSTTester {

    private String[] strings;
    //
    private IntsRef[] terms;

    @Before
    public void before() throws Throwable {
        FileUtils.forceMkdir(new File("/data/logs/lucene"));
        strings = new String[]{
                "station", "commotion", "elation", "elastic", "plastic",
                "stop", "ftop", "ftation", "stat"
        };
        terms = new IntsRef[strings.length];
        for (int idx = 0; idx < strings.length; idx++) {
            terms[idx] = toIntsRef(strings[idx], 0);
        }
        Arrays.sort(terms);
    }

    @Test
    public void test_fsa() throws Throwable {
        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        final Outputs<Object> outputs = NoOutputs.getSingleton();
        final Object NO_OUTPUT = outputs.getNoOutput();
        final List<FSTTester.InputOutput<Object>> pairs = new ArrayList<>(terms.length);
        for (IntsRef term : terms) {
            pairs.add(new FSTTester.InputOutput<>(term, NO_OUTPUT));
        }
        new FstCreator(dir, 0, pairs, outputs).create();
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File("/data/logs/lucene"));
    }

    public static class FstCreator<T> {

        final List<InputOutput<T>> pairs;

        final int inputMode;

        final Outputs<T> outputs;

        final Directory dir;

        public FstCreator(Directory dir, int inputMode, List<InputOutput<T>> pairs, Outputs<T> outputs) {
            this.dir = dir;
            this.inputMode = inputMode;
            this.pairs = pairs;
            this.outputs = outputs;
        }

        public void create() throws IOException {
            final Builder<T> builder = new Builder<>(FST.INPUT_TYPE.BYTE1, 0, 0, true,
                    true, Integer.MAX_VALUE, outputs, true, 15);
            for (InputOutput<T> pair : pairs) {
                builder.add(pair.input, pair.output);
            }
            FST<T> fst = builder.finish();
            Random random = new Random();
            IOContext context = LuceneTestCase.newIOContext(random);
            IndexOutput out = dir.createOutput("fst.bin", context);
            fst.save(out);
            out.close();
            IndexInput in = dir.openInput("fst.bin", context);
            try {
                new FST<>(in, outputs);
            } finally {
                in.close();
                dir.deleteFile("fst.bin");
            }
        }
    }

    /**
     * Holds one input/output pair.
     */
    public static class InputOutput<T> implements Comparable<InputOutput<T>> {
        public final IntsRef input;
        public final T output;

        public InputOutput(IntsRef input, T output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public int compareTo(InputOutput<T> other) {
            if (other instanceof InputOutput) {
                return input.compareTo((other).input);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    static IntsRef toIntsRef(String s, int inputMode) {
        return toIntsRef(s, inputMode, new IntsRefBuilder());
    }

    static IntsRef toIntsRef(String s, int inputMode, IntsRefBuilder ir) {
        if (inputMode == 0) {
            // utf8
            return toIntsRef(new BytesRef(s), ir);
        } else {
            // utf32
            return toIntsRefUTF32(s, ir);
        }
    }

    static IntsRef toIntsRefUTF32(String s, IntsRefBuilder ir) {
        final int charLength = s.length();
        int charIdx = 0;
        int intIdx = 0;
        ir.clear();
        while (charIdx < charLength) {
            ir.grow(intIdx + 1);
            final int utf32 = s.codePointAt(charIdx);
            ir.append(utf32);
            charIdx += Character.charCount(utf32);
            intIdx++;
        }
        return ir.get();
    }

    static IntsRef toIntsRef(BytesRef br, IntsRefBuilder ir) {
        ir.grow(br.length);
        ir.clear();
        for (int i = 0; i < br.length; i++) {
            ir.append(br.bytes[br.offset + i] & 0xFF);
        }
        return ir.get();
    }

}
