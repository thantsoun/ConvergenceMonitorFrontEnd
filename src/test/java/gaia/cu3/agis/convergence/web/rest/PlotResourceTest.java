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

import gaia.cu3.agis.convergence.service.IterationBeanWrapper;
import gaia.cu3.agis.convergence.service.PlotBeanWrapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class PlotResourceTest {

    @Inject
    private HttpMessageConverter<?>[] httpMessageConverters;
    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @MockBean
    private PlotBeanWrapper plotBeanWrapper;
    @MockBean
    private IterationBeanWrapper iterationBeanWrapper;
    
    private MockMvc mockMvcPlotController;
    
    @BeforeEach
    void init() throws Exception {
//        Mockito.when(plotBeanWrapper.getIterationCount()).thenReturn(1);
        PlotResource plotResource = new PlotResource(plotBeanWrapper, iterationBeanWrapper);
        this.mockMvcPlotController = MockMvcBuilders.standaloneSetup(plotResource)
                .setMessageConverters(httpMessageConverters)
                .build();
    }

//    @Test
//    void testIterCount() throws Exception {
//        MockHttpServletResponse response = mockMvcPlotController.perform(get("/api/iterationCount"))
//                .andExpect(status().isOk()).andReturn().getResponse();
//        int iterationCount = (Integer) jacksonMessageConverter.read(Integer.class, new MockHttpInputMessage(response.getContentAsByteArray()));
//        Assert.assertEquals(1, iterationCount);
//    }

    @Test
    void dummyEndpoint() throws Exception {
        mockMvcPlotController.perform(get("/api/dummy"))
                .andExpect(status().isNotFound());
    }


}
