#ifndef fastwebscrape_H
#define fastwebscrape_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

// Export JNI declarations for fastwebscrapeImpl

JNIEXPORT jstring JNICALL Java_fastwebscrape_FastWebScrapeImpl_nativeExtractReadableText(
    JNIEnv* env, jobject obj, jbyteArray htmlData);

JNIEXPORT jobjectArray JNICALL Java_fastwebscrape_FastWebScrapeImpl_nativeExtractLinks(
    JNIEnv* env, jobject obj, jbyteArray htmlData);

JNIEXPORT jobjectArray JNICALL Java_fastwebscrape_FastWebScrapeImpl_nativeExtractByTag(
    JNIEnv* env, jobject obj, jbyteArray htmlData, jstring tagName);

JNIEXPORT jstring JNICALL Java_fastwebscrape_FastWebScrapeImpl_nativeExtractJsonLD(
    JNIEnv* env, jobject obj, jbyteArray htmlData);

#ifdef __cplusplus
}
#endif

#endif // fastwebscrape_H
