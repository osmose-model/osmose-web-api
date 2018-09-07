package fr.ird.osmose.web.api;

import org.apache.commons.lang3.RandomUtils;
import ucar.ma2.ArrayFloat;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class LtlBiomassUtil {
    public static final double[] DEFAULT_MONTHS = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0};
    public static final double[] DEFAULT_LONGITUDES = new double[]{-86.91, -86.73005, -86.5501, -86.370155, -86.19021, -86.01026, -85.830315, -85.65037, -85.47042, -85.290474, -85.11053, -84.93058, -84.75063, -84.57069, -84.39074, -84.21079, -84.030846, -83.85089, -83.670944, -83.491, -83.31105, -83.1311, -82.95116, -82.77121, -82.59126, -82.411316, -82.23137, -82.05142, -81.871475, -81.69153, -81.51158, -81.331635, -81.15169, -80.97173, -80.79179, -80.61184, -80.43189, -80.251945, -80.072};
    public static final double[] DEFAULT_LATITUDES = new double[]{25.151, 25.330969, 25.510937, 25.690907, 25.870874, 26.050844, 26.230812, 26.410782, 26.59075, 26.77072, 26.950687, 27.130657, 27.310625, 27.490593, 27.670563, 27.85053, 28.0305, 28.210468, 28.390438, 28.570406, 28.750376, 28.930344, 29.110313, 29.290281, 29.47025, 29.650219, 29.830187, 30.010157, 30.190125, 30.370094, 30.550062, 30.730032, 30.91};

    public static void generateLtlBiomassNC(File ncFile, int numberOfBackgroundGroups) throws IOException, InvalidRangeException {
        if (numberOfBackgroundGroups <= 0) {
            throw new IOException("must defined at least one background group");
        }

        NetcdfFileWriter writer = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, ncFile.getAbsolutePath(), null);

        final Dimension dimLtl = writer.addDimension(null, "ltl", numberOfBackgroundGroups);
        final Dimension dimNx = writer.addDimension(null, "nx", 39);
        final Dimension dimNy = writer.addDimension(null, "ny", 33);
        final Dimension dimTime = writer.addDimension(null, "time", 12);

        final Variable time = writer.addVariable(null, "time", DataType.FLOAT, Collections.singletonList(dimTime));
        time.addAttribute(new Attribute("units", "months"));
        time.addAttribute(new Attribute("description", "plankton biomass in osmose cell, in tons integrated on water column, per group and per cell"));

        final Variable ltlBiomass = writer.addVariable(null, "ltl_biomass", DataType.FLOAT, Arrays.asList(dimTime, dimLtl, dimNy, dimNx));
        ltlBiomass.addAttribute(new Attribute("units", "tons per cell"));

        final Variable latitude = writer.addVariable(null, "latitude", DataType.FLOAT, Arrays.asList(dimNy, dimNx));
        latitude.addAttribute(new Attribute("units", "degree"));
        latitude.addAttribute(new Attribute("description", "latitude of the center of the cell"));

        final Variable longitude = writer.addVariable(null, "longitude", DataType.FLOAT, Arrays.asList(dimNy, dimNx));
        longitude.addAttribute(new Attribute("units", "degree"));
        longitude.addAttribute(new Attribute("description", "longitude of the center of the cell"));

        writer.create();

        writeMonths(writer, time);
        writeLatitudes(writer, latitude);
        writeLongitudes(writer, longitude);
        writeLtlBiomass(writer, ltlBiomass);

        writer.close();
    }

    private static void writeLtlBiomass(NetcdfFileWriter writer, Variable ltlBiomass) throws IOException, InvalidRangeException {
        final ArrayFloat tonsPerCell = new ArrayFloat(ltlBiomass.getShape());
        final Index index = tonsPerCell.getIndex();
        for (int month = 0; month < ltlBiomass.getShape(0); month++) {
            for (int ltl = 0; ltl < ltlBiomass.getShape(1); ltl++) {
                for (int lat = 0; lat < ltlBiomass.getShape()[2]; lat++) {
                    for (int lng = 0; lng < ltlBiomass.getShape()[3]; lng++) {
                        tonsPerCell.setFloat(index.set(month, ltl, lat, lng), RandomUtils.nextFloat(0, 1));
                    }
                }
            }
        }
        writer.write(ltlBiomass, tonsPerCell);
    }

    private static void writeLongitudes(NetcdfFileWriter writer, Variable longitude) throws IOException, InvalidRangeException {
        final ArrayFloat longitudes = new ArrayFloat(longitude.getShape());
        final Index index = longitudes.getIndex();
        for (int lat = 0; lat < longitude.getShape(0); lat++) {
            for (int lng = 0; lng < longitude.getShape(1); lng++) {
                longitudes.setFloat(index.set(lat, lng), (float) DEFAULT_LONGITUDES[lng]);
            }
        }
        writer.write(longitude, longitudes);
    }

    private static void writeLatitudes(NetcdfFileWriter writer, Variable latitude) throws IOException, InvalidRangeException {
        final ArrayFloat latitudes = new ArrayFloat(latitude.getShape());
        final Index index = latitudes.getIndex();
        for (int lat = 0; lat < latitude.getShape(0); lat++) {
            for (int lng = 0; lng < latitude.getShape(1); lng++) {
                latitudes.setFloat(index.set(lat, lng), (float) DEFAULT_LATITUDES[lat]);
            }
        }
        writer.write(latitude, latitudes);
    }

    public static void writeMonths(NetcdfFileWriter writer, Variable time) throws IOException, InvalidRangeException {
        final ArrayFloat months = new ArrayFloat(time.getShape());
        final Index index = months.getIndex();
        for (int month = 0; month < time.getShape(0); month++) {
            months.setFloat(index.set(month), (float) DEFAULT_MONTHS[month]);
        }
        writer.write(time, months);
    }
}
