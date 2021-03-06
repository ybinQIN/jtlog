package com.jeramtough.jtlog.recorder;

import com.jeramtough.jtlog.bean.LogInformation;

import java.io.*;
import java.util.concurrent.*;

/**
 * Created on 2019-02-09 09:37
 * by @author JeramTough
 */
public class FileLogRecorder implements LogRecorder {

    private File logFile;
    private PrintWriter printWriter;
    private ExecutorService executorService;

    public FileLogRecorder(File logFile) {
        this.logFile = logFile;
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("FileLogRecorder-" + thread.getId());
                return thread;
            }
        };
        executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), threadFactory);

        init();
    }

    protected void init() {
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void record(LogInformation logInformation, String stylizedText) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(logFile, true);
                    printWriter = new PrintWriter(fileWriter);
                    printWriter.println(stylizedText);
                    printWriter.flush();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (printWriter != null) {

                        printWriter.close();
                    }
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
