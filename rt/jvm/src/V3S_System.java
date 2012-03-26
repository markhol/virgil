// Copyright 2008 Google Inc. All rights reserved.
// See LICENSE for details of Apache 2.0 license.

import java.io.*;

/**
 * The <code>V3S_System</code> class implements system calls for Virgil III programs compiled
 * to Java bytecode.
 */
public class V3S_System {
    private static final int PRINT_SIZE = 128;
    private static final int MAX_FILES = 128;

    public static final FileInputStream[] fileInput;
    public static final FileOutputStream[] fileOutput;

    static {
        fileInput = new FileInputStream[MAX_FILES];
        fileOutput = new FileOutputStream[MAX_FILES];
    }

    public static void putc(byte ch) {
        System.out.print((char) ch);
    }

    public static void puts(byte[] str) {
        System.out.write(str, 0, str.length);
    }

    public static void puti(int i) {
        System.out.print(i);
    }

    public static void ln() {
        System.out.print('\n');
    }

    public static byte fileRead(int fd) throws IOException {
	InputStream in = getFileInput(fd);
	return in == null ? 0 : (byte) in.read();
    }

    public static void fileWriteK(int fd, byte[] b, int offset, int length) throws IOException {
	OutputStream out = getFileOutput(fd);
        if (out != null) out.write(b, offset, length);
    }

    public static int fileOpen(byte[] name, boolean input) {
        for (int i = 0; i < fileInput.length; i++) {
            if (fileInput[i] == null && fileOutput[i] == null) {
                File file = new File(new String(name));
                try {
                    if (input) fileInput[i] = new FileInputStream(file);
                    else fileOutput[i] = new FileOutputStream(file);
                    return i;
                } catch (Throwable t) {
                    return -1;
                }
            }
        }
        return -1;
    }

    public static int fileLeft(int fd) throws IOException {
	InputStream in = getFileInput(fd);
        if (in != null) return in.available();
	return 0;
    }

    public static void fileClose(int fd) {
        if (fd < 0) return;
        if (fd >= fileInput.length) return;
        try {
            if (fileInput[fd] != null) fileInput[fd].close();
            if (fileOutput[fd] != null) fileOutput[fd].close();
            fileInput[fd] = null;
            fileOutput[fd] = null;
        } catch (IOException e) {
            // do nothing.
        }
    }

    public static byte[] fileLoad(byte[] fname) {
        try {
            File file = new File(new String(fname));
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            int pos = 0;
            while (pos < buffer.length) {
                pos += fis.read(buffer, pos, buffer.length - pos);
            }
            fis.close();
            return buffer;
        } catch (IOException e) {
            return null;
        }
    }

    public static int ticksMs() {
        return (int) System.currentTimeMillis();
    }

    public static int ticksUs() {
        return (int) (System.nanoTime() / 1000);
    }

    public static int ticksNs() {
        return (int) System.nanoTime();
    }

    public static void chmod(byte[] fname, int mode) {
        String fileName = new String(fname);
        String modeString = Integer.toString(mode, 8);
	try {
            Runtime.getRuntime().exec(new String[] {"chmod", modeString, fileName}).waitFor();
        } catch (Throwable t) {
	    // do nothing.
	}
    }

    public static int exec(byte[][] args) throws Exception {
        String[] a = new String[args.length];
        for (int i = 0; i < args.length; i++) {
	    a[i] = new String(args[i]);
	}
	//        try {
	    return Runtime.getRuntime().exec(a).waitFor();
	    //        } catch (Throwable t) {
	    //            return -1;
	    //	}
    }

    private static InputStream getFileInput(int fd) {
	return (fd < 0 || fd > fileInput.length) ? null : fileInput[fd];
    }

    private static OutputStream getFileOutput(int fd) {
	return (fd < 0 || fd > fileOutput.length) ? null : fileOutput[fd];
    }

    public static void error(byte[] type, byte[] message) throws Exception {
        throw new Exception(new String(type) + ": " + new String(message));
    }

    public static boolean equals(Object a, Object b) {
        return a == b || (a == null ? b.equals(a) : a.equals(b));
    }

    public static int shl(int a, int b) {
	if ((b & 31) == b) return a << b;
	return 0;
    }

    public static int shr(int a, int b) {
	if ((b & 31) == b) return a >>> b;
	return 0;
    }
}
