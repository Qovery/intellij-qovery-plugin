package com.qovery.intellij.plugin.completion;

import com.qovery.intellij.plugin.completion.domain.QoverySpec;
import com.qovery.intellij.plugin.completion.field.ArrayField;
import com.qovery.intellij.plugin.completion.field.Field;
import com.qovery.intellij.plugin.completion.field.ObjectField;
import com.qovery.intellij.plugin.completion.field.StringField;
import com.qovery.intellij.plugin.completion.value.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.Stream;

public class Spec {

    final protected Map<String, List<Field>> fields = new HashMap<>();
    final protected Map<String, List<ValueSet>> values = new HashMap<>();

    public Spec() {
        initSpec();
    }

    public void initSpec() {
        String currentPath = "$.";
        for (java.lang.reflect.Field field : QoverySpec.class.getFields()) {
            handleField(currentPath, field).forEach(f -> {
                fields.computeIfAbsent(f.getPath(), (x) -> new ArrayList<>());
                fields.get(f.getPath()).add(f);
            });

            handleValue(currentPath, field).forEach(valueSet -> {
                values.computeIfAbsent(valueSet.getPath(), (x) -> new ArrayList<>());
                values.get(valueSet.getPath()).add(valueSet);
            });
        }
    }

    private List<Field> handleField(String path, java.lang.reflect.Field field) {
        List<Field> fieldList = new ArrayList<>();

        String fieldName = getKeyNameOfField(field);
        final String nextPath = path.endsWith(".") ? path + fieldName : path + "." + fieldName;
        boolean isRequired = getAnnotationOpt(field, KeyRequired.class).isPresent();

        switch (field.getType().getSimpleName()) {
            case "String":
            case "long":
            case "double":
            case "int":
            case "boolean":
                fieldList.add(new StringField(fieldName, isRequired, path));
                break;
            case "List":
                fieldList.add(new ArrayField(fieldName, isRequired, path));
                Optional<ListOf> listOfAnnotation = getAnnotationOpt(field, ListOf.class);
                if (listOfAnnotation.isPresent()) {
                    for (java.lang.reflect.Field typedListField : listOfAnnotation.get().value().getFields()) {
                        fieldList.addAll(handleField(nextPath, typedListField));
                    }
                }
                break;
            default:
                List<Field> nextFieldList = new ArrayList<>();
                List<Field> children = new ArrayList<>();

                java.lang.reflect.Field[] fields = field.getType().getFields();
                for (java.lang.reflect.Field fieldOfObject : fields) {
                    List<Field> fieldsFromNext = handleField(nextPath, fieldOfObject);
                    Field currentField = fieldsFromNext.get(0);
                    children.add(currentField);
                    nextFieldList.addAll(fieldsFromNext);
                }

                fieldList.add(new ObjectField(fieldName, isRequired, path, children));
                fieldList.addAll(nextFieldList);
        }

        return fieldList;
    }

    private List<ValueSet> handleValue(String path, java.lang.reflect.Field field) {
        List<ValueSet> valueList = new ArrayList<>();

        String fieldName = getKeyNameOfField(field);
        final String nextPath = path.endsWith(".") ? path + fieldName : path + "." + fieldName;

        switch (field.getType().getSimpleName()) {
            case "String":
            case "long":
            case "double":
            case "int":
                valueList.addAll(getValues(nextPath, field));
                break;
            case "boolean":
                valueList.add(new BooleanValueSet(nextPath));
                break;
            case "List":
                Optional<ListOf> listOfAnnotation = getAnnotationOpt(field, ListOf.class);
                if (listOfAnnotation.isPresent()) {
                    for (java.lang.reflect.Field typedListField : listOfAnnotation.get().value().getFields()) {
                        valueList.addAll(handleValue(nextPath, typedListField));
                    }
                }
                break;
            default:
                java.lang.reflect.Field[] fields = field.getType().getFields();
                for (java.lang.reflect.Field fieldOfObject : fields) {
                    valueList.addAll(handleValue(nextPath, fieldOfObject));
                }
        }

        return valueList;
    }

    private List<ValueSet> getValues(String path, java.lang.reflect.Field field) {
        List<ValueSet> valueSetList = new ArrayList<>();

        getAnnotationStream(field, ValueList.class).forEach(values -> {
            Set<String> proposals = new HashSet<>();
            Collections.addAll(proposals, values.value());
            valueSetList.add(new ValueSet(proposals, path));
        });

        getAnnotationOpt(field, ValueProject.class)
                .ifPresent(n -> valueSetList.add(new ValueSet(new ContextualProject(), path)));

        getAnnotationOpt(field, ValueFileEndWith.class)
                .ifPresent(n -> valueSetList.add(new ValueSet(new ContextualFileExtension(n.value()), path)));

        getAnnotationOpt(field, ValueDatabaseVersion.class)
                .ifPresent(n -> valueSetList.add(new ValueSet(new ContextualDatabaseVersion(), path)));

        return valueSetList;
    }

    private String getKeyNameOfField(java.lang.reflect.Field field) {
        return getAnnotationOpt(field, KeyName.class).map(KeyName::value).orElse(field.getName());
    }

    private <T extends Annotation> Stream<T> getAnnotationStream(java.lang.reflect.Field field, Class<T> cl) {
        return Arrays.stream(field.getAnnotationsByType(cl));
    }

    private <T extends Annotation> Optional<T> getAnnotationOpt(java.lang.reflect.Field field, Class<T> cl) {
        return Arrays.stream(field.getAnnotationsByType(cl)).findFirst();
    }


    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ListOf {
        Class value();
    }


    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface KeyName {
        String value() default "";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface KeyRequired {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValueFileEndWith {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValueProject {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValueDatabaseVersion {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValueList {
        String[] value();
    }

}
