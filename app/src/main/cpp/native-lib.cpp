#include <jni.h>
#include <string>

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_timerapplication_StopwatchActivity_convertToMinutesAndSeconds(
        JNIEnv *env,
        jobject, jint count) {

    jintArray result;
    result = env->NewIntArray(2);
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    jint minutes = count/60;
    jint seconds = count%60;

    jint minutesAndSeconds[2];
    minutesAndSeconds[0] = minutes;
    minutesAndSeconds[1] = seconds;

    env->SetIntArrayRegion(result, 0, 2, minutesAndSeconds);

    return result;
}
