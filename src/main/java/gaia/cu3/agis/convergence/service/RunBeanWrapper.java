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

package gaia.cu3.agis.convergence.service;

import gaia.cu1.tools.exception.GaiaException;
import gaia.cu3.agis.web.RunBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
@Service
public class RunBeanWrapper {

    private final Logger log = LoggerFactory.getLogger(RunBeanWrapper.class);
    
    private final RunBean runBean;

    public RunBeanWrapper() {
        log.info("Initializing {}", RunBeanWrapper.class);
        try {
            this.runBean = new RunBean();
        } catch (GaiaException e) {
            log.error("Error initialising the RunBean", e);
            throw new IllegalArgumentException("Error initialising the RunBean", e);
        }
    }
    
}
