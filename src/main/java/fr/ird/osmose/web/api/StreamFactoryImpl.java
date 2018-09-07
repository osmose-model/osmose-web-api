package fr.ird.osmose.web.api;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StreamFactoryImpl implements StreamFactory {

    private String baseDir = "target/";

    @Override
    public OutputStream outputStreamFor(String name) throws IOException {
        return new FileOutputStream(baseDir + name);
    }
}
