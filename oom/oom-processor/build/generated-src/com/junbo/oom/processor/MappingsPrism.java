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
/** A Prism representing an {@code @com.junbo.oom.core.Mappings} annotation. 
  */ 
public class MappingsPrism {
    /** store prism value of value */
    private List<Mapping> _value;

    /**
      * An instance of the Values inner class whose
      * methods return the AnnotationValues used to build this prism. 
      * Primarily intended to support using Messager.
      */
    public final Values values;
    /** Return a prism representing the {@code @com.junbo.oom.core.Mappings} annotation on 'e'. 
      * similar to {@code e.getAnnotation(com.junbo.oom.core.Mappings.class)} except that 
      * an instance of this class rather than an instance of {@code com.junbo.oom.core.Mappings}
      * is returned.
      */
    public static MappingsPrism getInstanceOn(Element e) {
        AnnotationMirror m = getMirror("com.junbo.oom.core.Mappings",e);
        if(m == null) return null;
        return getInstance(m);
   }

    /** Return a prism of the {@code @com.junbo.oom.core.Mappings} annotation whose mirror is mirror. 
      */
    public static MappingsPrism getInstance(AnnotationMirror mirror) {
        return new MappingsPrism(mirror);
    }

    private MappingsPrism(AnnotationMirror mirror) {
        for(ExecutableElement key : mirror.getElementValues().keySet()) {
            memberValues.put(key.getSimpleName().toString(),mirror.getElementValues().get(key));
        }
        for(ExecutableElement member : ElementFilter.methodsIn(mirror.getAnnotationType().asElement().getEnclosedElements())) {
            defaults.put(member.getSimpleName().toString(),member.getDefaultValue());
        }
        List<AnnotationMirror> valueMirrors = getArrayValues("value",AnnotationMirror.class);
         _value = new ArrayList<Mapping>(valueMirrors.size());
        for(AnnotationMirror valueMirror : valueMirrors) {
            _value.add(Mapping.getInstance(valueMirror));
        }
        this.values = new Values(memberValues);
        this.mirror = mirror;
        this.isValid = valid;
    }

    /** 
      * Returns a List<Mapping> representing the value of the {@code value()} member of the Annotation.
      * @see com.junbo.oom.core.Mappings#value()
      */ 
    public List<Mapping> value() { return _value; }

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
      * A class whose members corespond to those of com.junbo.oom.core.Mappings
      * but which each return the AnnotationValue corresponding to
      * that member in the model of the annotations. Returns null for
      * defaulted members. Used for Messager, so default values are not useful.
      */
    public static class Values {
       private Map<String,AnnotationValue> values;
       private Values(Map<String,AnnotationValue> values) {
           this.values = values;
       }    
       /** Return the AnnotationValue corresponding to the value() 
         * member of the annotation, or null when the default value is implied.
         */
       public AnnotationValue value(){ return values.get("value");}
    }
    private Map<String,AnnotationValue> defaults = new HashMap<String,AnnotationValue>(10);
    private Map<String,AnnotationValue> memberValues = new HashMap<String,AnnotationValue>(10);
    private boolean valid = true;

    private <T> T getValue(String name, Class<T> clazz) {
        T result = MappingsPrism.getValue(memberValues,defaults,name,clazz);
        if(result == null) valid = false;
        return result;
    } 

    private <T> List<T> getArrayValues(String name, final Class<T> clazz) {
        List<T> result = MappingsPrism.getArrayValues(memberValues,defaults,name,clazz);
        if(result == null) valid = false;
        return result;
    }
    public static class Mapping {
        /** store prism value of source */
        private String _source;

        /** store prism value of target */
        private String _target;

        /** store prism value of excluded */
        private Boolean _excluded;

        /** store prism value of bidirectional */
        private Boolean _bidirectional;

        /** store prism value of explicitMethod */
        private String _explicitMethod;

        /**
          * An instance of the Values inner class whose
          * methods return the AnnotationValues used to build this prism. 
          * Primarily intended to support using Messager.
          */
        public final Values values;
        /** Return a prism of the {@code @com.junbo.oom.core.Mapping} annotation whose mirror is mirror. 
          */
        private static Mapping getInstance(AnnotationMirror mirror) {
            return new Mapping(mirror);
        }

