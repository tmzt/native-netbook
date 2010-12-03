
package org.androix.nativenetbook;

import java.io.FileDescriptor;

public class NativeInit
{
	static {
		System.loadLibrary("nativeinitwrapper");
	}

	public static native FileDescriptor createProcess(
		int[] processId);

	public static native int waitFor(int processId);

	public static native void close(FileDescriptor fd);
}
