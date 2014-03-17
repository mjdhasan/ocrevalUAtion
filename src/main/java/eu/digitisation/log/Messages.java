/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 *
 * @author R.C.C.
 */
public class Messages {

    private static final Logger logger = Logger.getLogger("ApplicationLog");

    static {
        try {
            /**
            String path = Messages.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
                    */
            String path = System.getProperty("user.dir");
            String dir = new File(path).getParent();
            addFile(new File(dir, "ocrevaluation.log"));
            // for debugging
            addFrame();
        } catch (SecurityException ex) {
            Messages.info(Messages.class.getName() + ": " + ex);
        }

    }

    public static void addFile(File file) {
        try {
            FileHandler fh = new FileHandler(file.getAbsolutePath());
            fh.setFormatter(new LogFormatter());
            logger.addHandler(fh);
        } catch (IOException ex) {
            Messages.info(Messages.class.getName() + ": " + ex);
        }
    }

    public static void addFrame() {
        // Only while debugging
        LogFrameHandler lfh = new LogFrameHandler();
        lfh.setFormatter(new LogFormatter());
        logger.addHandler(lfh);
    }

    public static void info(String s) {
        logger.info(s);

    }

    public static void warning(String s) {
        logger.warning(s);
    }

    public static void severe(String s) {
        logger.severe(s);
    }
}
