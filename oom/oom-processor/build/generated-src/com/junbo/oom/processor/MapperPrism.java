package com.junbo.oom.processor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import net.java.dev.hickory.prism.internal.*;
import java.util.HashMap;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
/** A Prism representing an {@code @com.junbo.oom.core.Mapper} annotation. 
  */ 
public class MapperPrism {
    /** store prism value of uses */
    private List<TypeMirror> _uses;

    /**
      * An instance of the Values inner class whose
      * methods return the AnnotationValues used to build this prism. 
      * Primarily intended to support using Messager.
      */
    public final Values values;
    /** Return a prism representing the {@code @com.junbo.oom.core.Mapper} annotation on 'e'. 
      * similar to {@code e.getAnnotation(com.junbo.oom.core.Mapper.class)} except that 
      * an instance of this class rather than an instance of {@code com.junbo.oom.core.Mapper}
      * is returned.
      */
    public static MapperPrism getInstanceOn(Element e) {
        AnnotationMirror m = getMirror("com.junbo.oom.core.Mapper",e);
        if(m == null) return null;
        return getInstance(m);
   }

    /** Return a prism of the {@code @com.junbo.oom.core.Mapper} annotation whose mirror is mirror. 
      */
    public static MapperPrism getInstance(AnnotationMirror mirror) {
        return new MapperPrism(mirror);
    }

    private MapperPrism(AnnotationMirror mirror) {
        for(ExecutableElement key : mirror.getElementValues().keySet()) {
            memberValues.put(key.getSimpleName().toString(),mirror.getElementValues().get(key));
        }
        for(ExecutableElement member : ElementFilter.methodsIn(mirror.getAnnotationType().asElement().getEnclosedElements())) {
            defaults.put(member.getSimpleName().toString(),member.getDefaultValue());
        }
        _uses = getArrayValues("uses",TypeMirror.class);
        this.values = new Values(memberValues);
        this.mirror = mirror;
        this.isValid = valid;
    }

    /** 
      * Returns a List<TypeMirror> representing the value of the {@code uses()} member of the Annotation.
      * @see com.junbo.oom.core.Mapper#uses()
      */ 
    public List<TypeMirror> uses() { return _uses; }

    /**
      * Determine whether the underlying AnnotationMirror has no errors.
      * True if the underlying AnnotationMirror has no errors.
      * When true is returned, none of the methods will return null.
      * When false is returned, a least one member will either return null, or another
      * prism that is not valid.
      */
    public final boolean isValid;
    
    /**
      * The underlying AnnotationMirror of the annotation
      * represented by this Prism. 
      * Primarily intended to support using Messager.
      */
    public final AnnotationMirror mirror;
    /**
      * A class whose members corespond to those of com.junbo.oom.core.Mapper
      * but which each return the AnnotationValue corresponding to
      * that member in the model of the annotations. Returns null for
      * defaulted members. Used for Messager, so default values are not useful.
      */
    public static class Values {
       private Map<String,AnnotationValue> values;
       private Values(Map<String,AnnotationValue> values) {
           this.values = values;
       }    
       /** Return the AnnotationValue corresponding to the uses() 
         * member of the annotation, or null when the default value is implied.
         */
       public AnnotationValue uses(){ return values.get("uses");}
    }
    private Map<String,AnnotationValue> defaults = new HashMap<String,AnnotationValue>(10);
    private Map<String,AnnotationValue> memberValues = new HashMap<String,AnnotationValue>(10);
    private boolean valid = true;

    private <T> T getValue(String name, Class<T> clazz) {
        T result = MapperPrism.getValue(memberValues,defaults,name,clazz);
        if(result == null) valid = false;
        return result;
    } 

    private <T> List<T> getArrayValues(String name, final Class<T> clazz) {
        List<T> result = MapperPrism.getArrayValues(memberValues,defaults,name,clazz);
        if(result == null) valid = false;
        return result;
    }
    private static AnnotationMirror getMirror(String fqn, Element target) {
        for (AnnotationMirror m :target.getAnnotationMirrors()) {
            CharSequence mfqn = ((TypeElement)m.getAnnotationType().asElement()).getQualifiedName();
            if(fqn.contentEquals(mfqn)) return m;
        }
        return null;
    }
    private static <T> T getValue(Map<String,AnnotationValue> memberValues, Map<String,AnnotationValue> defaults, String name, Class<T> clazz) {
        AnnotationValue av = memberValues.get(name);
        if(av == null) av = defaults.get(name);
        if(av == null) {
            return null;
        }
        if(clazz.isInstance(av.getValue())) return clazz.cast(av.getValue());
        return null;
    }
    private static <T> List<T> getArrayValues(Map<String,AnnotationValue> memberValues, Map<String,AnnotationValue> defaults, String name, final Class<T> clazz) {
        AnnotationValue av = memberValues.get(name);
        if(av == null) av = defaults.get(name);
        if(av == null) {
            return null;
        }
        if(av.getValue() instanceof List) {
            List<T> result = new ArrayList<T>();
            for(AnnotationValue v : getValueAsList(av)) {
                if(clazz.isInstance(v.getValue())) {
                    result.add(clazz.cast(v.getValue()));
                } else{
                    return null;
                }
            }
            return result;
        } else {
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    private static List<AnnotationValue> getValueAsList(AnnotationValue av) {
        return (List<AnnotationValue>)av.getValue();
    }
}
