package gaia.cu3.agis.convergence.domain;

import gaia.cu1.tools.satellite.calibration.astro.CalibrationEffect;
import gaia.cu1.tools.satellite.calibration.astro.hypercube.AstroCalItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class CalibrationEffectInfo {
    
    public final String name;
    public final int id;
    public final String description;
    public final List<CalibrationEffectInfo> functions = new ArrayList<>();
    
    public CalibrationEffectInfo(CalibrationEffect calibrationEffect) {
        this((AstroCalItem) calibrationEffect);
        Arrays.stream(calibrationEffect.getCalibrationFunctions())
        .map(calFun -> (AstroCalItem) calFun)
        .map(CalibrationEffectInfo::new)
        .forEach(functions::add);
    }

    public CalibrationEffectInfo(AstroCalItem astroCalItem) {
        this.name = astroCalItem.getName();
        this.id = astroCalItem.getId();
        this.description = astroCalItem.getDescription();
    }
    
}
