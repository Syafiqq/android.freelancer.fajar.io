#include <jni.h>
#include <string>

extern "C"
jstring
Java_io_localhost_freelancer_statushukum_controller_Dashboard_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
