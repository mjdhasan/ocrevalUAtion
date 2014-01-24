/*
 * Copyright (C) 2013 Universidad de Alicante
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
package eu.digitisation.langutils;

import eu.digitisation.io.CharFilter;
import eu.digitisation.io.StringNormalizer;
import eu.digitisation.io.TextContent;
import eu.digitisation.io.WarningException;
import eu.digitisation.io.WordScanner;
import eu.digitisation.math.Counter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compute term frequencies in a collection
 *
 * @author R.C.C
 */
public class TermFrequency extends Counter<String> {

    private static final long serialVersionUID = 1L;
    private CharFilter filter;

    /**
     * Default constructor
     */
    public TermFrequency() {
        filter = null;
    }

    /**
     * Basic constructor
     *
     * @param filter a CharFilter implementing character equivalences
     */
    public TermFrequency(CharFilter filter) {
        this.filter = filter;
    }

    /**
     * Add CharFilter
     *
     * @param file a CSV file with character equivalences
     */
    public void addFilter(File file) {
        if (filter == null) {
            filter = new CharFilter(file);
        } else {
            filter.addFilter(file);
        }
    }

    /**
     * Extract words from a file
     *
     * @param dir the input file or directory
     */
    public void add(File dir) throws WarningException {
        if (dir.isDirectory()) {
            addFiles(dir.listFiles());
        } else {
            File[] files = {dir};
            addFiles(files);
        }
    }

    /**
     * Extract words from a file
     *
     * @param file an input files
     */
    public void addFile(File file) throws WarningException {
        try {
            TextContent content = new TextContent(file, filter);
            WordScanner scanner = new WordScanner(content.toString());
            String word;
            while ((word = scanner.nextWord()) != null) {
                String filtered = (filter == null)
                        ? word : filter.translate(word);
                inc(StringNormalizer.composed(filtered));
            }
        } catch (IOException ex) {
            Logger.getLogger(TermFrequency.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Extract words from a file
     *
     * @param files an array of input files
     */
    private void addFiles(File[] files) throws WarningException {
        for (File file : files) {
            addFile(file);
        }
    }

    /**
     *
     * @param other another term frequency vector
     * @return the cosine distance (normalized scalar product)
     */
    public double cosine(TermFrequency other) {
        double norm1 = 0;
        double norm2 = 0;
        double scalar = 0;

        for (Map.Entry<String, Integer> entry : this.entrySet()) {
            int freq = entry.getValue();
            norm1 += freq * freq;
            scalar += freq * other.get(entry.getKey());
        }

        for (int freq2 : other.values()) {
            norm2 += freq2 * freq2;
        }

        return scalar / Math.sqrt(norm1 * norm2);
    }

    /**
     *
     * @param other another term frequency vector
     * @return the recall provided by this term frequency vector (rate of words
     * in the other TF matching one in this TF)
     */
    public double recall(TermFrequency other) {
        int total = 0;
        int matched = 0;
       

        for (Map.Entry<String, Integer> entry : other.entrySet()) {
            total += entry.getValue();
            if (this.containsKey(entry.getKey())) {
                ++matched;
            }
        }

        return matched / (double)total;
    }

    /**
     * String representation
     *
     * @param order the criteria to sort words
     * @return String representation
     */
    public String toString(Order order) {
        StringBuilder builder = new StringBuilder();
        for (String word : this.keyList(order)) {
            builder.append(word).append(' ')
                    .append(get(word)).append('\n');
        }
        return builder.toString();
    }

    /**
     * Main function
     *
     * @param args see help
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: TermFrequency [-e equivalences_file] [-c] input_files_or_directories");
        } else {
            TermFrequency tf = new TermFrequency();
            List<File> files = new ArrayList<File>();
            CharFilter filter = new CharFilter();
            for (int n = 0; n < args.length; ++n) {
                if (args[n].equals("-e")) {
                    tf.addFilter(new File(args[++n]));
                } else if (args[n].equals("-c")) {
                    filter.setCompatibility(true);
                } else {
                    files.add(new File(args[n]));
                }
            }
            for (File file : files) {
                tf.add(file);
            }
            System.out.println(tf.toString(Order.DESCENDING_VALUE));
        }
    }
}
