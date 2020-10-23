package org.apache.solr.handler.admin;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/**
 * the extended class for /admin/collection api
 */
public class CollectionsHandlerExtend extends CollectionsHandler {

    public CollectionsHandlerExtend() {
        super();
    }

    public CollectionsHandlerExtend(CoreContainer coreContainer) {
        super(coreContainer);
    }

    @Override
    public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
        super.handleRequest(req, rsp);
    }

    @Override
    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
        super.handleRequestBody(req, rsp);
    }
}
