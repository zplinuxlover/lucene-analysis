### lucene添加文档源码解析

```

        Directory dir = FSDirectory.open(Paths.get("/data/logs/lucene"));
        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        config.setUseCompoundFile(false);
        config.setMaxBufferedDocs(2);
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
        Document doc = new Document();
        doc.add(new Field("content", "one", type));
        writer.addDocument(doc);
        
```
