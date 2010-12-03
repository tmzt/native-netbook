
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# This is the target being built.
LOCAL_MODULE:= libnativeinitwrapper

# All of the source files that we will compile.
LOCAL_SRC_FILES:= \
  nativeinitwrapper.c

LOCAL_LDLIBS := -ldl -llog

include $(BUILD_SHARED_LIBRARY)
