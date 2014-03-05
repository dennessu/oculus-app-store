package com.goshop.langur.processor.test;

import com.goshop.langur.processor.MapperTestBase;
import com.goshop.langur.processor.WithClasses;
import org.testng.annotations.Test;

@WithClasses({
        CategoryResource.class
})
public class CompileTest extends MapperTestBase {

    @Test
    public void testCompile() {
    }
}
