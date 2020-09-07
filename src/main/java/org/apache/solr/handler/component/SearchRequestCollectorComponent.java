package org.apache.solr.handler.component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * the component for collecting query
 */
public class SearchRequestCollectorComponent extends SearchComponent {

    private final Logger LOGGER = LoggerFactory.getLogger(SearchRequestCollectorComponent.class);

    public static final String DEBUG_PARAM_NAME = "debug";

    public static final String EXPERIMENT_PARAM_NAME = "experiment";

    private boolean debugEnabled = false;

    private final List<String> experiments = Lists.newArrayList();

    public void init(NamedList args) {
        Integer param = (Integer) args.get(DEBUG_PARAM_NAME);
        this.debugEnabled = param != null && param == 1;
        String expr = (String) args.get(EXPERIMENT_PARAM_NAME);
        if (expr != null) {
            for (final String str : StringUtils.split(expr, ",")) {
                experiments.add(StringUtils.trim(str));
            }
        }
        LOGGER.info("the query collector component is {} {}", this.debugEnabled, this.experiments);
    }

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (this.debugEnabled) {
            SolrParams solrParams = rb.req.getParams();
            String param = solrParams.get(CommonParams.DISTRIB, Boolean.TRUE.toString());
            if (StringUtils.equals(param, Boolean.TRUE.toString())) {
                final Map<String, String[]> searchParams = Maps.newHashMap();
                Iterator<Map.Entry<String, String[]>> it = solrParams.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String[]> entry = it.next();
                    searchParams.put(entry.getKey(), entry.getValue());
                }
                LOGGER.info("{}", JSON.toJSONString(searchParams));
            }
        }
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {

    }

    @Override
    public String getDescription() {
        return "query-collector";
    }
}
