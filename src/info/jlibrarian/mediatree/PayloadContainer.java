package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;

public interface PayloadContainer {
    public byte[] getPayload() throws IOException;
}
