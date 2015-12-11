package com.github.jhpoelen.fbob;

import java.io.IOException;
import java.io.OutputStream;

interface StreamFactory {
    OutputStream outputStreamFor(String name) throws IOException;
}