        private Mapping(AnnotationMirror mirror) {
        for(ExecutableElement key : mirror.getElementValues().keySet()) {
            memberValues.put(key.getSimpleName().toString(),mirror.getElementValues().get(key));
        }
        for(ExecutableElement member : ElementFilter.methodsIn(mirror.getAnnotationType().asElement().getEnclosedElements())) {
            defaults.put(member.getSimpleName().toString(),member.getDefaultValue());
        }
            _source = getValue("source",String.class);
            _target = getValue("target",String.class);
            _excluded = getValue("excluded",Boolean.class);
            _bidirectional = getValue("bidirectional",Boolean.class);
            _explicitMethod = getValue("explicitMethod",String.class);
            this.values = new Values(memberValues);
            this.mirror = mirror;
            this.isValid = valid;
        }

        /** 
          * Returns a String representing the value of the {@code java.lang.String source()} member of the Annotation.
          * @see com.junbo.oom.core.Mapping#source()
          */ 
        public String source() { return _source; }

        /** 
          * Returns a String representing the value of the {@code java.lang.String target()} member of the Annotation.
          * @see com.junbo.oom.core.Mapping#target()
          */ 
        public String target() { return _target; }

        /** 
          * Returns a Boolean representing the value of the {@code boolean excluded()} member of the Annotation.
          * @see com.junbo.oom.core.Mapping#excluded()
          */ 
        public Boolean excluded() { return _excluded; }

        /** 
          * Returns a Boolean representing the value of the {@code boolean bidirectional()} member of the Annotation.
          * @see com.junbo.oom.core.Mapping#bidirectional()
          */ 
        public Boolean bidirectional() { return _bidirectional; }

        /** 
          * Returns a String representing the value of the {@code java.lang.String explicitMethod()} member of the Annotation.
          * @see com.junbo.oom.core.Mapping#explicitMethod()
          */ 
        public String explicitMethod() { return _explicitMethod; }

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
          * A class whose members corespond to those of com.junbo.oom.core.Mapping
          * but which each return the AnnotationValue corresponding to
          * that member in the model of the annotations. Returns null for
          * defaulted members. Used for Messager, so default values are not useful.
          */
        public static class Values {
           private Map<String,AnnotationValue> values;
           private Values(Map<String,AnnotationValue> values) {
               this.values = values;
           }    
           /** Return the AnnotationValue corresponding to the source() 
             * member of the annotation, or null when the default value is implied.
             */
           public AnnotationValue source(){ return values.get("source");}
           /** Return the AnnotationValue corresponding to the target() 
             * member of the annotation, or null when the default value is implied.
             */
           public AnnotationValue target(){ return values.get("target");}
           /** Return the AnnotationValue corresponding to the excluded() 
             * member of the annotation, or null when the default value is implied.
             */
           public AnnotationValue excluded(){ return values.get("excluded");}
           /** Return the AnnotationValue corresponding to the bidirectional() 
             * member of the annotation, or null when the default value is implied.
             */
           public AnnotationValue bidirectional(){ return values.get("bidirectional");}
           /** Return the AnnotationValue corresponding to the explicitMethod() 
             * member of the annotation, or null when the default value is implied.
             */
           public AnnotationValue explicitMethod(){ return values.get("explicitMethod");}
        }
        private Map<String,AnnotationValue> defaults = new HashMap<String,AnnotationValue>(10);
        private Map<String,AnnotationValue> memberValues = new HashMap<String,AnnotationValue>(10);
        private boolean valid = true;

        private <T> T getValue(String name, Class<T> clazz) {
            T result = MappingsPrism.getValue(memberValues,defaults,name,clazz);
            if(result == null) valid = false;
            return result;
        } 

        private <T> List<T> getArrayValues(String name, final Class<T> clazz) {
            List<T> result = MappingsPrism.getArrayValues(memberValues,defaults,name,clazz);
            if(result == null) valid = false;
            return result;
        }
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
