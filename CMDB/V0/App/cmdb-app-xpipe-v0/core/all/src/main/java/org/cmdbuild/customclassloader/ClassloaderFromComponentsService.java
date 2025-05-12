package org.cmdbuild.customclassloader;

import javax.annotation.Nullable;

public interface ClassloaderFromComponentsService {

    @Nullable
    ClassLoaderHelper getClassLoaderFromComponentOrNull(String code);

    interface ClassLoaderHelper {

        ClassLoader getClassLoader(ClassLoader parent);
    }
}
