package fr.ird.osmose.web.api;

import java.io.IOException;
import java.io.OutputStream;

interface StreamFactory {
    OutputStream outputStreamFor(String name) throws IOException;
}
