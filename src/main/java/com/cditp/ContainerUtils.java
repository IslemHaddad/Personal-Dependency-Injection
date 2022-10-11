package com.cditp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ContainerUtils {
    
    private ContainerUtils (){
        super();
    }

    public static void inject(Container container,Class<?> classz, Object instance )
        throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
    {
        Field[] fields = classz.getDeclaredFields();
        for(Field field : fields){
            if (field.isAnnotationPresent(Inject.class)){
                    String qualifier = field.isAnnotationPresent(Qualifier.class) ? field.getAnnotation(Qualifier.class).value() : null;
                    Object fieldInstance = container.getBeanInstance(field.getType(), field.getName(), qualifier);
                    field.setAccessible(true);
                    field.set(instance,fieldInstance);
                    inject(container, fieldInstance.getClass(), fieldInstance);
                }
            }
        };
    
}
