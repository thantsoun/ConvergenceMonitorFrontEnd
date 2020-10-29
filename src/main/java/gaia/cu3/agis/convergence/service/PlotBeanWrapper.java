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
import gaia.cu3.agis.plotting.PlotCategory;
import gaia.cu3.agis.web.PlotBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
@Service
public class PlotBeanWrapper {

    private final Logger log = LoggerFactory.getLogger(PlotBeanWrapper.class);
    
    private final PlotBean plotBean;

    public PlotBeanWrapper() {
        log.info("Initializing {}", PlotBeanWrapper.class);
        this.plotBean = new PlotBean();
    }

    public Properties getPastRunPropsObject() throws GaiaException {
        return plotBean.getPastRunPropsObject();
    }

    public int getIterationCount() throws Exception {
        return plotBean.getIterationCount();
    }

    public void setRetrievalMode(boolean retrievalMode) {
        plotBean.setRetrievalMode(retrievalMode);
    }

    public PlotCategory[] getConvergencePlotCategories() throws GaiaException {
        return plotBean.getConvergencePlotCategories();
    }
    
    public int getNparamSolved() throws GaiaException {
        return plotBean.getNparamSolved();
    }

    public void setRun(long currRunId) throws Exception {
        plotBean.setRun(currRunId);
    }
}
