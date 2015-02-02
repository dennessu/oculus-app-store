package com.junbo.order.jobs.utils.csv

import com.Ostermiller.util.CSVPrinter

import java.util.concurrent.locks.Lock

/**
 * The CSVWriter could be used to write line concurrently.
 */
class ConcurrentCSVWriter implements CSVWriter {

    private final Lock lock;

    private final int bufferSize;

    private final File file;

    private final static DEFAULT_BUFFER_SIZE = 1000

    private List<List<String>> buffer = [] as List

    ConcurrentCSVWriter(File file, Lock lock, int bufferSize) {
        this.file = file
        this.lock = lock
        this.bufferSize = bufferSize
    }

    ConcurrentCSVWriter(File file, Lock lock) {
        this(file, lock, DEFAULT_BUFFER_SIZE)
    }

    @Override
    void writeRecords(List<List<String>> records) {
        buffer.addAll(records)
        if (buffer.size() > bufferSize) {
            flush()
        }
    }

    @Override
    void close() {
        flush()
    }

    private void flush() {
        if (lock != null) {
            lock.lock()
        }
        try {
            CSVPrinter writer = null
            try {
                writer = new CSVPrinter(new FileOutputStream(file, true));
                writer.lineEnding = '\n'
                buffer.each { List<String> row ->
                    writer.writeln(row.toArray(new String[0]))
                }
                buffer.clear()
            } finally {
                if (writer != null) {
                    writer.close()
                }
            }
        } finally {
            if (lock != null) {
                lock.unlock()
            }
        }
    }
}
