
#include <jni.h>
#include <android/log.h>

#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/wait.h>
#include <stdio.h>
#include <errno.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <termios.h>

int createProcess(int *ppid)
{
	char *devname;
	int ptm, pts;
	pid_t pid;
	int res;

	ptm = open("/dev/ptmx", O_RDWR);
	fcntl(ptm, F_SETFD, FD_CLOEXEC);

	if(grantpt(ptm) || unlockpt(ptm) || ((devname = (char*)ptsname(ptm)) == 0)) {
		return -1;
	}

	pid = fork();

	if (pid < 0) return -1;

	if (pid) {
		*ppid = pid;
		return ptm;
	} else {
		/* child */
		close(ptm);
		setsid();
		pts = open(devname, O_RDWR);
		if (pts < 0) exit(-1);
		dup2(pts, 0);
		dup2(pts, 1);
		dup2(pts, 2);
		//execl("./init", "./init", "/native", NULL);
		//res = execve("/system/bin/su", "su");
		//res = execve("/bin/su", "su");

		printf("in child\n");
		execlp("ls", "ls", "/", NULL);
		//execl("su", "-c", "./init", "/native", NULL);

		exit(-1);
	};
};

jobject Java_org_androix_nativenetbook_NativeInit_createProcess(JNIEnv *env, jobject cls, jintArray pidArray)
{
	int ptm;
	int pid = -1;
	int pidLen;
	int *ppid = NULL;
	jboolean copy;

	jclass FileDescriptor_class;
	jmethodID FileDescriptor_init;
	jfieldID FileDescriptor_descriptor;

	jobject filedescriptor;	

	FileDescriptor_class = (*env)->FindClass(env, "java/io/FileDescriptor");
	FileDescriptor_descriptor = (*env)->GetFieldID(env, FileDescriptor_class, "descriptor", "I");
	FileDescriptor_init = (*env)->GetMethodID(env, FileDescriptor_class, "<init>", "()V");

	ptm = createProcess(&pid);
	
	if (pidArray) {
		pidLen = (*env)->GetArrayLength(env, pidArray);
		if (pidLen > 0) {
			ppid = (int *) (*env)->GetPrimitiveArrayCritical(env, pidArray, &copy);
			if (ppid) {
				*ppid = pid;
				(*env)->ReleasePrimitiveArrayCritical(env, pidArray, ppid, 0);
			}
		}
	}

	filedescriptor = (*env)->NewObject(env, FileDescriptor_class, FileDescriptor_init);
	if (filedescriptor) {
		(*env)->SetIntField(env, filedescriptor, FileDescriptor_descriptor, ptm);
	} 

	return filedescriptor;	
}

jint Java_org_androix_nativenetbook_NativeInit_waitFor(JNIEnv *env, jobject cls, int pid)
{
	int status;
	int res;

	waitpid(pid, &status, 0);
	res = 0;
	if (WIFEXITED(status)) res = WEXITSTATUS(status);
	return res;	
};

void Java_org_androix_nativenetbook_NativeInit_close(int fd) {
	close(fd);
};



