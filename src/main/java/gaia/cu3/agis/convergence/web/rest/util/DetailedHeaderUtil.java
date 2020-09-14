
package gaia.cu3.agis.convergence.web.rest.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.List;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public final class DetailedHeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(DetailedHeaderUtil.class);

    private DetailedHeaderUtil() {
    }

    public static HttpHeaders createDetailedError(String applicationName, String errorMsg, String details) {
        return createDetailedError(applicationName, errorMsg, Collections.singletonList(details));
    }
    
    public static HttpHeaders createDetailedError(String applicationName, String errorMsg, List<String> details) {
        log.error("Processing failed, {}", errorMsg);
        log.error("Details, {}", details);
        HttpHeaders headers = new HttpHeaders();
        headers.add(applicationName + "-error-title", errorMsg);
        for (int i = 0; i < details.size(); i++) {
            headers.add(applicationName + "-error-details-" + StringUtils.leftPad(String.valueOf(i+1), 3, '0'), details.get(i));
        }
        return headers;
    }
}
