package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;

public interface PayloadContainer {
    public byte[] getPayload() throws IOException;
}
