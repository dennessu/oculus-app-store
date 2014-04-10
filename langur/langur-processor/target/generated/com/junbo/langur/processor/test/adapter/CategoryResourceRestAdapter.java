// CHECKSTYLE:OFF
package com.junbo.langur.processor.test.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("categories")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class CategoryResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultCategoryResource")
    private com.junbo.langur.processor.test.CategoryResource __adaptee;

    public com.junbo.langur.processor.test.CategoryResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.langur.processor.test.CategoryResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getCategories(
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> future;
    
        try {
            future = __adaptee.getCategories(
                getOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category> result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{categoryId}")
    public void getCategory(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.category.Category> future;
    
        try {
            future = __adaptee.getCategory(
                categoryId,
                getOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.category.Category>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.category.Category result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{categoryId}/draft")
    public void getCategoryDraft(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.category.Category> future;
    
        try {
            future = __adaptee.getCategoryDraft(
                categoryId,
                getOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.category.Category>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.category.Category result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{categoryId}/children")
    public void getCategoryChildren(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> future;
    
        try {
            future = __adaptee.getCategoryChildren(
                categoryId,
                getOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category> result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{categoryId}/descendents")
    public void getCategoryDescendents(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> future;
    
        try {
            future = __adaptee.getCategoryDescendents(
                categoryId,
                getOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category> result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{categoryId}/parents")
    public void getCategoryParents(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> future;
    
        try {
            future = __adaptee.getCategoryParents(
                categoryId,
                getOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category> result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{categoryId}/hists")
    public void getCategoryHists(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> future;
    
        try {
            future = __adaptee.getCategoryHists(
                categoryId,
                getOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category> result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.POST
    public void createCategory(
    com.junbo.langur.processor.model.category.Category category,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.category.Category> future;
    
        try {
            future = __adaptee.createCategory(
                category,
                postOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.category.Category>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.category.Category result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{categoryId}")
    public void updateCategory(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    com.junbo.langur.processor.model.category.Category category,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.category.Category> future;
    
        try {
            future = __adaptee.updateCategory(
                categoryId,
                category,
                postOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.category.Category>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.category.Category result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{categoryId}/release")
    public void releaseCategory(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.category.Category> future;
    
        try {
            future = __adaptee.releaseCategory(
                categoryId,
                postOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.category.Category>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.category.Category result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{categoryId}/delete")
    public void deleteCategory(
    @javax.ws.rs.PathParam("categoryId") java.lang.String categoryId,
    
    @javax.ws.rs.BeanParam com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.langur.processor.model.category.Category> future;
    
        try {
            future = __adaptee.deleteCategory(
                categoryId,
                postOptions
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.langur.processor.model.category.Category>() {
            @Override
            public void invoke(com.junbo.langur.processor.model.category.Category result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
}
