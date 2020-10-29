/*
 * Astrometric Global Iterative Solution (AGIS)
 * Copyright (C) 2006-2011 Gaia Data Processing and Analysis Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package gaia.cu3.agis.convergence.web.rest;

import gaia.cu3.agis.convergence.service.PlotBeanWrapper;
import gaia.cu3.agis.convergence.web.rest.util.DetailedHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
@RestController
@RequestMapping("/api")
public class PropsResource {

    private final Logger log = LoggerFactory.getLogger(PropsResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlotBeanWrapper plotBeanWrapper;

    public PropsResource(PlotBeanWrapper plotBeanWrapper) {
        log.info("Initializing {}", PropsResource.class);
        this.plotBeanWrapper = plotBeanWrapper;
    }
    
    @GetMapping("/properties")
    public ResponseEntity<Map<String, String>> getProperties() {
        try {
            Map<String, String> propsMap = new TreeMap<>();
            Properties props = plotBeanWrapper.getPastRunPropsObject();
            Set<Map.Entry<Object, Object>> entries = props.entrySet();
            String cu3AgisPrefix = "gaia.cu3.agis";
            String cu1ToolsPrefix = "gaia.cu1.tools";
            String cu1MdbPrefix = "gaia.cu1.mdb.cu1";
            String javaPrefix = "java";
            propsMap.put("user.name", System.getProperty("user.name"));
            for (Map.Entry<Object, Object> entry : entries) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.startsWith(cu3AgisPrefix) || key.startsWith(cu1ToolsPrefix) || key.startsWith(cu1MdbPrefix) || key.startsWith(javaPrefix)) {
                    propsMap.put(key, key.toLowerCase().contains("password") ? "########" : value);
                }
            }
            return ResponseEntity.ok(propsMap);
        } catch (Exception ex) {
            log.error("Error getting the properties", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the properties", ex.getMessage()))
                    .build();
        }
    }
}