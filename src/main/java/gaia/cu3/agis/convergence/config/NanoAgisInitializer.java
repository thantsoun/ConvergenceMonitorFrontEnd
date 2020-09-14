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

package gaia.cu3.agis.convergence.config;

import gaia.cu1.tools.util.SysUtils;
import gaia.cu3.agis.progs.LaunchPropertyManager;
import gaia.cu3.agis.util.AgisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

//
///**
// * $Author$
// * $Date$
// * $Id$
// * $Rev$
// */
@Configuration
public class NanoAgisInitializer {

    private final Logger log = LoggerFactory.getLogger(NanoAgisInitializer.class);

    @PostConstruct
    private void init() {
        log.info("Initializing {}", NanoAgisInitializer.class);
        try {
            AgisUtils.setTestMode(true);
            AgisUtils.setSimMode(false);
            AgisUtils.setNanoAgisMode(true);
            AgisUtils.setJmsEnabled(false);

            String[] defaultProps = new String[]{
                    "gaiatools.properties",
                    "agis.properties",
                    "agistools.properties",
                    "agistest.properties",
                    "nanoagis.properties"
            };
            String[] runProps = new String[]{
                    "agisrun.properties",
                    "agisrun-" + SysUtils.getHostname() + ".properties"
            };

            LaunchPropertyManager propManager = new LaunchPropertyManager();
            propManager.loadRunProperties(true, defaultProps, runProps, new String[] {});
        } catch (Exception exception) {
            log.error("Failed Initializing NanoAgis Specific properties", exception);
            throw new IllegalStateException("Failed Initializing NanoAgis Specific properties", exception);
        }
    }
}