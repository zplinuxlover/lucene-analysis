package org.apache.solr.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * solr servlet dispatcher filter
 */
public class SolrDispatchFilterExtend extends SolrDispatchFilter {

    public static Logger LOGGER = LoggerFactory.getLogger(SolrDispatchFilterExtend.class);

    @Override
    public void close() {
        LOGGER.info("solr begin to exit");
        super.close();
        LOGGER.info("solr exit end");
    }
}
