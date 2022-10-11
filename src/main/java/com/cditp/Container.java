package com.cditp;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;



public class Container {

    Map<Class<?>, Class<?>> diMap;
    Map<Class<?>, Object> applicationScope;

    private static Container container;

    private Container() {
		super();
		diMap = new HashMap<>();
		applicationScope = new HashMap<>();
	}

    public static void startApplication(Class<?> mainClass){
        try{
            synchronized (Container.class){
                if(container == null){
                    container = new Container();
                    container.initFramework(mainClass);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static <T> T getService(Class<T> classz){
        try{
            return container.getBeanInstance(classz);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void initFramework(Class<?> mainClass)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        List<Class<?>> classes = getClasses(mainClass.getPackage().getName());
        Collection<Class<?>> types = fetchWithComponentAnnoation(mainClass.getPackage().getName());
        for (Class<?> implementationClass : types){
            Class<?>[] interfaces = implementationClass.getInterfaces();
            if (interfaces.length == 0){
                diMap.put(implementationClass, implementationClass);
            } else {
                for (Class<?> ifaces : interfaces){
                    diMap.put(implementationClass, ifaces);
                }
            }
            

        }
        for (Class<?> classz : classes){
            if(classz.isAnnotationPresent(Component.class)){
                Object instance = classz.getDeclaredConstructor().newInstance();
                applicationScope.put(classz, instance);
                ContainerUtils.inject(this, classz, instance);
        }}
    }

    @SuppressWarnings("unchecked")
    private <T> T getBeanInstance(Class<?> interfaceClass) throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        return (T) getBeanInstance(interfaceClass, null, null);
    }

    public <T> Object getBeanInstance(Class<?> interfaceClass, String fieldName, String qualifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        Class<?> implementationClass = getImplimentationClass(interfaceClass, fieldName, qualifier);

		if (applicationScope.containsKey(implementationClass)) {
			return applicationScope.get(implementationClass);
		}
		synchronized (applicationScope) {
			Object service = implementationClass.getDeclaredConstructor().newInstance();
			applicationScope.put(implementationClass, service);
			return service;
		}
    }

    private Class<?> getImplimentationClass(Class<?> interfaceClass, final String fieldName, final String qualifier) {
		Set<Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
				.filter(entry -> entry.getValue() == interfaceClass).collect(Collectors.toSet());
		String errorMessage = "";
		if (implementationClasses == null || implementationClasses.size() == 0) {
			errorMessage = "no implementation found for interface " + interfaceClass.getName();
		} else if (implementationClasses.size() == 1) {
			Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
			if (optional.isPresent()) {
				return optional.get().getKey();
			}
		} else if (implementationClasses.size() > 1) {
			final String findBy = (qualifier == null || qualifier.trim().length() == 0) ? fieldName : qualifier;
			Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream()
					.filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy)).findAny();
			if (optional.isPresent()) {
				return optional.get().getKey();
			} else {
				errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
						+ " Expected single implementation or make use of @Qualifier to resolve conflict";
			}
		}
		throw new RuntimeErrorException(new Error(errorMessage));
	}


    public List<Class<?>> getClasses(String packageName){
        return (List<Class<?>>) fetchAllClasses(packageName).stream().collect(Collectors.toList());
    }

    public Set<Class<?>> fetchAllClasses(String packageName){
        InputStream inputStream = ClassLoader
            .getSystemClassLoader()
            .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.lines()
            .filter(line -> line.endsWith(".class"))
            .map(line -> getClass(line, packageName))
            .collect(Collectors.toSet());
    }

    private Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
              + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
                e.printStackTrace();
        }
        return null;
    }

    public Collection<Class<?>> fetchWithComponentAnnoation(String packageName){
        Set<Class<?>> classes = fetchAllClasses(packageName);
        return classes.stream().filter(classz -> 
            classz.isAnnotationPresent(Component.class)).collect(Collectors.toList());
       
    }
}
